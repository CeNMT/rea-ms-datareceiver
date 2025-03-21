package com.cnmt.server;

import com.cnmt.configuration.ServiceConfig;
import com.cnmt.service.Hl7Service;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.runtime.event.annotation.EventListener;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;

@Singleton
public class TcpServerImpl implements TcpServer {
    private static final Logger log = LoggerFactory.getLogger(TcpServerImpl.class);

    private boolean started;

    private int hl7Port;
    private int hl7AlarmPort;
    private final ServiceConfig serviceConfig;
    private final Hl7Service hl7Service;

    public TcpServerImpl(ServiceConfig serviceConfig, Hl7Service hl7Service) {
        this.serviceConfig = serviceConfig;
        this.hl7Service = hl7Service;
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    @EventListener
    public void onStartupEvent(@NonNull StartupEvent startupEvent) {
        hl7Port = serviceConfig.getHl7Port();
        hl7AlarmPort = serviceConfig.getHl7AlarmPort();
        log.info("Startup event: starting server: HL7port={} Hl7AlarmPort={}", hl7Port, hl7AlarmPort);

        Executors.newFixedThreadPool(serviceConfig.getHl7Threads()).execute(() -> {
            try {
                startInternal();
            } catch (InterruptedException e) {
                log.error("FATAL: Server interrupted", e);
                throw new RuntimeException(e);
            }
        });
    }

    private void startInternal() throws InterruptedException {
        EventLoopGroup parentGroup = new NioEventLoopGroup();
        EventLoopGroup childGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(parentGroup, childGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(@NonNull SocketChannel channel) {
                            channel.pipeline().addLast(new StringDecoder(), new StringEncoder(), new SimpleChannelInboundHandler<String>() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, String msg) {
                                    log.info("{}: received HL7 size={}", ctx, msg.length());
                                    String fhirResource = hl7Service.getFhirResource(msg); // todo split into chunks
                                    log.info("FHIR resource: {}", fhirResource); // todo
                                }
                            });
                        }
                    });

            ChannelFuture future = serverBootstrap.bind(hl7Port).sync();
            log.info("Netty TCP Server started on port {}", hl7Port);
            started = true;
            future.channel().closeFuture().sync();
        } finally {
            parentGroup.shutdownGracefully();
            childGroup.shutdownGracefully();
        }
    }
}
