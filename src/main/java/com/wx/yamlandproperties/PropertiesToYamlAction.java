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
 * @date 2025/08/26 20:21:10
 *
 */
public class PropertiesToYamlAction extends AnAction {

    Pattern PROPERTIES_FILE_SUFFIX = Pattern.compile(".+(\\.properties)");


    @Override
    public ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        VirtualFile data = e.getData(CommonDataKeys.VIRTUAL_FILE);
        try {
            String name = data.getName();
            if(!PROPERTIES_FILE_SUFFIX.matcher(name).matches()) {
                Messages.showInfoMessage("You must select a Properties file","Conversion failed");
                return;
            }
            String path = data.getPath();
            String whereFileIn = path.replace(name, "");
//            String yaml = whereFileIn + name.replace(".properties", ".yaml");
            String yamlFileName = UUID.randomUUID().toString().replace("-", "") + ".yml";
            String yaml = whereFileIn + yamlFileName;
            InputStream inputStream = data.getInputStream();
            FileOutputStream fileOutputStream = new FileOutputStream(yaml);
            YamlAndProperties.convertPropertiesToYaml(new InputStreamReader(inputStream), new OutputStreamWriter(fileOutputStream));
            VfsUtil.markDirtyAndRefresh(false, true, true, new File(whereFileIn));
            Notifications.Bus.notify(
                    new Notification(
                            "YamlAndProperties",
                            "Conversion successful",
                            "Generated successfully, please look in the same directory: "+yamlFileName,
                            NotificationType.INFORMATION
                    )
            );
        } catch (Exception ex) {
            YamlAndPropertiesCommonNotify.processExpAsPopUp(ex);
        }
    }
}
