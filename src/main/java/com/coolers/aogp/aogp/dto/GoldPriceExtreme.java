package com.coolers.aogp.aogp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoldPriceExtreme {
    private String date;
    private String type;
    private List<Map<String,String>> interval;
}
