package com.online.ordering.system.order.service.domain.entity;

import com.online.ordering.system.domain.entity.BaseEntity;
import com.online.ordering.system.domain.valueobject.Money;
import com.online.ordering.system.domain.valueobject.ProductID;

public class Product extends BaseEntity<ProductID> {
    private String name;
    private Money price;

    public Product(ProductID productID, String name, Money price) {
        super.setId(productID);
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public Money getPrice() {
        return price;
    }
}
