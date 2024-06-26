package com.mywork.rest.controllers;

import com.mywork.rest.models.Order;
import com.mywork.rest.services.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping(path = "/orders")
    public ResponseEntity<List<Order>> getAllOrders() {
        log.info("Retrieving orders");
        return ResponseEntity.ok().body(orderService.getOrders());
    }

    @PostMapping(path = "/orders")
    public ResponseEntity<Order> saveOrder(@RequestBody Order order) {
        Order newOrder = orderService.createOrder(order);
        log.info("Saved Order: {}", newOrder);
        return new ResponseEntity<>(newOrder, HttpStatus.CREATED);
    }

    @GetMapping(path = "/orders/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        log.info("Retrieving order with Id: {}", id);
        return ResponseEntity.ok().body(orderService.getOrderById(id));
    }

    @DeleteMapping(path = "/orders/{id}")
    public ResponseEntity<String> deleteOrderById(@PathVariable Long id) {
        boolean deleteOrderById = orderService.deleteOrderById(id);
        log.info("Delete order with Id: {}", id);
        if (deleteOrderById) {
            return new ResponseEntity<>(("Order deleted - Order ID:" + id), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(("Order deletion failed - Order ID:" + id), HttpStatus.BAD_REQUEST);
        }
    }

}
