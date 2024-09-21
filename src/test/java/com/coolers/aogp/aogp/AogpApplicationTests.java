package com.coolers.aogp.aogp;

import com.coolers.aogp.aogp.util.GoldPriceUtil;
import com.coolers.aogp.aogp.util.RedisUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AogpApplicationTests {

    @Autowired
    RedisUtil redisUtil;
    @Autowired
    GoldPriceUtil goldPriceUtil;


    @Test
    void contextLoads() {

    }

}