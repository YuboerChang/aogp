package com.coolers.aogp.aogp;

import com.coolers.aogp.aogp.constant.GoldConst;
import com.coolers.aogp.aogp.po.GoldPrice;
import com.coolers.aogp.aogp.util.GoldPriceQueryUtil;
import com.coolers.aogp.aogp.util.RedisUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class AogpApplicationTests {

    @Autowired
    RedisUtil redisUtil;
    @Autowired
    GoldPriceQueryUtil goldPriceQueryUtil;


    @Test
    void contextLoads() {

    }

}