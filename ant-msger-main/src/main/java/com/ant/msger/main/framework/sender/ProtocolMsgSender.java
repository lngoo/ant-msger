package com.ant.msger.main.framework.sender;

import com.ant.msger.base.dto.jt808.basics.Message;
import com.ant.msger.main.framework.codec.MsgSplitterEncoder;
import com.ant.msger.main.framework.commons.transform.HexUtil;
import com.ant.msger.main.framework.session.Session;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.nio.charset.Charset;
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
