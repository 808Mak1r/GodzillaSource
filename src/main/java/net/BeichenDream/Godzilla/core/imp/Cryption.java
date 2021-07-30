package net.BeichenDream.Godzilla.core.imp;

import net.BeichenDream.Godzilla.core.shell.ShellEntity;

public interface Cryption {
    boolean check();

    byte[] decode(byte[] bArr);

    byte[] encode(byte[] bArr);

    byte[] generate(String str, String str2);

    void init(ShellEntity shellEntity);

    boolean isSendRLData();
}
