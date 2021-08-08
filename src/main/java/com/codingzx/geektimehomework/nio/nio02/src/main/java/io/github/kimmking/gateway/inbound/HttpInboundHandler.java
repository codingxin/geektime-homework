package com.codingzx.geektimehomework.nio.nio02.src.main.java.io.github.kimmking.gateway.inbound;


import com.codingzx.geektimehomework.nio.nio02.src.main.java.io.github.kimmking.gateway.filter.HeaderHttpRequestFilter;
import com.codingzx.geektimehomework.nio.nio02.src.main.java.io.github.kimmking.gateway.filter.HttpRequestFilter;
import com.codingzx.geektimehomework.nio.nio02.src.main.java.io.github.kimmking.gateway.filter.MyHttpRequestFilter;
import com.codingzx.geektimehomework.nio.nio02.src.main.java.io.github.kimmking.gateway.outbound.okhttp.OkhttpOutboundHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

//@ChannelHandler.Sharable
public class HttpInboundHandler extends ChannelInboundHandlerAdapter {

    private static Logger logger = LoggerFactory.getLogger(HttpInboundHandler.class);
    private final List<String> proxyServer;
    //    private HttpOutboundHandler handler;
//    private HttpRequestFilter filter = new HeaderHttpRequestFilter();
    // 改写 okhttp  指定过滤器
    private HttpRequestFilter filter = new MyHttpRequestFilter();

    // 改写 okhttp
    private OkhttpOutboundHandler okHttpOutboundHandler;


    public HttpInboundHandler(List<String> proxyServer) {
        this.proxyServer = proxyServer;
//        this.handler = new HttpOutboundHandler(this.proxyServer);
        // 改写 okhttp  初始化 处理handle
        this.okHttpOutboundHandler = new OkhttpOutboundHandler(this.proxyServer);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        ctx.fireChannelActive();
        System.out.println("Client  " + ctx.channel().remoteAddress() + "   connected");
    }


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        System.out.println("当Channel上的一个读操作完成时调用");
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            System.out.println("当Channel读取数据时调用");
            FullHttpRequest fullRequest = (FullHttpRequest) msg;

//            handler.handle(fullRequest, ctx, filter);
            // 改写 okhttp  调用 处理handle
            okHttpOutboundHandler.handle(fullRequest, ctx, filter);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

//    private void handlerTest(FullHttpRequest fullRequest, ChannelHandlerContext ctx) {
//        FullHttpResponse response = null;
//        try {
//            String value = "hello,kimmking";
//            response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(value.getBytes("UTF-8")));
//            response.headers().set("Content-Type", "application/json");
//            response.headers().setInt("Content-Length", response.content().readableBytes());
//
//        } catch (Exception e) {
//            logger.error("处理测试接口出错", e);
//            response = new DefaultFullHttpResponse(HTTP_1_1, NO_CONTENT);
//        } finally {
//            if (fullRequest != null) {
//                if (!HttpUtil.isKeepAlive(fullRequest)) {
//                    ctx.write(response).addListener(ChannelFutureListener.CLOSE);
//                } else {
//                    response.headers().set(CONNECTION, KEEP_ALIVE);
//                    ctx.write(response);
//                }
//            }
//        }
//    }
//
//    @Override
//    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
//        cause.printStackTrace();
//        ctx.close();
//    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("抛出异常" + ctx + "    ,  " + cause.getMessage());
    }

}
