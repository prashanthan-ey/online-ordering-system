package com.online.ordering.system.order.service.domain.entity;

import com.online.ordering.system.domain.entity.AggregateRoot;
import com.online.ordering.system.domain.valueobject.*;
import com.online.ordering.system.order.service.domain.exception.OrderDomainException;
import com.online.ordering.system.order.service.domain.valueobject.OrderItemId;
import com.online.ordering.system.order.service.domain.valueobject.StreetAddress;
import com.online.ordering.system.order.service.domain.valueobject.TrackingId;

import java.util.List;
import java.util.UUID;

public class Order extends AggregateRoot<OrderId> {

    private final CustomerId customerId;
    private final ShopId shopId;
    private final StreetAddress deliveryAddress;
    private final Money price;
    private final List<OrderItem> items;

    /**
     * these three fields are not final because
     * I will set them during business logic after creating the order entity,
     * so I cannot make them final.
     */
    private TrackingId trackingId;
    private OrderStatus orderStatus;
    private List<String> failureMessages;


    //initialize the Order
    public void initializeOrder() {
        setId(new OrderId(UUID.randomUUID()));
        trackingId = new TrackingId(UUID.randomUUID());
        orderStatus = OrderStatus.PENDING;
        initializeOrderItems();
    }

    //initialize the order items
    private void initializeOrderItems() {
        long itemId = 1;
        for (OrderItem orderItem : items) {
            orderItem.initializeOrderItem(super.getId(), new OrderItemId(itemId++));
        }
    }

    //validate the order
    public void validateOrder() {
        validateInitialOrder();
        validateTotalPrice();
        validateItemsPrice();
    }

    /**
     * state changing methods to change the state of an order during order processing
     * state = pay
     */
    public void pay() {
        if (orderStatus != OrderStatus.PENDING) {
            throw new OrderDomainException("Order is not in correct state for Pay operation!");
        }
        orderStatus = OrderStatus.PAID;
    }

    /**
     * state changing methods to change the state of an order during order processing
     * state = approve
     */
    public void approve() {
        if (orderStatus != OrderStatus.PAID) {
            throw new OrderDomainException("Order is not in correct state for Approve operation!");
        }
        orderStatus = OrderStatus.APPROVED;
    }

    /**
     * state changing methods to change the state of an order during order processing
     * state = init cancel
     */
    public void initCancel(List<String> failureMessages) {
        if (orderStatus != OrderStatus.PAID) {
            throw new OrderDomainException("Order is not in a correct state for Init Cancel operation!");
        }
        orderStatus = OrderStatus.CANCELLING;
        updateFailureMessages(failureMessages);
    }

    /**
     * state changing methods to change the state of an order during order processing
     * state = cancel
     */
    public void cancel(List<String> failureMessages) {
        if (!(orderStatus == OrderStatus.PENDING || orderStatus == OrderStatus.CANCELLING)) {
            throw new OrderDomainException("Order is not in a correct state for Cancel operation");
        }
        orderStatus = OrderStatus.CANCELLED;
        updateFailureMessages(failureMessages);

    }

    /**
     * updating all the failure messages
     * @param failureMessages
     */
    private void updateFailureMessages(List<String> failureMessages) {
        if (this.failureMessages != null && failureMessages != null) {
            this.failureMessages.addAll(failureMessages.stream().filter(message -> !message.isEmpty()).toList());
        }
        if(this.failureMessages == null) {
            this.failureMessages = failureMessages;
        }
    }

    //validate the initial order
    //check whether order id and order status are not null
    private void validateInitialOrder() {
        if (orderStatus != null && getId() != null) {
            throw new OrderDomainException("Order is not in a correct state for initialization!");
        }
    }

    //validate the total price
    //check whether total price > 0
    private void validateTotalPrice() {
        if (price == null || !price.isGraterThanZero()) {
            throw new OrderDomainException("Total price must be greater than zero!");
        }
    }

    //validate all the items price
    private void validateItemsPrice() {
        Money orderItemsTotal = items.stream().map(orderItem -> {
            validateItemPrice(orderItem);
            return orderItem.getSubTotal();
        }).reduce(Money.ZERO, Money::add);

        if (!price.equals(orderItemsTotal)) {
            throw new OrderDomainException("Total price: " + price.getAmount()
                    + " is not  equal to Order items total: " + orderItemsTotal);
        }
    }

    //validate the single item price
    private void validateItemPrice(OrderItem orderItem) {
        if (!orderItem.isPriceValid()) {
            throw new OrderDomainException("Order item price: " + orderItem.getPrice().getAmount() +
                    " is  not valid for product " + orderItem.getProduct().getId().getValue());
        }
    }

    private Order(Builder builder) {
        super.setId(builder.orderId);
        customerId = builder.customerId;
        shopId = builder.shopId;
        deliveryAddress = builder.deliveryAddress;
        price = builder.price;
        items = builder.items;
        trackingId = builder.trackingId;
        orderStatus = builder.orderStatus;
        failureMessages = builder.failureMessages;
    }

    public static Builder builder() {
        return new Builder();
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public ShopId getShopId() {
        return shopId;
    }

    public StreetAddress getDeliveryAddress() {
        return deliveryAddress;
    }

    public Money getPrice() {
        return price;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public TrackingId getTrackingId() {
        return trackingId;
    }

    public OrderStatus getStatus() {
        return orderStatus;
    }

    public List<String> getFailureMessages() {
        return failureMessages;
    }

    public static final class Builder {
        private OrderId orderId;
        private CustomerId customerId;
        private ShopId shopId;
        private StreetAddress deliveryAddress;
        private Money price;
        private List<OrderItem> items;
        private TrackingId trackingId;
        private OrderStatus orderStatus;
        private List<String> failureMessages;

        private Builder() {
        }

        public Builder orderId(OrderId val) {
            orderId = val;
            return this;
        }

        public Builder customerId(CustomerId val) {
            customerId = val;
            return this;
        }

        public Builder shopId(ShopId val) {
            shopId = val;
            return this;
        }

        public Builder deliveryAddress(StreetAddress val) {
            deliveryAddress = val;
            return this;
        }

        public Builder price(Money val) {
            price = val;
            return this;
        }

        public Builder items(List<OrderItem> val) {
            items = val;
            return this;
        }

        public Builder trackingId(TrackingId val) {
            trackingId = val;
            return this;
        }

        public Builder status(OrderStatus val) {
            orderStatus = val;
            return this;
        }

        public Builder failureMessages(List<String> val) {
            failureMessages = val;
            return this;
        }

        public Order build() {
            return new Order(this);
        }
    }
}
