package com.online.ordering.system.order.service.domain.event;

import com.online.ordering.system.domain.event.DomainEvent;
import com.online.ordering.system.order.service.domain.entity.Order;

import java.time.ZonedDateTime;

public class OrderCancelledEvent extends OrderEvent {
    public OrderCancelledEvent(Order order, ZonedDateTime createdAt) {
        super(order, createdAt);
    }
}
