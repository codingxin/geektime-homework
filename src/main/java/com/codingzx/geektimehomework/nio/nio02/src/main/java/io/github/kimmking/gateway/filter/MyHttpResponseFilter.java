package com.codingzx.geektimehomework.nio.nio02.src.main.java.io.github.kimmking.gateway.filter;

import io.netty.handler.codec.http.FullHttpResponse;

import java.util.UUID;

/**
 * @date 2021/4/1
 */
public class MyHttpResponseFilter implements HttpResponseFilter {

    @Override
    public void filter(FullHttpResponse response) {
        response.headers().set("sign", UUID.randomUUID().toString());
    }
}
