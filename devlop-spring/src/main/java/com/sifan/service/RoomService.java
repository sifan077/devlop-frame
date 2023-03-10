package com.sifan.service;

import com.spring.Component;
import com.spring.InitializingBean;

@Component("roomService")
public class RoomService implements InitializingBean {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("afterPropertiesSet");
        System.out.println("你好");
    }
}
