package com.sifan.controller;

import com.sifan.service.UserService;
import com.springmvc.annotation.AutoWired;
import com.springmvc.annotation.Controller;
import com.springmvc.annotation.RequestMapping;


@Controller
public class UserController {

    @AutoWired(value = "userService")
    UserService userService;


    @RequestMapping(value = "/findUser")
    public String findUser(){

        userService.findUser();

        return "success";
    }
}
