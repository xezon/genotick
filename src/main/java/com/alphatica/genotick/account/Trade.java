package com.alphatica.genotick.account;

import lombok.Value;

import java.math.BigDecimal;

@Value
class Trade {

    private BigDecimal quantity;

    private BigDecimal price;

    BigDecimal value() {
        return quantity.abs().multiply(price);
    }
    
    public Trade(BigDecimal quantity, BigDecimal price) {
        this.quantity = quantity;
        this.price = price;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public BigDecimal getQuantity() {
        return quantity;
    }
}
