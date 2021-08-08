package com.codingzx.geektimehomework.nio.nio02.src.main.java.io.github.kimmking.gateway.outbound.okhttp;

import com.codingzx.geektimehomework.nio.nio02.src.main.java.io.github.kimmking.gateway.filter.HttpRequestFilter;
import com.codingzx.geektimehomework.nio.nio02.src.main.java.io.github.kimmking.gateway.filter.HttpResponseFilter;
import com.codingzx.geektimehomework.nio.nio02.src.main.java.io.github.kimmking.gateway.filter.MyHttpResponseFilter;
import com.codingzx.geektimehomework.nio.nio02.src.main.java.io.github.kimmking.gateway.router.HttpEndpointRouter;
import com.codingzx.geektimehomework.nio.nio02.src.main.java.io.github.kimmking.gateway.router.RandomHttpEndpointRouter;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import okhttp3.*;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class OkhttpOutboundHandler {
    private ExecutorService executorService;
    private List<String> backendUrls;
    private OkHttpClient okHttpClient;

    HttpResponseFilter myHttpResponseFilter = new MyHttpResponseFilter();
    HttpEndpointRouter router = new RandomHttpEndpointRouter();

    public OkhttpOutboundHandler(List<String> backendUrls) {
        //处理服务地址
        this.backendUrls = backendUrls.stream()
                .map(backendUrl -> backendUrl.endsWith("/") ? backendUrl.substring(0, backendUrl.length() - 1) : backendUrl)
                .collect(Collectors.toList());
        //创建线程池
        int cores = Runtime.getRuntime().availableProcessors();
        System.out.println("服务器可用的线程数" + cores);
        this.executorService = new ThreadPoolExecutor(cores, cores, 1000, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(2048), new ThreadPoolExecutor.CallerRunsPolicy());
        //初始化OkHttpClient
        this.okHttpClient = new OkHttpClient();
    }

    public void handle(final FullHttpRequest fullRequest, final ChannelHandlerContext ctx, HttpRequestFilter filter) {
        String backendUrl = router.route(this.backendUrls);
        final String url = backendUrl + fullRequest.uri();
        filter.filter(fullRequest, ctx);
        executorService.submit(() -> {
            //获取在过滤器里设置的请求头里的token信息
            String token = fullRequest.headers().get("Authorization");
            System.out.println("获取到的token信息" + token);
            Request request = new Request.Builder().url(url)
                    .header("Authorization", token)
                    .header("Org", "geek")
                    .build();

            //OkHttp的execute是同步方法,enqueue是异步方法
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    call.cancel();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    byte[] body = response.body().bytes();
                    FullHttpResponse fullHttpResponse = null;
                    try {
                        fullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(body));
                        fullHttpResponse.headers().set("Content-Type", "application/json");
                        fullHttpResponse.headers().set("Content-Length", body.length);

                        myHttpResponseFilter.filter(fullHttpResponse);

                    } catch (Exception e) {
                        System.err.println("处理出错:" + e.getMessage());
                        fullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NO_CONTENT);
                        ctx.close();
                    } finally {
                        if (fullRequest != null) {
                            if (!HttpUtil.isKeepAlive(fullRequest)) {
                                ctx.write(fullHttpResponse).addListener(ChannelFutureListener.CLOSE);
                            } else {
                                // 将接收到消息写给发送者
                                ctx.write(fullHttpResponse);
                            }
                        }
                        ctx.flush();
                    }
                }
            });

        });
    }


}
