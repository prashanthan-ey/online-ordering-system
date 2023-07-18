package com.online.ordering.system.order.service.domain.entity;

import com.online.ordering.system.domain.entity.AggregateRoot;
import com.online.ordering.system.domain.valueobject.ShopId;

import java.util.List;

public class Shop extends AggregateRoot<ShopId> {

    private final List<Product> products;
    private boolean active;

    private Shop(Builder builder) {
        super.setId(builder.shopId);
        products = builder.products;
        active = builder.active;
    }

    public static Builder builder() {
        return new Builder();
    }


    public List<Product> getProducts() {
        return products;
    }

    public boolean isActive() {
        return active;
    }

    public static final class Builder {
        private ShopId shopId;
        private List<Product> products;
        private boolean active;

        private Builder() {
        }

        public Builder id(ShopId val) {
            shopId = val;
            return this;
        }

        public Builder products(List<Product> val) {
            products = val;
            return this;
        }

        public Builder active(boolean val) {
            active = val;
            return this;
        }

        public Shop build() {
            return new Shop(this);
        }
    }
}
