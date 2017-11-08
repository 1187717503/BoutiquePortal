package com.intramirror.web.controller.api.coltori.category;

import com.google.gson.Gson;
import com.intramirror.web.util.HttpUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import pk.shoplus.service.excel.impl.ExcelServiceImpl;

/**
 * Created by dingyifan on 2017/11/8.
 */
public class GetCategoryMain {

    private static List<List<String>> lists = new ArrayList<>();

    public static void main(String[] args) throws Exception {
//        GetPostRequestUtil getPostRequestUtil = new GetPostRequestUtil();
//        String getTokenResponse = getPostRequestUtil.postAuth("https://api.orderlink.it/v2/user/token","TEST","1k0nic");
//        JSONObject toKenObject = JSONObject.fromObject(getTokenResponse);
//        String access_token = toKenObject.getString("access_token");
//        String expires_in = toKenObject.getString("expires_in");
        /*String access_token = "53dde4b7cb9c60358e5c4434ffb0cd776f9a14b7b2bb261e253b4d700bdfb0b31e36217e8e593b66596029330aa3101436ad94896616a7c97eeb0a91d66995f7ceec89345396fd10960060920a182c1d3762103f7a263f47c298cb316191208d";
        String expires_in = "86400";
        // 一级目录

        // 二级目录

        // 三级目录*/


//        String ct_token_url = "?access_token="+access_token+"&expires_in="+expires_in ;
        List<String> liming = new ArrayList<>();
        liming.add("ID1");
        liming.add("CategoryName1");

        liming.add("ID2");
        liming.add("CategoryName2");

        liming.add("ID3");
        liming.add("CategoryName3");


        String str = "[[\"ID\",\"CategoryName1\",\"ID\",\"CategoryName2\",\"ID\",\"CategoryName3\"],[\"DABB\",\"ABBIGLIAMENTO DONNA\",\"SBEAC\",\"BEACHWEAR\",\"CCOPR\",\"COPRICOSTUME\"],[\"DABB\",\"ABBIGLIAMENTO DONNA\",\"SBEAC\",\"BEACHWEAR\",\"CBIKI\",\"COSTUMI\"],[\"DABB\",\"ABBIGLIAMENTO DONNA\",\"SBEAC\",\"BEACHWEAR\",\"CTELI\",\"TELI MARE\"],[\"DABB\",\"ABBIGLIAMENTO DONNA\",\"SCAMI\",\"CAMICIE\",\"CBLUS\",\"BLUSE\"],[\"DABB\",\"ABBIGLIAMENTO DONNA\",\"SCAMI\",\"CAMICIE\",\"CCAMI\",\"CAMICIE\"],[\"DABB\",\"ABBIGLIAMENTO DONNA\",\"SCAPI\",\"CAPISPALLA\",\"CMANT\",\"MANTELLE\"],[\"DABB\",\"ABBIGLIAMENTO DONNA\",\"SCAPI\",\"CAPISPALLA\",\"CPIUM\",\"PIUMINI\"],[\"DABB\",\"ABBIGLIAMENTO DONNA\",\"SCAPI\",\"CAPISPALLA\",\"CGIUB\",\"GIUBBINI\"],[\"DABB\",\"ABBIGLIAMENTO DONNA\",\"SCAPI\",\"CAPISPALLA\",\"CGILE\",\"GILET/SMANICATO\"],[\"DABB\",\"ABBIGLIAMENTO DONNA\",\"SCAPI\",\"CAPISPALLA\",\"CCABA\",\"CABAN\"],[\"DABB\",\"ABBIGLIAMENTO DONNA\",\"SCAPI\",\"CAPISPALLA\",\"CCAPP\",\"CAPPOTTI\"],[\"DABB\",\"ABBIGLIAMENTO DONNA\",\"SCAPI\",\"CAPISPALLA\",\"CPELL\",\"PELLICCE\"],[\"DABB\",\"ABBIGLIAMENTO DONNA\",\"SCAPI\",\"CAPISPALLA\",\"CPARK\",\"PARKA\"],[\"DABB\",\"ABBIGLIAMENTO DONNA\",\"SCAPI\",\"CAPISPALLA\",\"CCAPI\",\"CAPI IN PELLE\"],[\"DABB\",\"ABBIGLIAMENTO DONNA\",\"SCAPI\",\"CAPISPALLA\",\"CTREN\",\"TRENCH E IMPERMEABILI\"],[\"DABB\",\"ABBIGLIAMENTO DONNA\",\"SJEAN\",\"JEANS\",\"CJEAN\",\"JEANS\"],[\"DABB\",\"ABBIGLIAMENTO DONNA\",\"SJEAN\",\"JEANS\",\"CSHOR\",\"SHORTS\"],[\"DABB\",\"ABBIGLIAMENTO DONNA\",\"SPANT\",\"PANTALONI\",\"CSHOR\",\"SHORTS\"],[\"DABB\",\"ABBIGLIAMENTO DONNA\",\"SPANT\",\"PANTALONI\",\"CPANT\",\"PANTALONI\"],[\"DABB\",\"ABBIGLIAMENTO DONNA\",\"SPANT\",\"PANTALONI\",\"CLEGG\",\"LEGGINS\"],[\"DABB\",\"ABBIGLIAMENTO DONNA\",\"STOPW\",\"TOPWEAR\",\"CTSHI\",\"T-SHIRTS E POLO\"],[\"DABB\",\"ABBIGLIAMENTO DONNA\",\"STOPW\",\"TOPWEAR\",\"CTOP\",\"TOP\"],[\"DABB\",\"ABBIGLIAMENTO DONNA\",\"STOPW\",\"TOPWEAR\",\"CFELP\",\"FELPE\"],[\"DABB\",\"ABBIGLIAMENTO DONNA\",\"SMAGL\",\"MAGLIERIA\",\"CMAGL\",\"MAGLIE\"],[\"DABB\",\"ABBIGLIAMENTO DONNA\",\"SMAGL\",\"MAGLIERIA\",\"CCARD\",\"CARDIGAN\"],[\"UABB\",\"ABBIGLIAMENTO UOMO\",\"STOPW\",\"TOPWEAR\",\"CSHIR\",\"T-SHIRT\"],[\"UABB\",\"ABBIGLIAMENTO UOMO\",\"STOPW\",\"TOPWEAR\",\"CPOLO\",\"POLO\"],[\"UABB\",\"ABBIGLIAMENTO UOMO\",\"STOPW\",\"TOPWEAR\",\"CFELP\",\"FELPE\"],[\"UABB\",\"ABBIGLIAMENTO UOMO\",\"SCAPI\",\"CAPISPALLA\",\"CCABA\",\"CABAN\"],[\"UABB\",\"ABBIGLIAMENTO UOMO\",\"SCAPI\",\"CAPISPALLA\",\"CPIUM\",\"PIUMINI\"],[\"UABB\",\"ABBIGLIAMENTO UOMO\",\"SCAPI\",\"CAPISPALLA\",\"CGIAC\",\"GIACCONI\"],[\"UABB\",\"ABBIGLIAMENTO UOMO\",\"SCAPI\",\"CAPISPALLA\",\"CCAPP\",\"CAPPOTTI\"],[\"UABB\",\"ABBIGLIAMENTO UOMO\",\"SCAPI\",\"CAPISPALLA\",\"CTREN\",\"TRENCH E IMPERMEABILI\"],[\"UABB\",\"ABBIGLIAMENTO UOMO\",\"SCAPI\",\"CAPISPALLA\",\"CPARK\",\"PARKA\"],[\"UABB\",\"ABBIGLIAMENTO UOMO\",\"SCAPI\",\"CAPISPALLA\",\"CBOMB\",\"GIUBBINI\"],[\"UABB\",\"ABBIGLIAMENTO UOMO\",\"SCAPI\",\"CAPISPALLA\",\"CCAPI\",\"CAPI IN PELLE\"],[\"UABB\",\"ABBIGLIAMENTO UOMO\",\"SCAPI\",\"CAPISPALLA\",\"CGILE\",\"GILET\"],[\"UABB\",\"ABBIGLIAMENTO UOMO\",\"SMAGL\",\"MAGLIERIA\",\"CGILE\",\"GILET\"],[\"UABB\",\"ABBIGLIAMENTO UOMO\",\"SMAGL\",\"MAGLIERIA\",\"CCARD\",\"CARDIGAN\"],[\"UABB\",\"ABBIGLIAMENTO UOMO\",\"SMAGL\",\"MAGLIERIA\",\"CMAGL\",\"MAGLIE\"],[\"UABB\",\"ABBIGLIAMENTO UOMO\",\"SBEAC\",\"BEACHWEAR\",\"CCOS\",\"COSTUMI\"],[\"UABB\",\"ABBIGLIAMENTO UOMO\",\"SBEAC\",\"BEACHWEAR\",\"CTEL\",\"TELI MARE\"],[\"UABB\",\"ABBIGLIAMENTO UOMO\",\"SPANT\",\"PANTALONI\",\"CBERM\",\"BERMUDA\"],[\"UABB\",\"ABBIGLIAMENTO UOMO\",\"SPANT\",\"PANTALONI\",\"CPANT\",\"PANTALONI\"]]\n";
        List<List<String>> s = new ArrayList<>();
        lists.add(liming);
//        new GetCategoryMain().m1(ct_token_url);
//        System.out.println(new Gson().toJson(lists));
        new ExcelServiceImpl().genExcel(lists);
    }

    public  void m1(String token_url){
        String category1Url = "https://api.orderlink.it/v2/groups";

        String response1 = HttpUtils.httpGet(category1Url + token_url);
        JSONArray category1s = JSONArray.fromObject(response1);
        System.out.println(response1);
        Iterator<Object> it = category1s.iterator();
        while (it.hasNext()) {
            JSONObject category1 = (JSONObject) it.next();
            m2(category1,token_url);
        }
    }

    public  void m2(JSONObject category1,String token_url){
        String category2Url = "https://api.orderlink.it/v2/subgroups";
        String id = category1.getString("id");
        String url = category2Url+"/"+id+token_url;

        String response2 = HttpUtils.httpGet(url);
        if(response2.contains("no results matching your query")) {
            return;
        }
        JSONArray category2s = JSONArray.fromObject(response2);
        Iterator<Object> it = category2s.iterator();
        while (it.hasNext()) {
            JSONObject category2 = (JSONObject) it.next();
            m3(category1,category2,token_url);
        }
    }

    public  void m3(JSONObject category1,JSONObject category2,String token_url) {
        String category3Url = "https://api.orderlink.it/v2/categories";
        String id1 = category1.getString("id");
        String id2 = category2.getString("id");
        String url = category3Url+"/"+id1+"/"+id2+token_url;
        System.out.println(url);
        String response3 = HttpUtils.httpGet(url);
        if(response3.contains("no results matching your query")) {
            return;
        }
        JSONArray category3s = JSONArray.fromObject(response3);
        Iterator<Object> it = category3s.iterator();


        while (it.hasNext()) {
            JSONObject category3 = (JSONObject) it.next();

            System.out.println(new Gson().toJson(category1)+"---"+new Gson().toJson(category2)+"---"+new Gson().toJson(category3));

            List<String> row = new ArrayList<>();
            row.add(category1.getString("id"));
            row.add(category1.getJSONObject("name").getString("it"));

            row.add(category2.getString("id"));
            row.add(category2.getJSONObject("name").getString("it"));

            row.add(category3.getString("id"));
            row.add(category3.getJSONObject("name").getString("it"));
            lists.add(row);
        }
    }



}
