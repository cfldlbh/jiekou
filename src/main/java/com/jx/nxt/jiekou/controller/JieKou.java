package com.jx.nxt.jiekou.controller;/*
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jx.nxt.jiekou.model.ReEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
/**
*abcddfsfsd
*/
@Controller
@RequestMapping("/region")
public class JieKou {
    @ResponseBody
    @RequestMapping("/getUserStatus/{phone}")
    public HashMap getUserStatus(@PathVariable String phone) {
        HashMap data = null;
        final String bearer = "Bearer ";
        String url = "http://a1.easemob.com/zyl/jx12316/token";  //获取token
        String userUrl = "http://a1.easemob.com/zyl/jx12316/users/" + phone + "/status"; //获取用户状态；

        HashMap<String, String> stringStringHashMap = new HashMap();
        stringStringHashMap.put("grant_type", "client_credentials");
        stringStringHashMap.put("client_id", "YXA6YcfpQOWUEeW524W0VX8FQg");
        stringStringHashMap.put("client_secret", "YXA6OB241eHKCUkSN3MVodh3ku7QcN8");
        JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(stringStringHashMap));

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Content-Type", "application/json");
        StringEntity entity = new StringEntity(jsonObject.toString(), "utf-8");
        entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        httpPost.setEntity(entity);
        String result = "";
        ReEntity parse = null;
        try {
            HttpResponse execute = httpClient.execute(httpPost);
            InputStream content = execute.getEntity().getContent();
            System.out.println(content);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(content, "utf-8"));
            StringBuilder stringBuilder = new StringBuilder();
            String line = null;
            while ((line = bufferedReader.readLine()) != null)
                stringBuilder.append(line + "\n");
            content.close();
            result = stringBuilder.toString();
            parse = (ReEntity) JSONObject.parseObject(result, ReEntity.class);
            System.out.println(result);
            if (execute.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

                System.out.println("请求服务器成功，做相应处理");

            } else {

                System.out.println("请求服务端失败");

            }
        } catch (IOException e) {
            e.printStackTrace();
            //return "Token请求异常";

        }
        if (parse == null || StringUtils.isEmpty(parse.getAccess_token())) {
            //return "未获取到Token";
        }

        HttpGet httpGet = new HttpGet(userUrl);
        httpGet.setHeader("Authorization", bearer + parse.getAccess_token());
        HttpClient getClient = new DefaultHttpClient();
        try {
            HttpResponse execute = getClient.execute(httpGet);
            if (execute.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND) {
                //return "此用户不存在";
            }
            ;
            if (execute.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED) {
                //return "未授权[无token、token错误、token过期]";
            }
            if (execute.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                InputStream content = execute.getEntity().getContent();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(content, "utf-8"));
                StringBuilder stringBuilder = new StringBuilder();
                String line = null;
                while ((line = bufferedReader.readLine()) != null)
                    stringBuilder.append(line + "\n");
                content.close();
                data = JSONObject.parseObject(stringBuilder.toString(), HashMap.class);
                //HashMap data = (HashMap) JSONObject.parseObject(hashMap.get("data").toString(), HashMap.class);
                //userStatus = (String) data.get(phone);
            }
        } catch (IOException e) {
            e.printStackTrace();
            //return "获取用户状态请求异常";

        }
        return data;
    }
}
