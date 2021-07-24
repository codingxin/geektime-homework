package java0.nio01.util;

import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author codingzx
 * @description
 * @date 2021/7/24 21:48
 */
public class HttpUtil {

    public static String getHttp() throws IOException {
        CloseableHttpClient build = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet("http://localhost:80/hello");
        CloseableHttpResponse execute = build.execute(httpGet);
        StatusLine statusLine = execute.getStatusLine();
        String s = EntityUtils.toString(execute.getEntity());
        Map<String, Object> result = new HashMap<>(2);
        result.put("code", statusLine.getStatusCode());
        result.put("data", s);
        execute.close();
        build.close();
        return s;
    }


}
