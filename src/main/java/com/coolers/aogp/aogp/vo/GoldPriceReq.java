package com.coolers.aogp.aogp.vo;

import com.coolers.aogp.aogp.constant.GoldConst;
import lombok.Data;

@Data
public class GoldPriceReq {
    private String goldType = GoldConst.GOLD_AU9999;
    private String startDate;
    private String endDate;
}
