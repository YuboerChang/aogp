package com.coolers.aogp.aogp.vo;

import com.coolers.aogp.aogp.constant.GoldConst;
import lombok.Data;

@Data
public class DaysPriceReq {
    private String date;
    private String goldType = GoldConst.GOLD_AU9999;
}
