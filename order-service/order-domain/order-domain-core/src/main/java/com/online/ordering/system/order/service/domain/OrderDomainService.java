package com.online.ordering.system.order.service.domain;

import com.online.ordering.system.order.service.domain.entity.Order;
import com.online.ordering.system.order.service.domain.entity.Shop;
import com.online.ordering.system.order.service.domain.event.OrderCancelledEvent;
import com.online.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.online.ordering.system.order.service.domain.event.OrderPaidEvent;

import java.util.List;

public interface OrderDomainService {

    OrderCreatedEvent validateAndInitiateOrder(Order order, Shop shop);

    OrderPaidEvent payOrder(Order order);

    void approveOrder(Order order);

    OrderCancelledEvent cancelOrderPayment(Order order, List<String> failureMessages);

    void cancelOrder(Order order, List<String> failureMessages);
}
