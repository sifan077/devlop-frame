package com;

import com.sifan.AppConfig;
import com.sifan.service.ShopService;
import com.sifan.service.UserService;
import com.spring.SifanApplicationContext;

public class Main {
    public static void main(String[] args) {
        SifanApplicationContext applicationContext
                = new SifanApplicationContext(AppConfig.class);
        UserService userService = (UserService) applicationContext.getBean("userService");
        System.out.println(userService.getBeanName());
    }
}
