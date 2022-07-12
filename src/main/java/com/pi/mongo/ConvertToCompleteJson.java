package com.pi.mongo;

import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConvertToCompleteJson {

    public static void main(String[] args) {
        try (FileReader custReader = new FileReader("src/main/java/resources/customer.json");
             FileReader prodCatInfoReader = new FileReader("src/main/java/resources/prod_cat_info.json");
             FileReader transactionsReader = new FileReader("src/main/java/resources/transactions.json");
             FileWriter updTrasactions = new FileWriter("updatedtransactions.json")) {

            //Read JSON file
            JSONParser jsonParser = new JSONParser();

            Object custObj = jsonParser.parse(custReader);
            JSONArray customerList = (JSONArray) custObj;
            Map<Long, JSONObject> custMap = new HashMap<>();
            customerList.forEach(cust -> prepareMapForCustomer((JSONObject) cust, custMap));

            Object prodCatInfoObj = jsonParser.parse(prodCatInfoReader);
            JSONArray prodCatInfoList = (JSONArray) prodCatInfoObj;
            Map<String, JSONObject> prodCatInfoMap = new HashMap<>();
            prodCatInfoList.forEach(prodCatInfo -> prepareMapForProdcatinfo((JSONObject) prodCatInfo, prodCatInfoMap));

            Object transactionsObj = jsonParser.parse(transactionsReader);
            JSONArray transactionsList = (JSONArray) transactionsObj;
            transactionsList.forEach(transaction -> prepareJson((JSONObject) transaction, custMap, prodCatInfoMap));
           // System.out.println(transactionsList);

            updTrasactions.write(transactionsList.toJSONString());
            updTrasactions.flush();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    private static void prepareJson(JSONObject jsonObj, Map<Long, JSONObject> custMap, Map<String, JSONObject> prodCatInfoMap) {
        String prodCatCode = ((Long) jsonObj.get("prod_cat_code")).toString();
        String prodSubCatCode = ((Long) jsonObj.get("prod_subcat_code")).toString();

        String key = prodCatCode + "-" + prodSubCatCode;
        if (prodCatInfoMap.get(key) != null) {
            jsonObj.remove("prod_cat_code");
            jsonObj.remove("prod_subcat_code");
            JSONObject catg = prodCatInfoMap.get(key);
            jsonObj.put("categorydetails", catg);
        }

        Long custId = ((Long) jsonObj.get("cust_id"));
        if (custMap.get(custId) != null) {
            jsonObj.remove("cust_id");
            JSONObject cust = custMap.get(custId);
            jsonObj.put("customerdetails", cust);
        }
    }

    private static void prepareMapForCustomer(@NotNull JSONObject jsonObj, Map<Long, JSONObject> custMap) {
        Long custId = (Long) jsonObj.get("customer_Id");
        if (custMap.get(custId) == null)
            custMap.put(custId, jsonObj);
    }

    private static void prepareMapForProdcatinfo(JSONObject jsonObj, Map<String, JSONObject> prodCatInfoMap) {
        String prodCatCode = ((Long) jsonObj.get("prod_cat_code")).toString();
        String prodSubCatCode = ((Long) jsonObj.get("prod_sub_cat_code")).toString();
        String key = prodCatCode + "-" + prodSubCatCode;
        if (prodCatInfoMap.get(key) == null)
            prodCatInfoMap.put(key, jsonObj);
    }

}
