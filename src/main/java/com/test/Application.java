package com.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@Controller
@EnableAutoConfiguration
@ComponentScan("com")
public class Application {

    @Autowired
    private Hello helloImp;
    @RequestMapping("/")
    @ResponseBody
    String home(HttpServletRequest request) {
        String uri = request.getRequestURI();

        helloImp.sayHello();
        return "Hello World!";
    }

    public static void main(String[] args) throws Exception {

        SpringApplication.run(Application.class, args);
        //System.out.println(Thread.currentThread().getContextClassLoader());
    }
}
