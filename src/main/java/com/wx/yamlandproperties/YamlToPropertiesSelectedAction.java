package com.wx.yamlandproperties;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ide.CopyPasteManager;
import com.wx.yamlandproperties.core.YamlAndProperties;
import com.wx.yamlandproperties.notify.YamlAndPropertiesCommonNotify;
import org.jetbrains.annotations.NotNull;

import java.awt.datatransfer.StringSelection;
import java.io.StringReader;

/**
 *
 * @author wuxin
 * @date 2025/08/27 11:22:03
 *
 */
public class YamlToPropertiesSelectedAction extends AnAction {



    @Override
    public ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        // 获取编辑器和选中文本
        Editor editor = anActionEvent.getData(CommonDataKeys.EDITOR);
//        Editor editor = anActionEvent.getRequiredData(CommonDataKeys.EDITOR);
        String selectedText = editor.getSelectionModel().getSelectedText();
        if (selectedText == null) return;
        try (StringReader reader = new StringReader(selectedText)){
            String yamlStr = YamlAndProperties.convertYamlToProperties(reader);
            CopyPasteManager.getInstance().setContents(new StringSelection(yamlStr));
            // 直接显示通知（标题、内容、类型）
            Notifications.Bus.notify(
                    new Notification(
                            "YamlAndProperties",
                            "Conversion successful",
                            "Copied to clipboard！",
                            NotificationType.INFORMATION
                    )
            );
        } catch (Exception ex) {
            YamlAndPropertiesCommonNotify.processExpAsPopUp(ex);
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        boolean hasSelection = editor != null && editor.getSelectionModel().hasSelection();
        e.getPresentation().setEnabledAndVisible(hasSelection);
    }
}
