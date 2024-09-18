package com.coolers.aogp.aogp;

import com.coolers.aogp.aogp.po.GoldPrice;
import com.coolers.aogp.aogp.util.GoldPriceQueryUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

@SpringBootTest
class AogpApplicationTests {

    @Autowired
    GoldPriceQueryUtil goldPriceQueryUtil;


    @Test
    void contextLoads() {
        ArrayList<GoldPrice> l = goldPriceQueryUtil.getGoldPriceArray();
        for (GoldPrice gp : l) {
            if (gp.getDate().equals("2024-09-03")) {
                System.out.println(" getDate " + gp.getDate() + " getOpen " + gp.getOpen() + " getClose " + gp.getClose() + " getLowest " + gp.getLowest() + " getHighest " + gp.getHighest());
            }
        }

    }

}