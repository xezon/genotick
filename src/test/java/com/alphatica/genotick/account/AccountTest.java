package com.alphatica.genotick.account;

import com.alphatica.genotick.data.DataSetName;
import com.alphatica.genotick.genotick.Prediction;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class AccountTest {

    private Account account;
    private BigDecimal initial;

    private final DataSetName name1 = new DataSetName("one");
    private final double price1 = 100;
    private final DataSetName name2 = new DataSetName("two");
    private final double price2 = 1000;
    private final Map<DataSetName, Double> map1 = Collections.singletonMap(name1, price1);
    private final Map<DataSetName, Double> map2 = buildMap2();

    private Map<DataSetName,Double> buildMap2() {
        Map<DataSetName, Double> map = new HashMap<>();
        map.put(name1, price1);
        map.put(name2, price2);
        return map;
    }

    @BeforeMethod
    public void init() {
        initial = BigDecimal.valueOf(1_000_000);
        account = new Account(initial);
    }

    @Test
    public void sameValueAfterClose() {
        BigDecimal closed = account.closeAccount();
        assertTrue(initial.equals(closed));
    }

    @Test
    public void sameValueIfPriceNotChanged() {
        account.addPendingOrder(name1, Prediction.DOWN);
        account.openTrades(map1);
        account.closeTrades(map1);
        BigDecimal closed = account.closeAccount();
        assertTrue(initial.equals(closed));
    }

    @Test
    public void valueUpIfOneMarketUp() {
        double profit = 1.05;
        account.addPendingOrder(name1, Prediction.UP);
        account.openTrades(map1);
        account.closeTrades(Collections.singletonMap(name1, price1 * profit));
        BigDecimal closed = account.closeAccount();
        compare(closed, initial.multiply(BigDecimal.valueOf(profit)));
    }

    @Test
    public void valueUpIfTwoMarketsUp() {
        double profit = 1.05;
        account.addPendingOrder(name1, Prediction.UP);
        account.addPendingOrder(name2, Prediction.UP);
        account.openTrades(map2);
        Map<DataSetName, Double> map = new HashMap<>();
        map.put(name1, price1 * profit);
        map.put(name2, price2 * profit);
        account.closeTrades(map);
        BigDecimal closed = account.closeAccount();
        compare(closed, initial.multiply(BigDecimal.valueOf(profit)));
    }

    /*
    valueUpIfTwoMarketsDown
    valueUpIfOneMarketUpOneDown
    valueDownIfOneMarketDown
    valueDownIfTwoMarketsUp
    valueDownIfTwoMarketsDown
    valueDownIfOneMarketUpOneDown
     */

    @Test
    public void valueCorrectIfConsecutiveTrades() {
        double profit = 1.05;
        Map<DataSetName, Double> mapClose = Collections.singletonMap(name1, price1 * profit);

        account.addPendingOrder(name1, Prediction.UP);
        account.openTrades(map1);
        account.closeTrades(mapClose);

        account.addPendingOrder(name1, Prediction.UP);
        account.openTrades(map1);
        account.closeTrades(mapClose);

        account.addPendingOrder(name1, Prediction.UP);
        account.openTrades(map1);
        account.closeTrades(mapClose);

        BigDecimal closed = account.closeAccount();
        BigDecimal profitBD = BigDecimal.valueOf(profit);
        compare(closed, initial.multiply(profitBD).multiply(profitBD).multiply(profitBD));
    }

    @Test
    public void canOpenConsecutiveTrades() {
        account.addPendingOrder(name1, Prediction.UP);
        account.openTrades(map1);
        account.closeTrades(map1);

        account.addPendingOrder(name1, Prediction.UP);
        account.openTrades(map1);
        account.closeTrades(map1);

        account.addPendingOrder(name1, Prediction.DOWN);
        account.openTrades(map1);

    }

    @Test(expectedExceptions = AccountException.class)
    public void errorIfTwoPendingOrdersForSameMarket() {
        account.addPendingOrder(name1, Prediction.DOWN);
        account.addPendingOrder(name1, Prediction.DOWN);
    }

    @Test(expectedExceptions = AccountException.class)
    public void errorIfPreviousTradeNotClosed() {
        account.addPendingOrder(name1, Prediction.DOWN);
        account.openTrades(map1);
        account.addPendingOrder(name1, Prediction.DOWN);
        account.openTrades(map1);
    }

    private void compare(BigDecimal one, BigDecimal two) {
        assertEquals(
                one.setScale(10, BigDecimal.ROUND_HALF_DOWN),
                two.setScale(10, BigDecimal.ROUND_HALF_DOWN)
        );
    }

}
