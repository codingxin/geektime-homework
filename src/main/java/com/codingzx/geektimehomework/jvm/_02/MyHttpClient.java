package com.codingzx.geektimehomework.jvm._02;



import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yangbiao
 * @date 2021/3/26
 */
@RestController
public class MyHttpClient {

    @GetMapping("/getHttpClient")
    public Object getHttpClient() throws IOException {
        CloseableHttpClient build = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet("http://localhost:8801");
        CloseableHttpResponse execute = build.execute(httpGet);
        StatusLine statusLine = execute.getStatusLine();
        String s = EntityUtils.toString(execute.getEntity());
        Map<String, Object> result = new HashMap<>(2);
        result.put("code", statusLine.getStatusCode());
        result.put("data", s);
        execute.close();
        build.close();
        return result;
    }

    @PostMapping("/postHttpClient")
    public Object postHttpClient() throws IOException {
        CloseableHttpClient build = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost("http://localhost:8801");
        CloseableHttpResponse execute = build.execute(httpPost);
        StatusLine statusLine = execute.getStatusLine();
        String s = EntityUtils.toString(execute.getEntity());
        Map<String,Object> result = new HashMap<>(2);
        result.put("code",statusLine.getStatusCode());
        result.put("data",s);
        execute.close();
        build.close();
        return result;
    }

    @GetMapping("/getOkHttp")
    public Object getOkHttp() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("http://localhost:8801").build();
        Response response = client.newCall(request).execute();
        String s = response.body().string();
        Map<String,Object> result = new HashMap<>(2);
        result.put("code",response.code());
        result.put("data",s);
        return result;
    }

}
