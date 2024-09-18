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
     * @param req id空时查全部，非空时查单个的step
     */
    @Override
    public GoldPriceRes queryGoldPrice(GoldPriceReq req) {
        GoldPriceRes res = new GoldPriceRes();
        List<GoldPrice> list = goldPriceQueryUtil.getGoldPriceArray();
        for (GoldPrice gp : list) {
            if (gp.getDate().equals("2024-09-03")) {
                GoldPrice ngp = goldPriceMapper.selectByPrimaryKey(gp.getDate());
                if (ngp == null) {
                    goldPriceMapper.insert(gp);
                }
                if (redisUtil.get(gp.getDate()) == null) {
                    redisUtil.set(gp.getDate(), gp.toString(), 300);
                }
            }
        }
        res.setGoldPriceList(list);
        return res;
    }

}
