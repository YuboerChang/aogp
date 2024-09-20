package com.coolers.aogp.aogp.util;

import com.coolers.aogp.aogp.constant.GoldConst;
import com.coolers.aogp.aogp.dao.GoldPriceCustomMapper;
import com.coolers.aogp.aogp.dao.GoldPriceMapper;
import com.coolers.aogp.aogp.po.GoldPrice;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class GoldPriceQueryUtil {
    @Autowired
    GoldPriceMapper goldPriceMapper;
    @Autowired
    GoldPriceCustomMapper goldPriceCustomMapper;
    @Autowired
    RedisUtil redisUtil;

    /**
     * 黄金价格数据获取
     */
    public List<GoldPrice> getGoldPriceList(String goldType) {
        if (BaseUtil.isEmptyString(goldType)) {
            goldType = GoldConst.GOLD_AU9999;
        }
        List<GoldPrice> gpList = redisUtil.get(goldType + LocalDate.now() + "list");
        // 查看DB是否已更新过数据，当天未更新则查上金所数据并更新DB
        if (redisUtil.setNx(goldType + LocalDate.now() + "updateFlag", "true", 24 * 60 * 60)) {
            try {
                gpList = getGoldPriceFromSGE(goldType);
            } catch (Exception e) {
                // 如果上金所查询异常，则当日使用DB数据即可
                gpList = goldPriceMapper.selectAll();
            }
            updateDataBase(gpList);
            redisUtil.set(goldType + LocalDate.now() + "list", gpList, 24 * 60 * 60);
        }
        return gpList;
    }

    /**
     * 通过上海黄金交易所，查询特定种类黄金近期交易价格
     *
     * @param goldType 黄金种类，价格查询必要参数
     */
    public ArrayList<GoldPrice> getGoldPriceFromSGE(String goldType) {
        WebClient webClient = WebClient.builder().baseUrl("https://www.sge.com.cn").build();
        String res = webClient.post().uri("/graph/Dailyhq").body(BodyInserters.fromFormData("instid", goldType)).retrieve().bodyToMono(String.class).block();
        ArrayList<GoldPrice> gpList = new ArrayList<>();
        if (res != null) {
            JSONObject jsonObject = new JSONObject(res);
            JSONArray gpJsonArray = (JSONArray) jsonObject.get("time");
            for (Object o : gpJsonArray) {
                GoldPrice goldPrice = new GoldPrice();
                goldPrice.setDate((String) ((JSONArray) o).get(0));
                goldPrice.setOpen((BigDecimal) ((JSONArray) o).get(1));
                goldPrice.setClose((BigDecimal) ((JSONArray) o).get(2));
                goldPrice.setLowest((BigDecimal) ((JSONArray) o).get(3));
                goldPrice.setHighest((BigDecimal) ((JSONArray) o).get(4));
                gpList.add(goldPrice);
            }
        }
        // 降序处理
        gpList = gpList.stream().sorted((o1, o2) -> o2.getDate().compareTo(o1.getDate())).collect(Collectors.toCollection(ArrayList::new));
        return gpList;
    }

    public void updateDataBase(List<GoldPrice> gpList) {
        String dbmaxDate = goldPriceCustomMapper.selectMaxDate();
        for (GoldPrice gp : gpList) {
            if (!BaseUtil.isEmptyString(dbmaxDate) && gp.getDate().compareTo(dbmaxDate) <= 0) {
                // 已更新到最新值
                break;
            }
            goldPriceMapper.insert(gp);
        }
    }

}
