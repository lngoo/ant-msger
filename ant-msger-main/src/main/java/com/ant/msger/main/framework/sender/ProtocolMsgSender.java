package com.ant.msger.main.framework.sender;

import com.ant.msger.base.dto.jt808.basics.Message;
import com.ant.msger.main.framework.session.Session;
import com.ant.msger.main.web.jt808.codec.JT808MessageEncodeHelper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class ProtocolMsgSender {

    public void sendTcpMsgSingle(Session session, Message message) {
        try {
            Channel channel = session.getChannel();
            channel.writeAndFlush(message).sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendUdpMsgSingle(Session session, Message message) {
        try {
            Channel channel = session.getChannel();
            channel.connect(session.getSocketAddress());
            ChannelFuture future = channel.writeAndFlush(message).sync();
            channel.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendWebsocketMsgSingle(Session session, Message message) {
        try {
            Channel channel = session.getChannel();
            TextWebSocketFrame tws = new TextWebSocketFrame(JT808MessageEncodeHelper.formatWebsocketMessage(message));
            channel.writeAndFlush(tws).sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
