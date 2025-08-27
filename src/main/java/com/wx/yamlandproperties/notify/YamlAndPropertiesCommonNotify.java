package com.wx.yamlandproperties.notify;

import com.intellij.openapi.ui.Messages;
import com.wx.yamlandproperties.exceptions.YamlAndPropertiesRuntimeException;

/**
 *
 * @author wuxin
 * @date 2025/08/27 16:34:22
 *
 */
public class YamlAndPropertiesCommonNotify {

    public static void processExpAsPopUp(Exception ex){
        if(ex instanceof YamlAndPropertiesRuntimeException exp) {
            Messages.showInfoMessage(exp.getMessage(), "转换失败");
            return;
        }
        Messages.showInfoMessage("语法错误请检查","转换失败");
    }


}
