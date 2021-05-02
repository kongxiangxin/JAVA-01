package io.kimmking.rpcfx.client;

import com.alibaba.fastjson.JSON;
import io.kimmking.rpcfx.api.RpcfxRequest;
import io.kimmking.rpcfx.api.RpcfxResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.URI;

public class NettyClient {
    public static RpcfxResponse post(RpcfxRequest req, String url){
        RpcfxResponse response = new RpcfxResponse();

        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        try {
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<Channel>() {

                        @Override
                        protected void initChannel(Channel channel)
                                throws Exception {
                            //channel.pipeline().addLast(new HttpRequestEncoder());
                            //channel.pipeline().addLast(new HttpResponseDecoder());
                            channel.pipeline().addLast(new HttpClientCodec());
                            channel.pipeline().addLast(new HttpObjectAggregator(65536));
                            channel.pipeline().addLast(new HttpContentDecompressor());
                            channel.pipeline().addLast(new HttpClientHandler(req, response, url));
                        }
                    });
            URI uri = new URI(url);
            ChannelFuture future = bootstrap.connect(uri.getHost(), uri.getPort()).sync();
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            group.shutdownGracefully();
        }
        return response;
    }

    public static class HttpClientHandler extends ChannelInboundHandlerAdapter {
        private String url;
        private RpcfxRequest request;
        private RpcfxResponse response;

        public HttpClientHandler(RpcfxRequest request, RpcfxResponse response, String url) {
            this.url = url;
            this.request = request;
            this.response = response;
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            String content = JSON.toJSONString(request);
            URI uri = new URI(url);
            FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_0, HttpMethod.POST, uri.toASCIIString(), Unpooled.wrappedBuffer(content.getBytes("UTF-8")));
            request.headers().add(HttpHeaderNames.CONTENT_TYPE,HttpHeaderValues.APPLICATION_JSON);
            request.headers().add(HttpHeaderNames.CONNECTION,HttpHeaderValues.KEEP_ALIVE);
            request.headers().add(HttpHeaderNames.CONTENT_LENGTH,request.content().readableBytes());
            ctx.writeAndFlush(request);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg)
                throws Exception {
            System.out.println("msg -> "+msg);
            if(msg instanceof FullHttpResponse){
                FullHttpResponse response = (FullHttpResponse)msg;
                ByteBuf buf = response.content();
                String result = buf.toString(CharsetUtil.UTF_8);
                System.out.println("response -> "+result);
                RpcfxResponse rpcfxResponse = JSON.parseObject(result, RpcfxResponse.class);
                this.response.setException(rpcfxResponse.getException());
                this.response.setStatus(rpcfxResponse.isStatus());
                this.response.setResult(rpcfxResponse.getResult());

            }
        }

    }
}
