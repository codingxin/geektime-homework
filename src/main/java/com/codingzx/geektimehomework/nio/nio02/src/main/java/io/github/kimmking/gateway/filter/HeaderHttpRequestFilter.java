package com.codingzx.geektimehomework.nio.nio02.src.main.java.io.github.kimmking.gateway.filter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

public class HeaderHttpRequestFilter implements HttpRequestFilter {

    @Override
    public void filter(FullHttpRequest fullRequest, ChannelHandlerContext ctx) {
        System.out.println("自定义请求过滤器");
        fullRequest.headers().set("author", "codingzx");
    }
}
