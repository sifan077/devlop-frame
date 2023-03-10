package com.sifan.service;

import com.spring.Component;

@Component("roomService")
public class RoomService implements RoomInterface {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void testRoom() {
        System.out.println();
    }
}
