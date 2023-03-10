package com.sifan.service;

import com.spring.BeanNameAware;
import com.spring.Component;
import com.spring.Scope;

@Component("userService")
//@Scope("prototype")
public class UserService implements BeanNameAware {

    private String beanName;

    public String getBeanName() {
        return beanName;
    }

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }

}
