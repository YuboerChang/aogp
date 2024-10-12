package com.coolers.aogp.aogp.vo;

import com.coolers.aogp.aogp.po.GoldPrice;
import lombok.Data;

import java.util.List;

@Data
public class AdviseRes {
    private String extremeType;
    private List<GoldPrice> goldPriceList;
}
