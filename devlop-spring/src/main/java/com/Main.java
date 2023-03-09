package com;

import com.sifan.AppConfig;
import com.spring.SifanApplicationContext;

public class Main {
    public static void main(String[] args) {
        SifanApplicationContext applicationContext
                = new SifanApplicationContext(AppConfig.class);
        Object userService = applicationContext.getBean("userService");
        Object shopService = applicationContext.getBean("shopService");
        System.out.println("userService=====>");
        System.out.println(userService);
        System.out.println(applicationContext.getBean("userService"));
        System.out.println(applicationContext.getBean("userService"));
        System.out.println("shopService=====>");
        System.out.println(shopService);
        System.out.println(applicationContext.getBean("shopService"));
        System.out.println(applicationContext.getBean("shopService"));
    }
}
