package com.coolers.aogp.aogp;

import com.coolers.aogp.aogp.constant.GoldConst;
import com.coolers.aogp.aogp.po.GoldPrice;
import com.coolers.aogp.aogp.util.GoldPriceQueryUtil;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

public class unit {
    @Test
    public void test() {
        GoldPriceQueryUtil goldPriceQueryUtil = new GoldPriceQueryUtil();
        List<GoldPrice> list = goldPriceQueryUtil.getGoldPriceFromSGE(GoldConst.GOLD_AU9999);
        list=list.stream().sorted(new Comparator<GoldPrice>() {
            @Override
            public int compare(GoldPrice o1, GoldPrice o2) {
                return o2.getDate().compareTo(o1.getDate());
            }
        }).toList();
        for (GoldPrice gp : list) {
            System.out.println(gp.getDate());
        }
    }
}
