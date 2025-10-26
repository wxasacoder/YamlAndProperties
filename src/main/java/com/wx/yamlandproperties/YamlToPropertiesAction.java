package com.wx.yamlandproperties;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.wx.yamlandproperties.core.YamlAndProperties;
import com.wx.yamlandproperties.notify.YamlAndPropertiesCommonNotify;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 *
 * @author wuxin
 * @date 2025/08/26 19:19:12
 *
 */
public class YamlToPropertiesAction extends AnAction {


    private static final Pattern YAML_PATTERN = Pattern.compile(".+(yaml|yml)");



    @Override
    public ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        VirtualFile data = anActionEvent.getData(CommonDataKeys.VIRTUAL_FILE);
        String name = data.getName();
        if(!YAML_PATTERN.matcher(name).matches()) {
            Messages.showInfoMessage("You must select a YAML file","Conversion failed");
            return;
        }
        String path = data.getPath();
        String whereFileIn = path.replace(name, "");
//            String yaml = whereFileIn + name.replace(".properties", ".yaml");
        String yamlFileName = UUID.randomUUID().toString().replace("-", "") + ".properties";
        String properties = whereFileIn + yamlFileName;
        try {
            InputStream inputStream = data.getInputStream();
            YamlAndProperties.convertYamlToProperties(new InputStreamReader(inputStream), new OutputStreamWriter(new FileOutputStream(properties)));
            VfsUtil.markDirtyAndRefresh(false, true, true, new File(whereFileIn));
            Notifications.Bus.notify(
                    new Notification(
                            "YamlAndProperties",
                            "Conversion successful",
                            "Generated successfully, please look in the same directory: "+yamlFileName,
                            NotificationType.INFORMATION
                    )
            );
        } catch (Exception e) {
            YamlAndPropertiesCommonNotify.processExpAsPopUp(e);
        }
    }

    @Override
    public void update(AnActionEvent e) {
        // 恒定展示
        e.getPresentation().setEnabledAndVisible(true);
    }
}
