package com.coolers.aogp.aogp.service;

import com.coolers.aogp.aogp.vo.AdviseReq;
import com.coolers.aogp.aogp.vo.AdviseRes;
import com.coolers.aogp.aogp.vo.GoldPriceReq;
import com.coolers.aogp.aogp.vo.GoldPriceRes;

public interface GoldPriceService {
    public GoldPriceRes queryGoldPrice(GoldPriceReq req);

    public AdviseRes queryAdvise(AdviseReq req);

}
