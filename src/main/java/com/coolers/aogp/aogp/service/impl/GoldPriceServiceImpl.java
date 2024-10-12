package com.coolers.aogp.aogp.service.impl;

import com.coolers.aogp.aogp.constant.ExceptionConst;
import com.coolers.aogp.aogp.constant.GoldConst;
import com.coolers.aogp.aogp.dao.GoldPriceCustomMapper;
import com.coolers.aogp.aogp.dao.GoldPriceMapper;
import com.coolers.aogp.aogp.dto.GoldPriceExtreme;
import com.coolers.aogp.aogp.po.GoldPrice;
import com.coolers.aogp.aogp.service.GoldPriceService;
import com.coolers.aogp.aogp.util.BaseUtil;
import com.coolers.aogp.aogp.util.GoldPriceUtil;
import com.coolers.aogp.aogp.util.RedisUtil;
import com.coolers.aogp.aogp.vo.AdviseReq;
import com.coolers.aogp.aogp.vo.AdviseRes;
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
     * 获取黄金历史价格，带有价格极值列表
     *
     * @param req goldType为查询的黄金类型，为空时默认是"Au99.99"
     */
    @Override
    public GoldPriceRes queryGoldPrice(GoldPriceReq req) {
        GoldPriceRes res = new GoldPriceRes();
        // 查询历史价格
        List<GoldPrice> gpList = getGoldPriceList(req.getGoldType());
        gpList = goldPriceUtil.goldPriceFiltering(gpList, req.getStartDate(), req.getEndDate());
        res.setGoldPriceList(gpList);
        // 查询价格极值
        List<GoldPriceExtreme> extremeList = getExtremeList(req.getGoldType());
        extremeList = goldPriceUtil.extremeFiltering(extremeList, req.getStartDate(), req.getEndDate());
        res.setExtremeList(extremeList);
        return res;
    }

    /**
     * 获取黄金极值分析建议，即最近一个极值类型和[极值点，至今]价格列表
     *
     * @param req goldType为查询的黄金类型，为空时默认是"Au99.99"
     */
    @Override
    public AdviseRes queryAdvise(AdviseReq req) {
        AdviseRes res = new AdviseRes();
        List<GoldPriceExtreme> extremeList = getExtremeList(req.getGoldType());
        // 如果没有极值直接异常处理即可
        GoldPriceExtreme recentExtreme = extremeList.get(extremeList.size() - 1);
        List<GoldPrice> gpList = getGoldPriceList(req.getGoldType());
        gpList = goldPriceUtil.goldPriceFiltering(gpList, recentExtreme.getDate(), null);
        res.setGoldPriceList(gpList);
        res.setExtremeType(recentExtreme.getType());
        return res;
    }

    private List<GoldPrice> getGoldPriceList(String goldType) {
        List<GoldPrice> gpList = redisUtil.get(goldType + LocalDate.now() + "goldPriceList");
        // 查看DB是否已更新过数据，当天未更新则查上金所数据并更新DB
        if (BaseUtil.isEmptyList(gpList)) {
            try {
                gpList = goldPriceUtil.getGoldPriceFromSGE(goldType);
            } catch (Exception e) {
                // 如果上金所查询异常，则当日使用DB数据即可，数据库目前只存储AU99的数据
                if (GoldConst.GOLD_AU9999.equals(goldType)) {
                    gpList = goldPriceMapper.selectAll();
                } else {
                    throw new RuntimeException(ExceptionConst.ERR_MES);
                }
            }
            updateDataBase(gpList);
            redisUtil.set(goldType + LocalDate.now() + "goldPriceList", gpList, BaseUtil.getSurplusTimeToday());
        }
        return gpList;
    }

    private List<GoldPriceExtreme> getExtremeList(String goldType) {
        List<GoldPriceExtreme> extremeList = redisUtil.get(goldType + LocalDate.now() + "extremeList");
        if (BaseUtil.isEmptyList(extremeList)) {
            extremeList = goldPriceUtil.getGoldPriceExtreme(getGoldPriceList(goldType));
            redisUtil.set(goldType + LocalDate.now() + "extremeList", extremeList, BaseUtil.getSurplusTimeToday());
        }
        return extremeList;
    }

    private void updateDataBase(List<GoldPrice> gpList) {
        String dbRecentDate = goldPriceCustomMapper.selectMaxDate();
        for (int i = gpList.size() - 1; i >= 0; i--) {
            GoldPrice gp = gpList.get(i);
            if (!BaseUtil.isEmptyString(dbRecentDate) && gp.getDate().compareTo(dbRecentDate) <= 0) {
                // 已更新到最新值
                break;
            }
            goldPriceMapper.insert(gp);
        }
    }


}
