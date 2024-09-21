package com.coolers.aogp.aogp.vo;

import com.coolers.aogp.aogp.dto.GoldPriceExtreme;
import com.coolers.aogp.aogp.po.GoldPrice;
import lombok.Data;

import java.util.List;

@Data
public class GoldPriceRes {
    List<GoldPrice> goldPriceList;
    List<GoldPriceExtreme> extremeList;
}
