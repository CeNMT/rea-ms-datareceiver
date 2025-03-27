package com.cnmt.server;

import com.cnmt.configuration.ServiceConfig;
import com.cnmt.service.Hl7Service;
import com.cnmt.util.Const;
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

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

@Singleton
public class TcpServerImpl implements TcpServer {
    private static final Logger log = LoggerFactory.getLogger(TcpServerImpl.class);

    private static final String HL7_MSH_MARKER = "(?i)MSH";
    private static final String HL7_MSH = "MSH";
    private static ZoneOffset zoneOffset;

    // Data processing statistics
    private static final AtomicLong overallMessageNums = new AtomicLong(0);
    private static final AtomicLong overallMshNums = new AtomicLong(0);
    Map<String, Map<Boolean, String>> overallHl7ParseStatus = new ConcurrentHashMap<>(); // todo flush to disk upon reaching some number of overallMshNums
    private static final AtomicLong overallHl7MshSucceeded = new AtomicLong(0);
    private static final AtomicLong overallHl7MshFailed = new AtomicLong(0);
    private static final Map<String, String> statistics = new ConcurrentHashMap<>();

    private volatile boolean started;
    private int hl7Port;
    private int hl7AlarmPort;

    private final ServiceConfig serviceConfig;
    private final Hl7Service hl7Service;

    public TcpServerImpl(ServiceConfig serviceConfig, Hl7Service hl7Service) {
        this.serviceConfig = serviceConfig;
        this.hl7Service = hl7Service;
        zoneOffset = ZoneOffset.of(serviceConfig.getZoneId());
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    @Override
    public Map<String, String> getStatistics() {
        statistics.put("HL7 MSH succeeded", String.valueOf(overallHl7MshSucceeded.get()));
        statistics.put("HL7 MSH failed", String.valueOf(overallHl7MshFailed.get()));
        statistics.put("HL7 message processed", String.valueOf(overallMessageNums.get()));
        statistics.put("HL7 MSH processed", String.valueOf(overallMshNums.get()));
        return statistics;
    }

    @Override
    public Map<String, Map<Boolean, String>> getParseDetails() {
        return new TreeMap<>(overallHl7ParseStatus).descendingMap();
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

                                /**
                                 * Is called for each message of the type String.
                                 *
                                 * @param ctx the {@link ChannelHandlerContext} which this {@link SimpleChannelInboundHandler} belongs to
                                 * @param msg the message to handle
                                 */
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, String msg) {
                                    String dateTime =
                                            DateTimeFormatter.ofPattern(Const.DATETIME_FORMAT).format(ZonedDateTime.ofInstant(Instant.now(), zoneOffset));
                                    overallMessageNums.set(overallMessageNums.get() + 1);
                                    String[] messages = msg.split(HL7_MSH_MARKER);
                                    log.info("[{}][{}] {}: received HL7 MSHs={} size={}", dateTime, overallMessageNums.get(), ctx, messages.length - 1, msg.length());
                                    log.trace("[{}][{}]: Messages: {}", dateTime, overallMessageNums.get(), Arrays.stream(messages).toList());

                                    for (String message : messages) {
                                        if (message.isEmpty()) continue;
                                        final String fullMessage = String.format("%s%s", HL7_MSH, message);
                                        overallMshNums.set(overallMshNums.get() + 1);
                                        AtomicBoolean parserStatus = new AtomicBoolean(false);
                                        Executors.newSingleThreadExecutor().execute(() -> {
                                            String messageDateTime =
                                                    DateTimeFormatter.ofPattern(Const.DATETIME_FORMAT).format(ZonedDateTime.ofInstant(Instant.now(), zoneOffset));
                                            Map<Boolean, String> detailedMessageParseResult = new HashMap<>();

                                            Map<Boolean, String> messageParseResult = hl7Service.createFhirResources(messageDateTime, fullMessage);
                                            parserStatus.set(messageParseResult.containsKey(true));
                                            if (parserStatus.get()) {
                                                log.info("[{}]:[{}][{}/{}]: successfully parsed/upload HL7 size={}", dateTime, messageDateTime, overallMessageNums.get(),
                                                        overallMshNums.get(), fullMessage.length()); // todo trace
                                                detailedMessageParseResult.put(true, String.format("[%s][OK]: %s", overallMshNums.get(), fullMessage)); // todo comment out to keep space?
                                                overallHl7MshSucceeded.set(overallHl7MshSucceeded.get() + 1);
                                            } else {
                                                log.warn("[{}]:[{}][{}/{}]: FAILED to parse/upload HL7 size={}", dateTime, messageDateTime, overallMessageNums.get(),
                                                        overallMshNums.get(), fullMessage.length());
                                                detailedMessageParseResult.put(false, String.format("[%s][%s]: %s",
                                                        overallMshNums.get(), messageParseResult.get(false), fullMessage));
                                                overallHl7MshFailed.set(overallHl7MshFailed.get() + 1);
                                            }
                                            log.trace("[{}]:[{}][{}/{}]: FHIR resource in JSON: {}", dateTime, messageDateTime, overallMessageNums.get(),
                                                    overallMshNums.get(), parserStatus.get() ? hl7Service.getFhirResourceJson(fullMessage) : "{}");

                                            overallHl7ParseStatus.put(messageDateTime, detailedMessageParseResult);
                                        });
                                    }

                                    log.info("Ready for next"); // todo trace
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
