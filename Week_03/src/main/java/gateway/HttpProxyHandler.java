package gateway;

import gateway.filter.HeaderHttpRequestFilter;
import gateway.filter.HttpRequestFilter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class HttpProxyHandler extends ChannelInboundHandlerAdapter {

    private static Logger logger = LoggerFactory.getLogger(HttpProxyHandler.class);
    private final List<String> targetServer;
    private HttpRequestFilter filter = new HeaderHttpRequestFilter();
    
    public HttpProxyHandler(List<String> targetServer) {
        this.targetServer = targetServer;
    }
    
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            FullHttpRequest fullRequest = (FullHttpRequest) msg;

    
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

}
