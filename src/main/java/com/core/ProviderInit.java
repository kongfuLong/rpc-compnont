package com.core;

import com.annotations.RPCconsumer;
import com.annotations.RPCprovider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.stereotype.Service;

import java.lang.annotation.Annotation;

/**
 * @author <a href="mailto:ruancl@59store.com">软软</a>
 * @version 1.0 16/7/14
 * @since 1.0
 */
@Service
public class ProviderInit extends InstantiationAwareBeanPostProcessorAdapter {

    private Logger logger = LoggerFactory.getLogger(ProviderInit.class);


    @Autowired
    private RegisterCenter registerCenter;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Annotation[] annotations = bean.getClass().getAnnotations();
        if(annotations!=null && annotations.length>0){
            for (Annotation annotation : annotations){
                Class annotationClass = annotation.getClass();
                if(annotationClass == RPCprovider.class){
                    logger.info("远程服务进入:{}",bean.getClass().getName());
                    //服务注册 地址注册到zookeeper
                    registerCenter.register(bean);
                }else if(annotationClass == RPCconsumer.class){
                    //远程服务引用
                    return registerCenter.proxy(bean);
                }
            }
        }
        return bean;
    }
}
