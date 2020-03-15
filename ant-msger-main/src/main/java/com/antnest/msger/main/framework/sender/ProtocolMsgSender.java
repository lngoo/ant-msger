package com.antnest.msger.main.framework.sender;

import com.antnest.msger.core.dto.jt808.basics.Message;
import com.antnest.msger.core.codec.MsgSplitterEncoder;
import com.antnest.msger.core.utils.HexUtil;
import com.antnest.msger.main.framework.session.Session;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.List;

public class ProtocolMsgSender {

    private static final MsgSplitterEncoder encoder = new MsgSplitterEncoder();

    public void sendTcpMsgSingle(Session session, Message message) {
        try {
            Channel channel = session.getChannel();
            List<String> msgs = encoder.splitAndEncode(message);
            for (String msg : msgs) {
                channel.writeAndFlush(Unpooled.wrappedBuffer(HexUtil.hexString2Bytes(msg))).sync();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendUdpMsgSingle(Session session, Message message) {
        try {
            Channel channel = session.getChannel();
            channel.connect(session.getSocketAddress());
            List<String> msgs = encoder.splitAndEncode(message);
            for (String msg : msgs) {
                channel.writeAndFlush(Unpooled.wrappedBuffer(HexUtil.hexString2Bytes(msg))).sync();
            }
            channel.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendWebsocketMsgSingle(Session session, Message message) {
        try {
            Channel channel = session.getChannel();
            List<String> msgs = encoder.splitAndEncode(message);
            for (String msg : msgs) {
                TextWebSocketFrame tws = new TextWebSocketFrame(msg);
//                channel.writeAndFlush(msg).sync();
                channel.writeAndFlush(tws).sync();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
