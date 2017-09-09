package com.alphatica.genotick.account;

import lombok.Value;
import java.math.BigDecimal;
import com.alphatica.genotick.genotick.Prediction;

@Value
class Trade {
    private BigDecimal quantity;
    private BigDecimal price;
    private Prediction prediction;

    BigDecimal value() {
        return quantity.abs().multiply(price);
    }
}
