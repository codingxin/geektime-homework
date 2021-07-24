package com.codingzx.geektimehomework.nio.nio02.src.main.java.io.github.kimmking.gateway.filter;

import io.netty.handler.codec.http.FullHttpResponse;

public interface HttpResponseFilter {

    void filter(FullHttpResponse response);

}
