package com.shuo.krpc.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * RPC Response
 * @author <a href="https://github.com/Kev1nWangsus">shuo</a>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RpcResponse implements Serializable {

    /**
     * response data
     */
    private Object data;

    /**
     * response data type
     */
    private Class<?> dataType;

    /**
     * response message
     */
    private String message;

    /**
     * exception
     */
    private Exception exception;
}
