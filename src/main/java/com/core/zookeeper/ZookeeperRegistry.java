package com.core.zookeeper;

import com.core.Tools;
import com.core.remoteModules.RemoteInterfaceModule;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author <a href="mailto:ruancl@59store.com">软软</a>
 * @version 1.0 16/7/14    zookeeper地址管理
 * @since 1.0
 */
@Service
public class ZookeeperRegistry {

    private Logger logger = LoggerFactory.getLogger(ZookeeperRegistry.class);

    private ZkClient zkClient;

    private final String ZOOKEEPER_URL = "127.0.0.1:2181";

    private final String ROOT = "/rpc";

    private final String KEY = "provider";

    private Lock                 lock      = new ReentrantLock();

    @Autowired
    private RemoteInterfaceModule rmiModel;
    /**
     * 远程对象路由表
     * 接口对象-->实际远程对象列表   本地服务扫描到需要引用远程服务时候开始往map里面写数据
     *
     * 此处涉及到多线程的读取    应该没有并发问题
     * 主要是写入的时候,真正发生并发就是 接口发现坏点 重载的时候会发生写入,那么此时进行上锁应该就可以解决并发问题了
     */
    private Map<Class,ArrayList> routTable = new HashMap<Class, ArrayList>();


    @PostConstruct
    public void zkInit(){
        zkClient = new ZkClient(ZOOKEEPER_URL);
    }

    /**
     * clazz
     * 根据接口  来为路由表里面某个接口的列表进行刷新   线程安全   写入时候加锁
     */
    public void routTableReloadByKey(Class clazz){
        if(clazz==null){
            throw new IllegalAccessError();
        }

        ArrayList<Object> objects = listObjectsByClazz(clazz);
        lock.lock();
        try {
            routTable.put(clazz,objects);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    /**
     * 初始化时候使用  无需加锁 没有竞争
     */
    public void routTableloadByKey(final Class clazz){
        if(clazz==null){
            throw new IllegalAccessError();
        }
        if(!routTable.containsKey(clazz)){
            routTable.put(clazz,listObjectsByClazz(clazz));
            //对接口节点进行监听 如果节点发生改变重载路由表
            zkClient.subscribeChildChanges(String.format("%s/%s", ROOT, Tools.serviceNameCreate(clazz)), new IZkChildListener() {
                @Override
                public void handleChildChange(String s, List<String> list) throws Exception {
                    routTableReloadByKey(clazz);
                }
            });
        }

    }


    /**
     * 根据传入接口类型  返回可用接口列表
     * @param clazz
     * @return
     */
    public ArrayList listObjectsByClazz(Class clazz){
        if(clazz==null){
            throw new IllegalAccessError();
        }
        String serviceName = Tools.serviceNameCreate(clazz);
        String serviceNode = String.format("%s/%s",ROOT,serviceName);
        if(!zkClient.exists(serviceNode)){
            throw new NullPointerException("没有该接口的服务可以提供!");
        }
        List<String> servers = zkClient.getChildren(serviceNode);
        if(servers==null || servers.size()==0){
            throw new NullPointerException("没有该接口的服务可以提供!");
        }
        ArrayList objects = new ArrayList();
        for(String server : servers){
            String url = zkClient.readData(String.format("%s/%s",serviceNode,server));
            objects.add(rmiModel.convertObjectByUrl(url,clazz));
        }
        return objects;
    }



    /**
     *
     * @param serviceName
     * @param url
     *
     * 注册服务地址
     */
    public void doRegister(String serviceName,String url){
        if(StringUtils.isEmpty(serviceName) || StringUtils.isEmpty(url)){
            throw new IllegalAccessError();
        }
        //判断根节点是否存在
        if(!zkClient.exists(ROOT)){
            zkClient.createPersistent(ROOT);
        }
        //判断service节点
        String serviceNode = String.format("%s/%s",ROOT,serviceName);
        if(!zkClient.exists(serviceNode)){
            zkClient.createPersistent(serviceNode);
        }
        //创建一个临时顺序节点
        zkClient.createEphemeralSequential(String.format("%s/%s",serviceNode,KEY),url.getBytes());
        logger.info("服务地址注册:{}",url);
    }

    /**
     *
     * @param clazz
     *
     * 服务查找
     *
     * 目前只做均衡负载   以后扩展权重方案
     *
     */
    public Object lookup(Class clazz){
        if(clazz==null){
            throw new IllegalAccessError();
        }

        if(!routTable.containsKey(clazz)){
            throw new NullPointerException("没有该接口的服务可以提供!");
        }
        ArrayList objects = routTable.get(clazz);
        int size = objects.size();
        if(size>0){
            //通过随机数负载均衡 雨露均沾!
            return objects.get((int)(Math.random()*size));
        }else {
            throw new NullPointerException("暂无可用接口服务!");
        }
    }


    /**
     * 接口重载  并返回一个
     * @param clazz
     * @return
     */
    public Object reloadInterfacesAndGetOne(Class clazz) {
        if(clazz==null){
            throw new IllegalAccessError();
        }
        routTableReloadByKey(clazz);
        return lookup(clazz);
    }

}
