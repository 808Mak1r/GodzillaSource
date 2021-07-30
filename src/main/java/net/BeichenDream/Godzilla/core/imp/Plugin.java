package net.BeichenDream.Godzilla.core.imp;

import net.BeichenDream.Godzilla.core.shell.ShellEntity;
import javax.swing.JPanel;

public interface Plugin {
    JPanel getView();

    void init(ShellEntity shellEntity);
}
