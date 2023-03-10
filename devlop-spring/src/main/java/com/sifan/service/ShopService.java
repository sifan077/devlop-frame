package com.sifan.service;

import com.spring.AutoWired;
import com.spring.Component;

@Component("shopService")
public class ShopService {

    @AutoWired
    private OrderService orderService;

    public OrderService getOrderService() {
        return orderService;
    }

    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }
}
