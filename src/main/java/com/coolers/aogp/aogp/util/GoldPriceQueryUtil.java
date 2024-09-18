package com.coolers.aogp.aogp.util;

import com.coolers.aogp.aogp.constant.GoldConst;
import com.coolers.aogp.aogp.po.GoldPrice;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.ArrayList;

@Component
public class GoldPriceQueryUtil {

    public ArrayList<GoldPrice> getGoldPriceArray() {
        return getGoldPriceFromSGE(GoldConst.GOLD_AU9999);
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
        return gpList;
    }

}
