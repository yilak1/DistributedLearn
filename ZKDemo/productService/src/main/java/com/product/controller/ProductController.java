package com.product.controller;

import com.product.pojo.Product;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RequestMapping("/product")
@RestController
public class ProductController {

    @GetMapping("/getProduct/{id}")
    public Object getProduct(HttpServletRequest request, @PathVariable("id") String id) {
        return new Product(id,"name:"+request.getLocalPort());
    }
}
