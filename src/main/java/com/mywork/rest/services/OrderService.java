package com.mywork.rest.services;

import com.mywork.rest.repositories.OrderRepository;
import com.mywork.rest.exceptions.OrderNotFoundException;
import com.mywork.rest.models.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public List<Order> getOrders() {
        log.info("Returning all orders");
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> throwException(String.valueOf(id)));
    }

    public boolean deleteOrderById(Long id) {
        Optional<Order> order = orderRepository.findById(id);
        if (order.isPresent()) {
            log.info("Deleting order with Id: {}", id);
            orderRepository.deleteById(id);
            return true;
        } else {
            throwException(String.valueOf(id));
            return false;
        }
    }

    public Order createOrder(Order order) {
        log.info("Saving order: {}", order);
        return orderRepository.save(order);
    }

    private OrderNotFoundException throwException(String value) {
        throw new OrderNotFoundException("Order Not Found with ID: " + value);
    }
}
