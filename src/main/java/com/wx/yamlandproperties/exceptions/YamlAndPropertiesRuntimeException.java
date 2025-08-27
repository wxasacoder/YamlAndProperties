package com.wx.yamlandproperties.exceptions;

/**
 *
 * @author wuxin
 * @date 2025/08/27 16:31:51
 *
 */

public class YamlAndPropertiesRuntimeException extends RuntimeException{
    private String msg;
    public YamlAndPropertiesRuntimeException(String msg) {
        super(msg);
        this.msg = msg;
    }

}
