package com.alphatica.genotick.account;

import lombok.Value;

import java.math.BigDecimal;

@Value
class Trade {

    private BigDecimal quantity;

    private BigDecimal price;

}
