package com.coolers.aogp.aogp.service.impl;

import com.coolers.aogp.aogp.dao.GoldPriceMapper;
import com.coolers.aogp.aogp.po.GoldPrice;
import com.coolers.aogp.aogp.service.GoldPriceService;
import com.coolers.aogp.aogp.util.GoldPriceQueryUtil;
import com.coolers.aogp.aogp.util.RedisUtil;
import com.coolers.aogp.aogp.vo.GoldPriceReq;
import com.coolers.aogp.aogp.vo.GoldPriceRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoldPriceServiceImpl implements GoldPriceService {
    @Autowired
    GoldPriceMapper goldPriceMapper;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    GoldPriceQueryUtil goldPriceQueryUtil;

    /**
     * @param req goldType为查询的黄金类型，为空时默认是"Au99.99"
     */
    @Override
    public GoldPriceRes queryGoldPrice(GoldPriceReq req) {
        GoldPriceRes res = new GoldPriceRes();
        List<GoldPrice> list = goldPriceQueryUtil.getGoldPriceList(req.getGoldType());
        res.setGoldPriceList(list);
        return res;
    }

}
