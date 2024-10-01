package com.coolers.aogp.aogp.util;

import com.coolers.aogp.aogp.constant.GoldConst;
import com.coolers.aogp.aogp.dto.GoldPriceExtreme;
import com.coolers.aogp.aogp.po.GoldPrice;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GoldPriceUtil {

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


    /**
     * 获取价格区间的所有极值
     * Xi = top; X(i+1,i+3) < Xi; top 最快得是X(i+4); 不影响dow
     * Xi = dow; X(i+1,i+3) > Xi; dow 最快得是X(i+4); 不影响top
     * 不过由于top和dow值不多，跳跃性小，不如直接跑一遍不用分开跑
     */
    public ArrayList<GoldPriceExtreme> getGoldPriceExtreme(List<GoldPrice> gpList) {
        ArrayList<GoldPriceExtreme> extremes = new ArrayList<>();
        for (int index = 3, border = gpList.size() - 3; index < border; index++) {
            String type = judgeExtreme(gpList, index);
            if (type.equals(GoldConst.TOP) || type.equals(GoldConst.DOW)) {
                // 组装前端要用到的x轴标记坐标
                List<Map<String, String>> xAxis = new ArrayList<>();
                Map<String, String> xAxisStart = new HashMap<>();
                xAxisStart.put("xAxis", gpList.get(index - 1).getDate());
                xAxis.add(xAxisStart);
                Map<String, String> xAxisEnd = new HashMap<>();
                xAxisEnd.put("xAxis", gpList.get(index + 1).getDate());
                xAxis.add(xAxisEnd);
                GoldPriceExtreme extreme = new GoldPriceExtreme(gpList.get(index).getDate(), type, xAxis);
                extremes.add(extreme);
            }
        }
        return extremes;
    }

    /**
     * 价格区间极值，前后三天均低/高于该日值，为极值
     * 有等于的不算极值
     */
    public String judgeExtreme(List<GoldPrice> gpList, int index) {
        BigDecimal data = gpList.get(index).getClose();
        int result = gpList.get(index - 3).getClose().compareTo(data);
        String extreme = GoldConst.NO;
        for (int i = index - 2; i <= index + 3; i++) {
            if (i == index) {
                continue;
            }
            if (gpList.get(i).getClose().compareTo(data) != result) {
                result = 0;
                break;
            }
        }
        if (result < 0) {
            extreme = GoldConst.TOP;
        } else if (result > 0) {
            extreme = GoldConst.DOW;
        }
        return extreme;
    }

}
