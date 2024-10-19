package com.coolers.aogp.aogp.service;

import com.coolers.aogp.aogp.vo.*;

public interface GoldPriceService {
    public GoldPriceRes queryGoldPrice(GoldPriceReq req);

    public AdviseRes queryAdvise(AdviseReq req);

    public DaysPriceRes queryDaysPrice(DaysPriceReq req);

}
