package net.BeichenDream.Godzilla.core.ui.component;

import net.BeichenDream.Godzilla.core.Db;
import net.BeichenDream.Godzilla.core.shell.ShellEntity;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import net.BeichenDream.Godzilla.util.Log;
import net.BeichenDream.Godzilla.util.functions;

public class ShellNote extends JPanel {
    private String lastNoteMd5;
    private String noteData;
    private ShellEntity shellEntity;
    private String shellId = this.shellEntity.getId();
    private boolean state;
    private RTextArea textArea;

    public ShellNote(ShellEntity entity) {
        this.shellEntity = entity;
        ShellNote.super.setLayout(new BorderLayout(1, 1));
        String noteData2 = Db.getShellNote(this.shellId);
        this.lastNoteMd5 = functions.md5(noteData2);
        this.textArea = new RTextArea();
        this.textArea.setText(noteData2);
        this.state = true;
        new Thread(new Runnable() {
            /* class core.ui.component.ShellNote.AnonymousClass1 */

            public void run() {
                while (ShellNote.this.state) {
                    try {
                        Thread.sleep(10000);
                        ShellNote.this.updateDbNote();
                    } catch (InterruptedException e) {
                        Log.error(e);
                    }
                }
            }
        }).start();
        ShellNote.super.add(new JScrollPane(this.textArea));
    }

    public void updateDbNote() {
        String noteData2 = this.textArea.getText();
        if (!this.lastNoteMd5.equals(functions.md5(noteData2))) {
            Db.updateShellNote(this.shellId, noteData2);
        }
    }
}
