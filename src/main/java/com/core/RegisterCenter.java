package com.core;

import com.annotations.RPCprovider;
import com.core.aop.MethodIntecept;
import com.core.remoteModules.RemoteFactory;
import com.core.remoteModules.RemoteInterfaceModule;
import com.core.zookeeper.ZookeeperRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author <a href="mailto:ruancl@59store.com">软软</a>
 * @version 1.0 16/7/14
 * @since 1.0   服务注册与发现中心
 *
 */
@Service
public class RegisterCenter {

    @Autowired
    private RemoteFactory remoteFactory;
    @Autowired
    private ZookeeperRegistry zookeeperRegistry;



    public void register(Object bean,RPCprovider rpCprovider){
        if(bean==null || rpCprovider==null){
            throw new IllegalArgumentException();
        }
        Class interfaceClass = rpCprovider.interfaceClass();
        //注册服务
        String url = remoteFactory.getRemoteModule(rpCprovider.remoteType()).register(bean,interfaceClass);
        //写入zookeeper
        zookeeperRegistry.doRegister(Tools.serviceNameCreate(interfaceClass),url);
    }

    public Object proxy(Object bean) {
        Class[] interfaceClasss = bean.getClass().getInterfaces();
        if(interfaceClasss.length<=0){
            throw new NullPointerException("远程服务必须要实现一个接口啊兄弟!");
        }
        Class interfaceClass = interfaceClasss[0];

        //为该接口刷新路由表
        zookeeperRegistry.routTableloadByKey(interfaceClass);
        //为返回对象做动态代理
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(interfaceClass);
        enhancer.setCallback(new MethodIntecept(zookeeperRegistry,interfaceClass));
        return enhancer.create();
    }


}
