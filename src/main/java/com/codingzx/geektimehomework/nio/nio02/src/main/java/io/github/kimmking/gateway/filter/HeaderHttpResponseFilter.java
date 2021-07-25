package com.codingzx.geektimehomework.nio.nio02.src.main.java.io.github.kimmking.gateway.filter;

import io.netty.handler.codec.http.FullHttpResponse;

public class HeaderHttpResponseFilter implements HttpResponseFilter {
    @Override
    public void filter(FullHttpResponse response) {
        System.out.println("自定义回复过滤器");
        response.headers().set("modulename", "java-1-nio");
    }
}
