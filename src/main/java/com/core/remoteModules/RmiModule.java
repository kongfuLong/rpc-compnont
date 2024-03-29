package com.core.remoteModules;

import com.core.Tools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;
import org.springframework.remoting.rmi.RmiServiceExporter;
import org.springframework.stereotype.Service;

import java.rmi.RemoteException;

/**
 * @author <a href="mailto:ruancl@59store.com">软软</a>
 * @version 1.0 16/7/14
 * @since 1.0
 */
@Service
public class RmiModule implements RemoteInterfaceModule {


    private final int dataPort = 7777;//数据端口

    private final int vistPort = 8899;//访问端口

    public String serviceNameCreate(Class clazz){
        return clazz.getName().replace(".","_");
    }

    /**
     *
     * @param bean
     * @param interfaceClass
     * @return 接口注册 并返回url
     */
    @Override
    public String register(Object bean,Class interfaceClass) {
        String url = null;
        RmiServiceExporter rmiServiceExporter = new RmiServiceExporter();
        try {
            //服务发布
            rmiServiceExporter.setServiceName(Tools.serviceNameCreate(interfaceClass));
            rmiServiceExporter.setServiceInterface(interfaceClass);
            rmiServiceExporter.setService(bean);
            rmiServiceExporter.setServicePort(dataPort);
            rmiServiceExporter.setRegistryPort(vistPort);
            rmiServiceExporter.prepare();
            url = urlCreate(Tools.localHost(),vistPort,interfaceClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * 根据资源url生成对象
     * @param url
     * @param clazz
     * @return
     */
    @Override
    public Object convertObjectByUrl(String url,Class clazz){
        RmiProxyFactoryBean proxy = new RmiProxyFactoryBean();
        proxy.setServiceInterface(clazz);
        proxy.setServiceUrl(url);
        proxy.afterPropertiesSet();
        proxy.prepare();
        return proxy.getObject();
    }

    @Override
    public String urlCreate(String host, int port, Class clazz) {
        return String.format("rmi://%s/%s", String.format("%s:%s",host, port), Tools.serviceNameCreate(clazz));
    }

}
