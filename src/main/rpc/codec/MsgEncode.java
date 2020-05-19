package codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import protocol.ProSerializeUtil;

/**
 *
 *
 * @description:
 * @author: zhangtb
 */
public class MsgEncode extends MessageToByteEncoder {

    private Class<?> encodeClass;

    public MsgEncode(Class<?> encodeClass) {
        this.encodeClass = encodeClass;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if(encodeClass.isInstance(msg)){
            byte [] byteMsg = ProSerializeUtil.serialize(msg);
            out.writeBytes(byteMsg);
            out.writeInt(byteMsg.length);
        }

    }
}
