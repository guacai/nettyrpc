package codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import protocol.ProSerializeUtil;

import java.util.List;

/**
 *
 *
 * @description:
 * @author: zhangtb
 */
public class MSgDecode extends ByteToMessageDecoder {

    private Class<?> decodeClass;

    public MSgDecode(Class<?> decodeClass) {
        this.decodeClass = decodeClass;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if(in.readableBytes() < 4){
            return;
        }
        in.markReaderIndex();
        int length = in.readInt();
        if(in.readableBytes() < length){
            return;
        }
        byte [] byteMsg = new byte[length];
        in.readBytes(byteMsg);
        ProSerializeUtil.deserialize(byteMsg, decodeClass);
        out.add(byteMsg);
    }
}
