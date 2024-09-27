package com.coolers.aogp.aogp.service.impl;

import com.coolers.aogp.aogp.constant.GoldConst;
import com.coolers.aogp.aogp.dao.GoldPriceCustomMapper;
import com.coolers.aogp.aogp.dao.GoldPriceMapper;
import com.coolers.aogp.aogp.dto.GoldPriceExtreme;
import com.coolers.aogp.aogp.po.GoldPrice;
import com.coolers.aogp.aogp.service.GoldPriceService;
import com.coolers.aogp.aogp.util.BaseUtil;
import com.coolers.aogp.aogp.util.GoldPriceUtil;
import com.coolers.aogp.aogp.util.RedisUtil;
import com.coolers.aogp.aogp.vo.GoldPriceReq;
import com.coolers.aogp.aogp.vo.GoldPriceRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class GoldPriceServiceImpl implements GoldPriceService {
    @Autowired
    GoldPriceMapper goldPriceMapper;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    GoldPriceUtil goldPriceUtil;
    @Autowired
    GoldPriceCustomMapper goldPriceCustomMapper;


    /**
     * @param req goldType为查询的黄金类型，为空时默认是"Au99.99"
     */
    @Override
    public GoldPriceRes queryGoldPrice(GoldPriceReq req) {
        GoldPriceRes res = new GoldPriceRes();
        String goldType = BaseUtil.isEmptyString(req.getGoldType()) ? GoldConst.GOLD_AU9999 : req.getGoldType();
        List<GoldPrice> gpList = getGoldPriceList(goldType);
        gpList = doSomeFilter(gpList, req);
        res.setGoldPriceList(gpList);
        return res;
    }

    @Override
    public GoldPriceRes queryGoldPriceExtreme(GoldPriceReq req) {
        GoldPriceRes res = new GoldPriceRes();
        String goldType = BaseUtil.isEmptyString(req.getGoldType()) ? GoldConst.GOLD_AU9999 : req.getGoldType();
        List<GoldPriceExtreme> extremeList = redisUtil.get(goldType + LocalDate.now() + "extremeList");
        if (BaseUtil.isEmptyList(extremeList)) {
            extremeList = goldPriceUtil.getGoldPriceExtreme(getGoldPriceList(goldType));
            redisUtil.set(goldType + LocalDate.now() + "extremeList", extremeList, BaseUtil.getSurplusTimeToday());
        }
        res.setExtremeList(extremeList);
        return res;
    }

    /**
     * 由于数据一般直接从缓存取，查询条件不做限制，且一般数据量小，在service做过滤即可
     */
    private List<GoldPrice> doSomeFilter(List<GoldPrice> gpList, GoldPriceReq req) {
        if (!BaseUtil.isEmptyString(req.getStartDate())) {
            gpList = gpList.stream().filter(o -> o.getDate().compareTo(req.getStartDate()) >= 0).toList();
        }
        if (!BaseUtil.isEmptyString(req.getEndDate())) {
            gpList = gpList.stream().filter(o -> o.getDate().compareTo(req.getEndDate()) <= 0).toList();
        }
        return gpList;
    }

    private List<GoldPrice> getGoldPriceList(String goldType) {
        List<GoldPrice> gpList = redisUtil.get(goldType + LocalDate.now() + "goldPriceList");
        // 查看DB是否已更新过数据，当天未更新则查上金所数据并更新DB
        if (BaseUtil.isEmptyList(gpList)) {
            try {
                gpList = goldPriceUtil.getGoldPriceFromSGE(goldType);
            } catch (Exception e) {
                // 如果上金所查询异常，则当日使用DB数据即可
                gpList = goldPriceMapper.selectAll();
            }
            updateDataBase(gpList);
            redisUtil.set(goldType + LocalDate.now() + "goldPriceList", gpList, BaseUtil.getSurplusTimeToday());
        }
        return gpList;
    }

    private void updateDataBase(List<GoldPrice> gpList) {
        String dbmaxDate = goldPriceCustomMapper.selectMaxDate();
        for (int i = gpList.size() - 1; i >= 0; i--) {
            GoldPrice gp = gpList.get(i);
            if (!BaseUtil.isEmptyString(dbmaxDate) && gp.getDate().compareTo(dbmaxDate) <= 0) {
                // 已更新到最新值
                break;
            }
            goldPriceMapper.insert(gp);
        }
    }


}
