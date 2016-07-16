package com.test;

import com.annotations.RPCprovider;
import com.enums.RemoteType;
import org.springframework.stereotype.Service;

/**
 * @author <a href="mailto:ruancl@59store.com">软软</a>
 * @version 1.0 16/7/14
 * @since 1.0
 */
@Service
@RPCprovider(remoteType = RemoteType.RMI,interfaceClass = Hello.class)
public class HelloImp implements Hello {
    @Override
    public void sayHello() {
        System.out.println("hello");
    }
}
