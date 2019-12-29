package org.yzh.framework;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.ReferenceCountUtil;
import org.yzh.framework.mapping.Handler;
import org.yzh.framework.message.AbstractMessage;
import org.yzh.framework.session.Session;

import java.lang.reflect.Type;

public class UdpServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            System.out.println("%%%%%%######### " + msg.getClass().getSimpleName());
            DatagramPacket packet = (DatagramPacket)msg;
            System.out.println("/////// " + packet.sender().getAddress().toString() + packet.sender().getPort());
            super.channelRead(ctx, msg);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("%%%%%%######### channelActive");
    }
}
