package com.coolers.aogp.aogp.vo;

import lombok.Data;

@Data
public class GoldPriceReq {
    private String goldType;
    private String startDate;
    private String endDate;
}
