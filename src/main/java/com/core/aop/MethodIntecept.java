package com.core.aop;

import com.core.zookeeper.ZookeeperRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.remoting.RemoteLookupFailureException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author <a href="mailto:ruancl@59store.com">软软</a>
 * @version 1.0 16/7/15
 * @since 1.0
 */
public class MethodIntecept implements MethodInterceptor {

    private  final Logger logger = LoggerFactory.getLogger("远程服务代理:");

    private ZookeeperRegistry zookeeperRegistry;

    private Class interfaceClass;

    public MethodIntecept(ZookeeperRegistry zookeeperRegistry, Class interfaceClass) {

        this.zookeeperRegistry = zookeeperRegistry;

        this.interfaceClass = interfaceClass;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {

        Object remoteObject = zookeeperRegistry.lookup(interfaceClass);
        if(remoteObject==null){
            logger.info("暂无可用远程接口提供服务");
            return "暂无可用远程接口提供服务";
        }
        Object result = null;
        try {
            result = method.invoke(remoteObject,objects);
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof RemoteLookupFailureException) {
                remoteObject = zookeeperRegistry.reloadInterfacesAndGetOne(interfaceClass);//重新刷新路由表 并取得接口
                if(remoteObject==null){
                    logger.info("暂无可用远程接口提供服务");
                    return "暂无可用远程接口提供服务";
                }
                try {
                    result =  method.invoke(remoteObject,objects);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            } else {
                e.printStackTrace();
            }
        }
        return result;
    }
}
