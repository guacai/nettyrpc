package protocol;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

/**
 *
 *
 * @description:
 * @author: zhangtb
 */
public class ProSerializeUtil {


    public static <T> byte[] serialize(T source) {
        RuntimeSchema<T> schema;
        LinkedBuffer buffer = null;
        byte[] result;
        try {
            schema = RuntimeSchema.createFrom((Class<T>) source.getClass());
            buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
            result = ProtostuffIOUtil.toByteArray(source, schema, buffer);
        } catch (Exception e) {
            throw new RuntimeException("serialize exception");
        } finally {
            if (buffer != null) {
                buffer.clear();
            }
        }

        return result;
    }


    public static <T> T deserialize(byte[] source, Class<T> typeClass) {
        RuntimeSchema<T> schema;
        T newInstance;
        try {
            schema = RuntimeSchema.createFrom(typeClass);
            newInstance = typeClass.newInstance();
            ProtostuffIOUtil.mergeFrom(source, newInstance, schema);
        } catch (Exception e) {
            throw new RuntimeException("deserialize exception");
        }

        return newInstance;
    }
}
