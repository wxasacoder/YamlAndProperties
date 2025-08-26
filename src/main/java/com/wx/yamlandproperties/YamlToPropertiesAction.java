package com.wx.yamlandproperties;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author wuxin
 * @date 2025/08/26 19:19:12
 *
 */
public class YamlToPropertiesAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        VirtualFile data = anActionEvent.getData(CommonDataKeys.VIRTUAL_FILE);
        System.out.println("现在是事件触发");
    }

    @Override
    public void update(AnActionEvent e) {
        // 恒定展示
        e.getPresentation().setEnabledAndVisible(true);
    }
}
