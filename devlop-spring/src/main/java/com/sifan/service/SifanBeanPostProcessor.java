package com.sifan.service;

import com.spring.BeanPostProcessor;
import com.spring.Component;

/**
 * sifan bean前置和后置处理程序
 * 需要放在扫描组件的包中
 * @author 思凡
 * @date 2023/03/10
 */
@Component
public class SifanBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        System.out.println("初始化前");
        if (beanName.equals("roomService")) {
            ((RoomService) bean).setName("思凡");
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        System.out.println("初始化后");
        return bean;
    }
}
