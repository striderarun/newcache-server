package com.newcache.server.tcp;

import com.newcache.server.command.CommandProcessor;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.util.List;

public class TCPServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf inBuffer = (ByteBuf) msg;

        // Read input from the client
        String commandInput = inBuffer.toString(CharsetUtil.UTF_8);

        // Start processing the command
        CommandProcessor commandProcessor = new CommandProcessor();
        List<String> commandResponse = commandProcessor.processCommand(commandInput);

        // Write the command response to the channel
        String message = String.join("\n", commandResponse);
        ctx.write(Unpooled.copiedBuffer(message, CharsetUtil.UTF_8));
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}
