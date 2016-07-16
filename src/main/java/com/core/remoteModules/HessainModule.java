package com.core.remoteModules;

import com.caucho.hessian.client.HessianProxyFactory;
import com.core.Tools;
import org.springframework.remoting.caucho.HessianServiceExporter;

import java.net.MalformedURLException;

/**
 * Created by Administrator on 2016/7/16.
 */
public class HessainModule implements RemoteInterfaceModule {

    private final int port = 9999;

    private final String PROJECT_NAME = "rpc-common";

    private HessianProxyFactory hessianProxyFactory = new HessianProxyFactory();

    @Override
    public String register(Object bean, Class interfaceClass) {
        HessianServiceExporter hessianServiceExporter = new HessianServiceExporter();
        hessianServiceExporter.setServiceInterface(interfaceClass);
        hessianServiceExporter.setService(bean);
        hessianServiceExporter.afterPropertiesSet();
        return urlCreate(Tools.localHost(),port,interfaceClass);
    }

    @Override
    public Object convertObjectByUrl(String url, Class clazz) {
        Object result = null;
        try {
            result = hessianProxyFactory.create(clazz,url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public String urlCreate(String host, int port, Class clazz) {
        return String.format("http://%s/%s", String.format("%s:%s", host, port), String.format("%s/%s",PROJECT_NAME,clazz.getSimpleName()));
    }
}
