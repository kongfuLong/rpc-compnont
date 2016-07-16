package com.core.remoteModules;

/**
 * @author <a href="mailto:ruancl@59store.com">软软</a>
 * @version 1.0 16/7/14
 * @since 1.0
 */
public interface RemoteInterfaceModule {


    String register(Object bean,Class interfaceClass);

    Object convertObjectByUrl(String url,Class clazz);

    String urlCreate(String host,int port,Class clazz);
}
