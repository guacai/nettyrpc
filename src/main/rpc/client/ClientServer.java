package client;

import codec.MSgDecode;
import codec.MsgEncode;
import entity.Request;
import entity.Response;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.springframework.beans.factory.DisposableBean;
import handle.RequestHandle;
import org.springframework.beans.factory.annotation.Autowired;
import register.ClientDiscoryService;

import java.util.Random;

/**
 * @description:
 * @author: zhangtb
 */
public class ClientServer implements DisposableBean {

    @Autowired
    private ClientDiscoryService clientDiscoryService;

    private EventLoopGroup workGroup;

    public void start() throws Exception {
        clientDiscoryService.watchStart();
        //random
        Random random = new Random();
        int index = random.nextInt(clientDiscoryService.endPoints.size());
        String [] endpoint = clientDiscoryService.endPoints.get(index).split(",");
        String host = endpoint[0];
        String port = endpoint[1];
        workGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        ChannelFuture f = bootstrap.group(workGroup).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                ch.pipeline()
                .addLast(new MsgEncode(Request.class))
                .addLast(new RequestHandle())
                .addLast(new MSgDecode(Response.class));
            }
        })
        .connect(host, Integer.valueOf(port)).sync();
        f.channel().closeFuture().sync();
    }

    @Override
    public void destroy() throws Exception {
        workGroup.shutdownGracefully();
    }
}
