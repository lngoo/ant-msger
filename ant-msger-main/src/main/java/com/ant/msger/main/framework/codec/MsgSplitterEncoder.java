package com.ant.msger.main.framework.codec;

import com.ant.msger.base.dto.jt808.basics.Message;
import com.ant.msger.base.message.AbstractBody;
import com.ant.msger.base.message.AbstractMessage;
import com.ant.msger.main.framework.commons.constant.Constants;
import com.ant.msger.main.web.jt808.codec.JT808MessageEncoder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;

import java.util.LinkedList;
import java.util.List;

/**
 * 拆子包
 */
public class MsgSplitterEncoder<T extends AbstractBody> extends JT808MessageEncoder {

    public List<String> splitAndEncode(AbstractMessage<T> message) {
        List<ByteBuf> listBodyBufs = geneBodyBuffers(message);
        List<String> list = new LinkedList<>();
        int size = listBodyBufs.size();
        for (int i = 0; i < size; i++) {
            ByteBuf bodyBuf = listBodyBufs.get(i);
            // 更新message属性信息
            refreshMessageSummaryInfo((Message<T>) message, i, size, bodyBuf.readableBytes());
            ByteBuf headerBuf = encode(Unpooled.buffer(Constants.JT808_MSG_HEADER_LENGTH), message);
            ByteBuf buf = Unpooled.wrappedBuffer(headerBuf, bodyBuf);
            buf = sign(buf);
            buf = escape(buf);
            list.add(ByteBufUtil.hexDump(buf));
        }
        return list;
    }

    /**
     * @param message
     * @param paraIndex  从0开始计数的
     * @param bodyLength
     */
    private void refreshMessageSummaryInfo(Message<T> message, int paraIndex, int totalPara, int bodyLength) {
        if (totalPara == 1) {
            message.setSubPackage(false);
        } else {
            message.setSubPackage(true);
            message.setSubPackageTotal(totalPara);
            message.setSubPackageNumber(paraIndex + 1);
        }
        message.setBodyLength(bodyLength);
    }

    private List<ByteBuf> geneBodyBuffers(AbstractMessage<T> message) {
        AbstractBody body = message.getBody();
        ByteBuf bodyBuf = encode(Unpooled.buffer(), body);
        int length = bodyBuf.readableBytes();
        List<ByteBuf> list = new LinkedList<>();
        int remain = length;
        for (; remain > 0; ) {
            int readLength = remain > Constants.JT808_MSG_BODY_LENGTH ? Constants.JT808_MSG_BODY_LENGTH : remain;
            list.add(bodyBuf.readBytes(readLength));
            remain -= readLength;
        }
        return list;
    }
}
