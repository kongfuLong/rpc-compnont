package com.annotations;

import com.enums.RemoteType;

import java.lang.annotation.*;

/**
 * @author <a href="mailto:ruancl@59store.com">软软</a>
 * @version 1.0 16/7/14
 * @since 1.0
 */
@Target(ElementType.TYPE)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface RPCprovider {

    Class interfaceClass();

    RemoteType remoteType() default RemoteType.RMI;

}
