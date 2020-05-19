package server;


import codec.MSgDecode;
import codec.MsgEncode;
import com.beust.jcommander.internal.Maps;
import config.ServerConfig;
import entity.Request;
import entity.Response;
import entity.RpcRegistered;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import register.RpcRegisterService;
import handle.ResponseHandle;
import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;

/**
 *
 *
 * @description:
 * @author: zhangtb
 */
@Component
@Slf4j
public class Server implements ApplicationContextAware, DisposableBean {

    @Resource
    private ServerConfig serverConfig;

    private NioEventLoopGroup bossGroup;

    private NioEventLoopGroup workGroup;

    @Autowired
    private RpcRegisterService rpcRegisterService;

    private Map<String, Object> beanMap = Maps.newHashMap();

    public void start() throws InterruptedException {
        bossGroup = new NioEventLoopGroup();
        workGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<ServerSocketChannel>() {
                        @Override
                        protected void initChannel(ServerSocketChannel ch) throws Exception {
                            ch.pipeline()
                            .addLast(new MSgDecode(Request.class))
                            .addLast(new ResponseHandle())
                            .addLast(new MsgEncode(Response.class));
                        }
                    });
            ChannelFuture cf = serverBootstrap.bind(serverConfig.getPort()).sync();
            cf.channel().closeFuture().sync();
            RpcRegistered rpcRegistered = new RpcRegistered(serverConfig.getIp(), serverConfig.getPort(), serverConfig.getId(), serverConfig.getName());
            rpcRegisterService.register(rpcRegistered);
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> rpcMap = applicationContext.getBeansWithAnnotation(RcpService.class);
        rpcMap.values().forEach(value->{
            String beanName = value.getClass().getName();
            beanMap.put(beanName, value);
        });
    }

    @Override
    public void destroy(){
        if(Objects.nonNull(bossGroup)){
            bossGroup.shutdownGracefully();
        }
        if(Objects.nonNull(workGroup)){
            workGroup.shutdownGracefully();
        }
    }
}
