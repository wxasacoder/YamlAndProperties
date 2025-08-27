package com.wx.yamlandproperties.gui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.wx.yamlandproperties.core.YamlAndProperties;
import com.wx.yamlandproperties.notify.YamlAndPropertiesCommonNotify;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.StringReader;

public class YamlAndPropertiesDialog extends DialogWrapper {


    private JPanel container;
    private JButton clearButton;
    private JButton toPropertiesButton;
    private JButton toYamlButton;
    private JTextArea yamlArea;
    private JTextArea propertiesArea;

    public YamlAndPropertiesDialog(@Nullable Project project, boolean canBeParent) {
        super(project, canBeParent);
        setTitle("YamlAndProperties");
        init(); // 初始化对话框
    }


    @Override
    protected @Nullable JComponent createCenterPanel() {
        clearButton.addActionListener(e -> {
            yamlArea.setText(null);
            propertiesArea.setText(null);
        });
        toPropertiesButton.addActionListener(e -> {
            String text = yamlArea.getText();
            if(text == null || text.isBlank()){
                return;
            }
            try (var yamlReader = new StringReader(text)){
                String propertiesStr = YamlAndProperties.convertYamlToProperties(yamlReader);
                propertiesArea.setText(propertiesStr);
            }catch (Exception ex){
                YamlAndPropertiesCommonNotify.processExpAsPopUp(ex);
            }
        });
        toYamlButton.addActionListener(e -> {
            String text = propertiesArea.getText();
            if(text == null || text.isBlank()){
                return;
            }
            try (var propertiesReader = new StringReader(text)){
                String propertiesStr = YamlAndProperties.convertPropertiesToYaml(propertiesReader);
                yamlArea.setText(propertiesStr);
            }catch (Exception ex){
                YamlAndPropertiesCommonNotify.processExpAsPopUp(ex);
            }
        });
        return container;
    }



    @Override
    protected Action[] createActions() {
        return new Action[0];
    }
}