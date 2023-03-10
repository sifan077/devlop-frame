package com;

import com.sifan.AppConfig;
import com.sifan.service.ShopService;
import com.spring.SifanApplicationContext;

public class Main {
    public static void main(String[] args) {
        SifanApplicationContext applicationContext
                = new SifanApplicationContext(AppConfig.class);
        ShopService shopService
                = (ShopService) applicationContext.getBean("shopService");
        System.out.println(shopService.getOrderService());
    }
}
