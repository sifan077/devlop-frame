package com;

import com.sifan.AppConfig;
import com.sifan.service.RoomInterface;
import com.sifan.service.RoomService;
import com.spring.SifanApplicationContext;

public class Main {
    public static void main(String[] args) {
        SifanApplicationContext applicationContext
                = new SifanApplicationContext(AppConfig.class);
        RoomInterface roomService
                = (RoomInterface) applicationContext.getBean("roomService");
        System.out.println(roomService);
    }
}
