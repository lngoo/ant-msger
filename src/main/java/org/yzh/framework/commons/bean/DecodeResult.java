package org.yzh.framework.commons.bean;

import io.netty.channel.socket.DatagramPacket;
import org.yzh.framework.message.AbstractBody;
import org.yzh.framework.message.AbstractMessage;

public class DecodeResult {
    private AbstractMessage<? extends AbstractBody> message;
    private DatagramPacket datagramPacket;

    public DecodeResult(AbstractMessage<? extends AbstractBody> message, DatagramPacket datagramPacket) {
        this.message = message;
        this.datagramPacket = datagramPacket;
    }

    public AbstractMessage<? extends AbstractBody> getMessage() {
        return message;
    }

    public void setMessage(AbstractMessage<? extends AbstractBody> message) {
        this.message = message;
    }

    public DatagramPacket getDatagramPacket() {
        return datagramPacket;
    }

    public void setDatagramPacket(DatagramPacket datagramPacket) {
        this.datagramPacket = datagramPacket;
    }
}
