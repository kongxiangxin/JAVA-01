package gateway.handler;

import gateway.filter.HttpRequestFilter;
import gateway.filter.HttpResponseFilter;
import gateway.router.HttpEndpointRouter;
import gateway.router.RandomHttpEndpointRouter;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.util.ReferenceCountUtil;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class ServiceProxyHandler extends ChannelInboundHandlerAdapter {

    private static Logger logger = LoggerFactory.getLogger(ServiceProxyHandler.class);
    private CloseableHttpAsyncClient httpclient;
    private ExecutorService proxyService;

    private final List<String> proxyServers;

    private List<HttpRequestFilter> requestFilters = new ArrayList<>();
    private List<HttpResponseFilter> responseFilters = new ArrayList<>();
    private HttpEndpointRouter router = new RandomHttpEndpointRouter();

    public ServiceProxyHandler(List<String> proxyServers) {
        this.proxyServers = proxyServers.stream().map(this::formatUrl).collect(Collectors.toList());

        int cores = Runtime.getRuntime().availableProcessors();
        long keepAliveTime = 1000;
        int queueSize = 2048;
        proxyService = new ThreadPoolExecutor(cores, cores,
                keepAliveTime, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(queueSize));


        IOReactorConfig ioConfig = IOReactorConfig.custom()
                .setConnectTimeout(1000)
                .setSoTimeout(1000)
                .setIoThreadCount(cores)
                .setRcvBufSize(32 * 1024)
                .build();
        httpclient = HttpAsyncClients.custom().setMaxConnTotal(40)
                .setMaxConnPerRoute(8)
                .setDefaultIOReactorConfig(ioConfig)
                .setKeepAliveStrategy((response,context) -> 6000)
                .build();
        httpclient.start();
    }

    private String formatUrl(String backend) {
        return backend.endsWith("/")?backend.substring(0,backend.length()-1):backend;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    private void preHandle(FullHttpRequest request, ChannelHandlerContext ctx){
        for (HttpRequestFilter filter : requestFilters) {
            filter.filter(request, ctx);
        }
    }
    private void postHandle(FullHttpResponse response){
        for (HttpResponseFilter filter : responseFilters) {
            filter.filter(response);
        }
    }
    private void handle(FullHttpRequest fullRequest, ChannelHandlerContext ctx){
        proxyService.submit(() -> {
            doHandle(fullRequest, ctx);
        });
    }

    private void doHandle(final FullHttpRequest inbound, final ChannelHandlerContext ctx) {
        preHandle(inbound, ctx);

        String backendUrl = router.route(this.proxyServers);
        String url = backendUrl + inbound.uri();

        final HttpGet httpGet = new HttpGet(url);
        //httpGet.setHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_CLOSE);
        httpGet.setHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_KEEP_ALIVE);

        httpclient.execute(httpGet, new FutureCallback<HttpResponse>() {
            @Override
            public void completed(final HttpResponse endpointResponse) {
                try {
                    handleResponse(inbound, ctx, endpointResponse);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {

                }
            }

            @Override
            public void failed(final Exception ex) {
                httpGet.abort();
                ex.printStackTrace();
            }

            @Override
            public void cancelled() {
                httpGet.abort();
            }
        });
    }

    private void handleResponse(final FullHttpRequest fullRequest, final ChannelHandlerContext ctx, final HttpResponse endpointResponse) throws Exception {
        FullHttpResponse response = null;
        try {

            byte[] body = EntityUtils.toByteArray(endpointResponse.getEntity());

            response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(body));
            response.headers().set("Content-Type", "application/json");
            response.headers().setInt("Content-Length", Integer.parseInt(endpointResponse.getFirstHeader("Content-Length").getValue()));

            postHandle(response);

        } catch (Exception e) {
            e.printStackTrace();
            response = new DefaultFullHttpResponse(HTTP_1_1, NO_CONTENT);
            exceptionCaught(ctx, e);
        } finally {
            if (fullRequest != null) {
                if (!HttpUtil.isKeepAlive(fullRequest)) {
                    ctx.write(response).addListener(ChannelFutureListener.CLOSE);
                } else {
                    //response.headers().set(CONNECTION, KEEP_ALIVE);
                    ctx.write(response);
                }
            }
            ctx.flush();
            //ctx.close();
        }

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            logger.info("channelRead流量接口请求开始");
            FullHttpRequest fullRequest = (FullHttpRequest) msg;

            handle(fullRequest, ctx);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    public void addRequestFilter(HttpRequestFilter requestFilter) {
        requestFilters.add(requestFilter);
    }

    public void addResponseFilter(HttpResponseFilter responseFilter) {
        responseFilters.add(responseFilter);
    }
}