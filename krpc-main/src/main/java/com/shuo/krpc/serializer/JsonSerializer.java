package com.shuo.krpc.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuo.krpc.model.RpcRequest;
import com.shuo.krpc.model.RpcResponse;

import java.io.IOException;

/**
 * Serializer JSON implementation
 *
 * @author <a href="https://github.com/Kev1nWangsus">shuo</a>
 */
public class JsonSerializer implements Serializer {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public <T> byte[] serialize(T object) throws IOException {
        return OBJECT_MAPPER.writeValueAsBytes(object);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> type) throws IOException {
        T object = OBJECT_MAPPER.readValue(bytes, type);

        if (object instanceof RpcRequest) {
            return handleRequest((RpcRequest) object, type);
        }

        if (object instanceof RpcResponse) {
            return handleResponse((RpcResponse) object, type);
        }
        return object;
    }

    /**
     * The original Object type would be converted to LinkedHashMap, which cannot be cast to original class.
     * Here we special case and assign correct class to each object.
     * @param rpcRequest
     * @param type
     * @return
     * @param <T>
     * @throws IOException
     */
    private <T> T handleRequest(RpcRequest rpcRequest, Class<T> type) throws IOException {
        Class<?>[] parameterTypes = rpcRequest.getParameterTypes();
        Object[] args = rpcRequest.getArgs();

        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> classType = parameterTypes[i];
            if (!classType.isAssignableFrom(args[i].getClass())) {
                byte[] argBytes = OBJECT_MAPPER.writeValueAsBytes(args[i]);
                args[i] = OBJECT_MAPPER.readValue(argBytes, classType);
            }
        }
        return type.cast(rpcRequest);
    }

    /**
     * The original Object type would be converted to LinkedHashMap, which cannot be cast to original class.
     * Here we special case and assign correct class to each object.
     * @param rpcResponse
     * @param type
     * @return
     * @param <T>
     * @throws IOException
     */
    private <T> T handleResponse(RpcResponse rpcResponse, Class<T> type) throws IOException {
        byte[] dataBytes = OBJECT_MAPPER.writeValueAsBytes(rpcResponse.getData());
        rpcResponse.setData(OBJECT_MAPPER.readValue(dataBytes, rpcResponse.getDataType()));
        return type.cast(rpcResponse);
    }
}
