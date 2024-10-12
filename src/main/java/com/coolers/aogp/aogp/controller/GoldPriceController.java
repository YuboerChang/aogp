package com.coolers.aogp.aogp.controller;

import com.coolers.aogp.aogp.service.GoldPriceService;
import com.coolers.aogp.aogp.vo.AdviseReq;
import com.coolers.aogp.aogp.vo.AdviseRes;
import com.coolers.aogp.aogp.vo.GoldPriceReq;
import com.coolers.aogp.aogp.vo.GoldPriceRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GoldPriceController {
    @Autowired
    GoldPriceService goldPriceService;

    @PostMapping("/queryGoldPrice")
    public GoldPriceRes queryGoldPrice(GoldPriceReq req) {
        return goldPriceService.queryGoldPrice(req);
    }

    @PostMapping("/queryAdvise")
    public AdviseRes queryAdvise(AdviseReq req) {
        return goldPriceService.queryAdvise(req);
    }


}
