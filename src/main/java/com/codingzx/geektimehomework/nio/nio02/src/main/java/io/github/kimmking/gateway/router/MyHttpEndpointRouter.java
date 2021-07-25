package com.codingzx.geektimehomework.nio.nio02.src.main.java.io.github.kimmking.gateway.router;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;


public class MyHttpEndpointRouter implements HttpEndpointRouter {
    private static AtomicInteger atomicInteger = new AtomicInteger(0);

    @Override
    public String route(List<String> endpoints) {
          // 权重获取  2  8 比例
          int a = Math.abs(new Random(10).nextInt());
          if(a<3) return endpoints.get(0);
          return endpoints.get(1);
//        return endpoints.get(atomicInteger.getAndIncrement() % endpoints.size());
    }

}
