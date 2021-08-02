package net.BeichenDream.Godzilla.core.ui.component.dialog;

import com.formdev.flatlaf.demo.intellijthemes.IJThemeInfo;
import com.formdev.flatlaf.demo.intellijthemes.IJThemesPanel;
import net.BeichenDream.Godzilla.core.ApplicationContext;
import net.BeichenDream.Godzilla.core.Db;
import net.BeichenDream.Godzilla.core.ui.MainActivity;
import net.BeichenDream.Godzilla.core.ui.component.GBC;
import net.BeichenDream.Godzilla.core.ui.component.RTextArea;
import net.BeichenDream.Godzilla.core.ui.component.SimplePanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import net.BeichenDream.Godzilla.util.Log;
import net.BeichenDream.Godzilla.util.functions;

public class AppSeting extends JDialog {
    private static final HashMap<String, Class<?>> pluginSeting = new HashMap<>();
    private JButton bigFileConfigSaveButton;
    private JLabel bigFileErrorRetryNumLabel;
    private JTextField bigFileErrorRetryNumTextField;
    private SimplePanel bigFilePanel;
    private JLabel bigFileSendRequestSleepLabel;
    private JTextField bigFileSendRequestSleepTextField;
    private JPanel coreConfigPanel;
    private JLabel currentFontLLabel;
    private JLabel currentFontLabel;
    private JComboBox<String> fontNameComboBox;
    private JLabel fontNameLabel;
    private JComboBox<String> fontSizeComboBox;
    private JLabel fontSizeLabel;
    private JComboBox<String> fontTypeComboBox;
    private JLabel fontTypeLabel;
    private JPanel globallHttpHeaderPanel;
    private JCheckBox godModeCheckBox;
    private JLabel godModeLabel;
    private RTextArea headerTextArea;
    private JCheckBox isTipCheckBox;
    private JLabel isTipJLabel;
    private JLabel oneceBigFileDownloadByteNumLabel;
    private JTextField oneceBigFileDownloadByteNumTextField;
    private JLabel oneceBigFileUploadByteNumLabel;
    private JTextField oneceBigFileUploadByteNumTextField;
    private JButton resetFontButton;
    private JPanel setFontPanel;
    private JTabbedPane tabbedPane = new JTabbedPane();
    private JLabel testFontLabel;
    private IJThemesPanel themesPanel;
    private JSplitPane themesSplitPane;
    private JButton updateFontButton;
    private JButton updateHeaderButton;
    private JButton updateThemesButton;

     
    public AppSeting() {
         
        throw new UnsupportedOperationException("Method not decompiled: core.ui.component.dialog.AppSeting.<init>():void");
    }

    private   void lambda$new$0(String k) {
        try {
            this.tabbedPane.addTab(k, (JPanel) pluginSeting.get(k).newInstance());
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
        }
    }

     
    public void initSetFontPanel() {
        Font currentFont = ApplicationContext.getFont();
        this.setFontPanel = new JPanel(new GridBagLayout());
        this.fontNameComboBox = new JComboBox<>(getAllFontName());
        this.fontTypeComboBox = new JComboBox<>(getAllFontType());
        this.fontSizeComboBox = new JComboBox<>(getAllFontSize());
        this.testFontLabel = new JLabel("你好\tHello");
        this.currentFontLabel = new JLabel(functions.toString(currentFont));
        this.currentFontLLabel = new JLabel("当前字体 : ");
        this.updateFontButton = new JButton("修改");
        this.resetFontButton = new JButton("重置");
        this.fontNameLabel = new JLabel("字体:    ");
        this.fontTypeLabel = new JLabel("字体类型 : ");
        this.fontSizeLabel = new JLabel("字体大小 : ");
        GBC gbcLFontName = new GBC(0, 0).setInsets(5, -40, 0, 0);
        GBC gbcFontName = new GBC(1, 0, 3, 1).setInsets(5, 20, 0, 0);
        GBC gbcLFontType = new GBC(0, 1).setInsets(5, -40, 0, 0);
        GBC gbcFontType = new GBC(1, 1, 3, 1).setInsets(5, 20, 0, 0);
        GBC gbcLFontSize = new GBC(0, 2).setInsets(5, -40, 0, 0);
        GBC gbcFontSize = new GBC(1, 2, 3, 1).setInsets(5, 20, 0, 0);
        GBC gbcLCurrentFont = new GBC(0, 3).setInsets(5, -40, 0, 0);
        GBC gbcCurrentFont = new GBC(1, 3, 3, 1).setInsets(5, 20, 0, 0);
        GBC gbcTestFont = new GBC(0, 4);
        GBC gbcUpdateFont = new GBC(2, 5).setInsets(5, -40, 0, 0);
        GBC gbcResetFont = new GBC(1, 5, 3, 1).setInsets(5, 20, 0, 0);
        this.setFontPanel.add(this.fontNameLabel, gbcLFontName);
        this.setFontPanel.add(this.fontNameComboBox, gbcFontName);
        this.setFontPanel.add(this.fontTypeLabel, gbcLFontType);
        this.setFontPanel.add(this.fontTypeComboBox, gbcFontType);
        this.setFontPanel.add(this.fontSizeLabel, gbcLFontSize);
        this.setFontPanel.add(this.fontSizeComboBox, gbcFontSize);
        this.setFontPanel.add(this.currentFontLLabel, gbcLCurrentFont);
        this.setFontPanel.add(this.currentFontLabel, gbcCurrentFont);
        this.setFontPanel.add(this.testFontLabel, gbcTestFont);
        this.setFontPanel.add(this.updateFontButton, gbcUpdateFont);
        this.setFontPanel.add(this.resetFontButton, gbcResetFont);
        this.fontNameComboBox.addActionListener(new ActionListener() {
             

            public void actionPerformed(ActionEvent paramActionEvent) {
                AppSeting.this.testFontLabel.setFont(AppSeting.this.getSelectFont());
            }
        });
        this.fontTypeComboBox.addActionListener(new ActionListener() {
             

            public void actionPerformed(ActionEvent paramActionEvent) {
                AppSeting.this.testFontLabel.setFont(AppSeting.this.getSelectFont());
            }
        });
        this.fontSizeComboBox.addActionListener(new ActionListener() {
             

            public void actionPerformed(ActionEvent paramActionEvent) {
                AppSeting.this.testFontLabel.setFont(AppSeting.this.getSelectFont());
            }
        });
        if (currentFont != null) {
            this.fontNameComboBox.setSelectedItem(currentFont.getName());
            this.fontTypeComboBox.setSelectedItem(getFontType(currentFont.getStyle()));
            this.fontSizeComboBox.setSelectedItem(Integer.toString(currentFont.getSize()));
            this.testFontLabel.setFont(currentFont);
        }
    }

    public static void registerPluginSeting(String tabName, Class<?> panelClass) {
        pluginSeting.put(tabName, panelClass);
    }

     
    public void initGloballHttpHeader() {
        this.globallHttpHeaderPanel = new JPanel(new BorderLayout(1, 1));
        this.headerTextArea = new RTextArea();
        this.updateHeaderButton = new JButton("修改");
        this.headerTextArea.setText(ApplicationContext.getGloballHttpHeader());
        Dimension dimension = new Dimension();
        dimension.height = 30;
        JSplitPane splitPane = new JSplitPane();
        splitPane.setOrientation(0);
        JPanel bottomPanel = new JPanel();
        splitPane.setTopComponent(new JScrollPane(this.headerTextArea));
        bottomPanel.add(this.updateHeaderButton);
        bottomPanel.setMaximumSize(dimension);
        bottomPanel.setMinimumSize(dimension);
        splitPane.setBottomComponent(bottomPanel);
        splitPane.setResizeWeight(0.9d);
        this.globallHttpHeaderPanel.add(splitPane);
    }

     
    public void initCoreConfigPanel() {
        this.coreConfigPanel = new JPanel(new GridBagLayout());
        this.godModeLabel = new JLabel("运行模式: ");
        this.godModeCheckBox = new JCheckBox("上帝模式", ApplicationContext.isGodMode());
        this.isTipJLabel = new JLabel("提示语");
        this.isTipCheckBox = new JCheckBox("开启", functions.toBoolean(Db.getSetingValue("AppIsTip")));
        GBC gbcLGodMode = new GBC(0, 0).setInsets(5, -40, 0, 0);
        GBC gbcGodMode = new GBC(1, 0, 3, 1).setInsets(5, 20, 0, 0);
        GBC gbcLIsTip = new GBC(0, 1).setInsets(5, -40, 0, 0);
        GBC gbcIsTip = new GBC(1, 1, 3, 1).setInsets(5, 20, 0, 0);
        this.coreConfigPanel.add(this.godModeLabel, gbcLGodMode);
        this.coreConfigPanel.add(this.godModeCheckBox, gbcGodMode);
        this.coreConfigPanel.add(this.isTipJLabel, gbcLIsTip);
        this.coreConfigPanel.add(this.isTipCheckBox, gbcIsTip);
        this.isTipCheckBox.addActionListener(new ActionListener() {
             

            public void actionPerformed(ActionEvent e) {
                if (Db.updateSetingKV("AppIsTip", Boolean.toString(AppSeting.this.isTipCheckBox.isSelected()))) {
                    JOptionPane.showMessageDialog(AppSeting.this, "修改成功!", "提示", 1);
                } else {
                    JOptionPane.showMessageDialog(AppSeting.this, "修改失败!", "提示", 2);
                }
            }
        });
        this.godModeCheckBox.addActionListener(new ActionListener() {
             

            public void actionPerformed(ActionEvent e) {
                if (ApplicationContext.setGodMode(AppSeting.this.godModeCheckBox.isSelected())) {
                    JOptionPane.showMessageDialog(AppSeting.this, "修改成功!", "提示", 1);
                } else {
                    JOptionPane.showMessageDialog(AppSeting.this, "修改失败!", "提示", 2);
                }
            }
        });
    }

     
    public void initBigFilePanel() {
        this.bigFilePanel = new SimplePanel();
        this.bigFileErrorRetryNumLabel = new JLabel("错误重试最大次数: ");
        this.bigFileSendRequestSleepLabel = new JLabel("请求抖动延时(ms)");
        this.oneceBigFileDownloadByteNumLabel = new JLabel("下载单次读取字节: ");
        this.oneceBigFileUploadByteNumLabel = new JLabel("上传单次读取字节: ");
        this.oneceBigFileDownloadByteNumTextField = new JTextField(String.valueOf(Db.getSetingIntValue("oneceBigFileDownloadByteNum", 1048576)), 10);
        this.oneceBigFileUploadByteNumTextField = new JTextField(String.valueOf(Db.getSetingIntValue("oneceBigFileUploadByteNum", 1048576)), 10);
        this.bigFileErrorRetryNumTextField = new JTextField(String.valueOf(Db.getSetingIntValue("bigFileErrorRetryNum", 10)));
        this.bigFileSendRequestSleepTextField = new JTextField(String.valueOf(Db.getSetingIntValue("bigFileSendRequestSleep", 521)));
        this.bigFileConfigSaveButton = new JButton("保存配置");
        this.bigFilePanel.setSetup(-270);
        this.bigFilePanel.addX(this.bigFileErrorRetryNumLabel, this.bigFileErrorRetryNumTextField);
        this.bigFilePanel.addX(this.bigFileSendRequestSleepLabel, this.bigFileSendRequestSleepTextField);
        this.bigFilePanel.addX(this.oneceBigFileDownloadByteNumLabel, this.oneceBigFileDownloadByteNumTextField);
        this.bigFilePanel.addX(this.oneceBigFileUploadByteNumLabel, this.oneceBigFileUploadByteNumTextField);
        this.bigFilePanel.addX(this.bigFileConfigSaveButton);
    }

    public void initThemesPanel() {
        this.themesPanel = new IJThemesPanel();
        this.updateThemesButton = new JButton("修改");
        this.themesSplitPane = new JSplitPane(0);
        this.themesSplitPane.setBottomComponent(this.updateThemesButton);
        this.themesSplitPane.setTopComponent(this.themesPanel);
        this.themesSplitPane.setResizeWeight(0.99d);
    }

    public Font getSelectFont() {
        try {
            return new Font((String) this.fontNameComboBox.getSelectedItem(), Font.class.getDeclaredField((String) this.fontTypeComboBox.getSelectedItem()).getInt(null), Integer.parseInt((String) this.fontSizeComboBox.getSelectedItem()));
        } catch (Exception e) {
            Log.error(e);
            return null;
        }
    }

    public String getFontType(int type) {
        try {
            Field[] fields = Font.class.getDeclaredFields();
            for (Field field : fields) {
                if (field.getType().isAssignableFrom(Integer.TYPE) && field.getName().indexOf("_") == -1 && field.getModifiers() == 25 && field.getInt(null) == type) {
                    return field.getName();
                }
            }
            return null;
        } catch (Exception e) {
            Log.error(e);
            return null;
        }
    }

    public static String[] getAllFontName() {
        ArrayList<String> arrayList = new ArrayList<>();
        for (Font font : GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts()) {
            arrayList.add(font.getFontName());
        }
        return (String[]) arrayList.toArray(new String[0]);
    }

    public static String[] getAllFontType() {
        ArrayList<String> arrayList = new ArrayList<>();
        Field[] fields = Font.class.getDeclaredFields();
        for (Field field : fields) {
            if (field.getType().isAssignableFrom(Integer.TYPE) && field.getName().indexOf("_") == -1 && field.getModifiers() == 25) {
                arrayList.add(field.getName());
            }
        }
        return (String[]) arrayList.toArray(new String[0]);
    }

    public static String[] getAllFontSize() {
        ArrayList<String> arrayList = new ArrayList<>();
        for (int i = 8; i < 48; i++) {
            arrayList.add(Integer.toString(i));
        }
        return (String[]) arrayList.toArray(new String[0]);
    }

    private void updateFontButtonClick(ActionEvent actionEvent) {
        ApplicationContext.setFont(getSelectFont());
        JOptionPane.showMessageDialog(this, "修改成功! 重启程序生效!", "提示", 1);
    }
 
     
    private void resetFontButtonClick(ActionEvent actionEvent) {
        ApplicationContext.resetFont();
        JOptionPane.showMessageDialog(this, "重置成功! 重启程序生效!", "提示", 1);
    }

    private void updateHeaderButtonClick(ActionEvent actionEvent) {
        if (ApplicationContext.updateGloballHttpHeader(this.headerTextArea.getText())) {
            JOptionPane.showMessageDialog(this, "修改成功!", "提示", 1);
        } else {
            JOptionPane.showMessageDialog(this, "修改失败!", "提示", 2);
        }
    }

    private void updateThemesButtonClick(ActionEvent actionEvent) {
        IJThemeInfo ijThemeInfo = this.themesPanel.getSelect();
        if (ijThemeInfo == null || !ApplicationContext.saveUi(ijThemeInfo)) {
            JOptionPane.showMessageDialog(this, "修改失败!", "提示", 2);
        } else {
            JOptionPane.showMessageDialog(this, "修改成功!", "提示", 1);
        }
    }

    private void bigFileConfigSaveButtonClick(ActionEvent actionEvent) throws Exception {
        Db.updateSetingKV("oneceBigFileDownloadByteNum", this.oneceBigFileDownloadByteNumTextField.getText().trim());
        Db.updateSetingKV("oneceBigFileUploadByteNum", this.oneceBigFileUploadByteNumTextField.getText().trim());
        Db.updateSetingKV("bigFileErrorRetryNum", String.valueOf(this.bigFileErrorRetryNumTextField.getText().trim()));
        Db.updateSetingKV("bigFileSendRequestSleep", String.valueOf(this.bigFileSendRequestSleepTextField.getText().trim()));
        JOptionPane.showMessageDialog(this, "Succes!", "提示", 1);
    }
}
