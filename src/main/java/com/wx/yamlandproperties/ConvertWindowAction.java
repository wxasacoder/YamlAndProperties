package com.wx.yamlandproperties;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.wx.yamlandproperties.gui.YamlAndPropertiesDialog;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author wuxin
 * @date 2025/08/27 11:50:14
 *
 */
public class ConvertWindowAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        YamlAndPropertiesDialog dialog = new YamlAndPropertiesDialog(e.getProject(), true);
        dialog.setSize(1200, 630);
        dialog.show();
    }
}
