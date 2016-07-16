package com.test;

import com.annotations.RPCprovider;
import org.springframework.stereotype.Service;

/**
 * @author <a href="mailto:ruancl@59store.com">软软</a>
 * @version 1.0 16/7/14
 * @since 1.0
 */
@Service
//@RPCprovider
public class HelloImp implements Hello {
    @Override
    public void sayHello() {
        System.out.println("hello");
    }
}
