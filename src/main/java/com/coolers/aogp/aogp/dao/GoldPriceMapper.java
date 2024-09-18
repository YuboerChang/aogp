package com.coolers.aogp.aogp.dao;

import com.coolers.aogp.aogp.po.GoldPrice;
import java.util.List;

public interface GoldPriceMapper {
    int deleteByPrimaryKey(String date);

    int insert(GoldPrice record);

    GoldPrice selectByPrimaryKey(String date);

    List<GoldPrice> selectAll();

    int updateByPrimaryKey(GoldPrice record);
}