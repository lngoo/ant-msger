package com.ant.msger.main.mq.publisher;

import com.ant.msger.base.dto.jt808.basics.Message;
import com.ant.msger.base.message.MessageExternal;
import com.ant.msger.main.framework.commons.enumeration.ProtocolBusiness;
import com.ant.msger.main.framework.commons.transform.JsonUtils;
import com.ant.msger.main.mq.util.LocalProtoBufUtil;
import com.ant.msger.main.mq.util.MessageConvertUtil;
import com.ant.msger.main.mq.util.ProtoBufUtil;
import com.antnest.msger.proto.ProtoMain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

@Component
public class RequestDataPublisher {
    private static final Logger LOG = LoggerFactory.getLogger("requestChannel");

    @Autowired
    RedisTemplate redisTemplate;

    @Value("${redis.key.queue.request.jt808}")
    String redisRequestChannelJt808;

    @Value("${redis.key.queue.request.im}")
    String redisRequestChannelIm;

    @Value("${system.msger.id}")
    String msgerId;

    /**
     * @param message
     */
    public void send(Message message, String sessionId, ProtocolBusiness protocol) {
        LOG.info("### start try send data to request chanel..., message={}", JsonUtils.toJson(message));
        try {
            if (null == message
                    || StringUtils.isEmpty(message.getMobileNumber())
                    || null == message.getType()
                    || null == message.getBody()) {
                LOG.warn("#### some data loss, can`t send to redis...stop all...");
                return;
            }

            MessageExternal external = MessageConvertUtil.toExternal(message, msgerId, sessionId);
            ProtoMain.Message.Builder builder = ProtoMain.Message.newBuilder();
            ProtoMain.Message messageProto = LocalProtoBufUtil.copyMessageExternalToProtoBean(external, builder);

            // 设置不使用redis的序列化
            redisTemplate.setEnableDefaultSerializer(false);
            redisTemplate.setValueSerializer(null);

            String redisKey = redisRequestChannelJt808;
            switch (protocol) {
                case AntIM:
                    redisKey = redisRequestChannelIm;
                    break;
            }
            redisTemplate.convertAndSend(redisKey, messageProto.toByteArray());
            LOG.info("### success to send data to redis chanel.key={}", redisKey);
        } catch (Exception e) {
            LOG.error("##### Exception when send data to request channel. stop all... {}", e);
        }
    }
}
