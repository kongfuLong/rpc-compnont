package com.core;

/**
 * @author <a href="mailto:ruancl@59store.com">软软</a>
 * @version 1.0 16/7/15
 * @since 1.0
 * 防止服务接口名重名,服务名使用类全名    并且类全名 的.替换成_
 */
public class Tools {

    public static String serviceNameCreate(Class clazz){
        return clazz.getName().replace(".","_");
    }
}
