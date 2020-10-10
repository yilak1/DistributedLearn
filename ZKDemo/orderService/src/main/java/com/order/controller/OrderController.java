package com.order.controller;

import com.order.pojo.Order;
import com.order.pojo.Product;
import com.order.utils.LoadBalance;
import com.order.utils.RamdomLoadBalance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RequestMapping("/order")
@RestController
public class OrderController {
    @Autowired
    private RestTemplate restTemplate;

    private LoadBalance loadBalance = new RamdomLoadBalance();

    @RequestMapping("/getOrder/{id}")
    public Object getOrder(@PathVariable("id") String id ) {
        Product product = restTemplate.getForObject("http://"+loadBalance.choseServiceHost()+"/product/getProduct/1", Product.class);
        return new Order(id,"orderName",product);
    }
}
