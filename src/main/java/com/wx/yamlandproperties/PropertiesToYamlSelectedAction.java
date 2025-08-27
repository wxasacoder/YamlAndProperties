package com.wx.yamlandproperties;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.ui.Messages;
import com.wx.yamlandproperties.core.YamlAndProperties;
import org.jetbrains.annotations.NotNull;

import java.awt.datatransfer.StringSelection;
import java.io.StringReader;

/**
 *
 * @author wuxin
 * @date 2025/08/27 10:11:39
 *
 */
public class PropertiesToYamlSelectedAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        // 获取编辑器和选中文本
        Editor editor = anActionEvent.getRequiredData(CommonDataKeys.EDITOR);
        String selectedText = editor.getSelectionModel().getSelectedText();
        if (selectedText == null) return;
        try (StringReader reader = new StringReader(selectedText)){
            String yamlStr = YamlAndProperties.convertPropertiesToYaml(reader);
            CopyPasteManager.getInstance().setContents(new StringSelection(yamlStr));
            // 直接显示通知（标题、内容、类型）
            Notifications.Bus.notify(
                    new Notification(
                            "YamlAndProperties",  // 通知组 ID，可随意定义
                            "转换成功",      // 标题
                            "已复制到剪切板！", // 内容
                            NotificationType.INFORMATION // 类型：INFORMATION、WARNING、ERROR
                    )
            );
        } catch (Exception ex) {
            Messages.showInfoMessage(ex.getMessage() ,"转换失败");
        }
    }

@Override
public void update(@NotNull AnActionEvent e) {
    Editor editor = e.getData(CommonDataKeys.EDITOR);
    boolean hasSelection = editor != null && editor.getSelectionModel().hasSelection();
    e.getPresentation().setEnabledAndVisible(hasSelection);
}
}
