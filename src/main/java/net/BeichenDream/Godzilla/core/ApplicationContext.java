package net.BeichenDream.Godzilla.core;

import com.formdev.flatlaf.demo.intellijthemes.IJThemeInfo;
import com.formdev.flatlaf.demo.intellijthemes.IJThemesPanel;
import com.formdev.flatlaf.util.SystemInfo;
import net.BeichenDream.Godzilla.core.annotation.CryptionAnnotation;
import net.BeichenDream.Godzilla.core.annotation.PayloadAnnotation;
import net.BeichenDream.Godzilla.core.annotation.PluginAnnotation;
import net.BeichenDream.Godzilla.core.imp.Cryption;
import net.BeichenDream.Godzilla.core.imp.Payload;
import net.BeichenDream.Godzilla.core.imp.Plugin;
import net.BeichenDream.Godzilla.core.shell.ShellEntity;
import net.BeichenDream.Godzilla.util.Log;
import net.BeichenDream.Godzilla.util.functions;
import net.BeichenDream.Godzilla.util.http.Http;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.Proxy;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ApplicationContext {
    public static final String VERSION = "3.03";
    private static HashMap<String, Class<?>> cryptionMap = new HashMap<>();
    public static boolean easterEgg = true;
    private static Font font;
    private static Map<String, String> headerMap;
    public static ThreadLocal<Boolean> isShowHttpProgressBar = new ThreadLocal<>();
    private static HashMap<String, Class<?>> payloadMap = new HashMap<>();
    private static File[] pluginJarFiles;
    private static HashMap<String, Class<?>> pluginMap = new HashMap<>();
    public static int windowWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
    public static int windowsHeight = Toolkit.getDefaultToolkit().getScreenSize().height;

    protected ApplicationContext() {
    }

    public static void init() {
        initFont();
        initHttpHeader();
        scanPluginJar();
        scanPayload();
        scanCryption();
        scanPlugin();
    }

    private static void initFont() {
        String fontName = Db.getSetingValue("font-name");
        String fontType = Db.getSetingValue("font-type");
        String fontSize = Db.getSetingValue("font-size");
        if (fontName != null && fontType != null && fontSize != null) {
            font = new Font(fontName, Integer.parseInt(fontType), Integer.parseInt(fontSize));
            InitGlobalFont(font);
        }
    }

    private static void initHttpHeader() {
        int index;
        String headerString = getGloballHttpHeader();
        if (headerString != null) {
            String[] reqLines = headerString.split("\n");
            headerMap = new Hashtable();
            for (int i = 0; i < reqLines.length; i++) {
                if (!reqLines[i].trim().isEmpty() && (index = reqLines[i].indexOf(":")) > 1) {
                    headerMap.put(reqLines[i].substring(0, index).trim(), reqLines[i].substring(index + 1, reqLines[i].length()).trim());
                }
            }
        }
    }

    private static void scanPayload() {
        try {
            Log.log(String.format("load payload success! payloadMaxNum:%s onceLoadPayloadNum:%s", Integer.valueOf(payloadMap.size()), Integer.valueOf(scanClass(ApplicationContext.class.getResource("/shells/payloads/").toURI(), "shells.payloads", Payload.class, PayloadAnnotation.class, payloadMap))), new Object[0]);
        } catch (Exception e) {
            Log.error(e);
        }
    }

    private static void scanCryption() {
        try {
            Log.log(String.format("load cryption success! cryptionMaxNum:%s onceLoadCryptionNum:%s", Integer.valueOf(cryptionMap.size()), Integer.valueOf(scanClass(ApplicationContext.class.getResource("/shells/cryptions/").toURI(), "shells.cryptions", Cryption.class, CryptionAnnotation.class, cryptionMap))), new Object[0]);
        } catch (Exception e) {
            Log.error(e);
        }
    }

    private static void scanPlugin() {
        try {
            Log.log(String.format("load plugin success! pluginMaxNum:%s onceLoadPluginNum:%s", Integer.valueOf(pluginMap.size()), Integer.valueOf(scanClass(ApplicationContext.class.getResource("/shells/plugins/").toURI(), "shells.plugins", Plugin.class, PluginAnnotation.class, pluginMap))), new Object[0]);
        } catch (Exception e) {
            Log.error(e);
        }
    }

    private static void scanPluginJar() {
        String[] pluginJars = Db.getAllPlugin();
        ArrayList list = new ArrayList();
        for (int i = 0; i < pluginJars.length; i++) {
            File jarFile = new File(pluginJars[i]);
            if (!jarFile.exists() || !jarFile.isFile()) {
                Log.error(String.format("PluginJarFile : %s no found", pluginJars[i]));
            } else {
                addJar(jarFile);
                list.add(jarFile);
            }
        }
        pluginJarFiles = (File[]) list.toArray(new File[0]);
        Log.log(String.format("load pluginJar success! pluginJarNum:%s LoadPluginJarSuccessNum:%s", Integer.valueOf(pluginJars.length), Integer.valueOf(pluginJars.length)), new Object[0]);
    }

    private static int scanClass(URI uri, String packageName, Class<?> parentClass, Class<?> annotationClass, Map<String, Class<?>> destMap) {
        int num = scanClassX(uri, packageName, parentClass, annotationClass, destMap);
        for (int i = 0; i < pluginJarFiles.length; i++) {
            num += scanClassByJar(pluginJarFiles[i], packageName, parentClass, annotationClass, destMap);
        }
        return num;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r29v0, resolved type: java.lang.Class<?> */
    /* JADX WARN: Multi-variable type inference failed */
    private static int scanClassX(URI uri, String packageName, Class<?> parentClass, Class<?> annotationClass, Map<String, Class<?>> destMap) {
        String jarFileString = functions.getJarFileByClass(ApplicationContext.class);
        if (jarFileString != null) {
            return scanClassByJar(new File(jarFileString), packageName, parentClass, annotationClass, destMap);
        }
        int addNum = 0;
        try {
            File[] file2 = new File(uri).listFiles();
            for (File objectFile : file2) {
                if (objectFile.isDirectory()) {
                    File[] objectFiles = objectFile.listFiles();
                    for (File objectClassFile : objectFiles) {
                        if (objectClassFile.getPath().endsWith(".class")) {
                            try {
                                Class objectClass = Class.forName(String.format("%s.%s.%s", packageName, objectFile.getName(), objectClassFile.getName().substring(0, objectClassFile.getName().length() - ".class".length())));
                                if (parentClass.isAssignableFrom(objectClass) && objectClass.isAnnotationPresent(annotationClass)) {
                                    Annotation annotation = objectClass.getAnnotation(annotationClass);
                                    destMap.put((String) annotation.annotationType().getMethod("Name", new Class[0]).invoke(annotation, null), objectClass);
                                    addNum++;
                                }
                            } catch (Exception e) {
                                Log.error(e);
                            }
                        }
                    }
                }
            }
            return addNum;
        } catch (Exception e2) {
            Log.error(e2);
            return 0;
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for r16v0, resolved type: java.lang.Class<?> */
    /* JADX WARN: Multi-variable type inference failed */
    private static int scanClassByJar(File srcJarFile, String packageName, Class<?> parentClass, Class<?> annotationClass, Map<String, Class<?>> destMap) {
        int addNum = 0;
        try {
            JarFile jarFile = new JarFile(srcJarFile);
            Enumeration<JarEntry> jarFiles = jarFile.entries();
            String packageName2 = packageName.replace(".", "/").substring(0);
            while (jarFiles.hasMoreElements()) {
                String name = jarFiles.nextElement().getName();
                if (name.startsWith(packageName2) && name.endsWith(".class")) {
                    String name2 = name.replace("/", ".");
                    try {
                        Class objectClass = Class.forName(name2.substring(0, name2.length() - 6));
                        if (parentClass.isAssignableFrom(objectClass) && objectClass.isAnnotationPresent(annotationClass)) {
                            Annotation annotation = objectClass.getAnnotation(annotationClass);
                            destMap.put((String) annotation.annotationType().getMethod("Name", new Class[0]).invoke(annotation, null), objectClass);
                            addNum++;
                        }
                    } catch (Exception e) {
                        Log.error(e);
                    }
                }
            }
            jarFile.close();
        } catch (Exception e2) {
            Log.error(e2);
        }
        return addNum;
    }

    public static String[] getAllPayload() {
        return (String[]) payloadMap.keySet().toArray(new String[0]);
    }

    public static Payload getPayload(String payloadName) {
        Class<?> payloadClass = payloadMap.get(payloadName);
        if (payloadClass == null) {
            return null;
        }
        try {
            return (Payload) payloadClass.newInstance();
        } catch (Exception e) {
            Log.error(e);
            return null;
        }
    }

    public static Plugin[] getAllPlugin(String payloadName) {
        ArrayList<Plugin> list = new ArrayList<>();
        for (String cryptionName : pluginMap.keySet()) {
            Class<?> pluginClass = pluginMap.get(cryptionName);
            if (pluginClass != null && ((PluginAnnotation) pluginClass.getAnnotation(PluginAnnotation.class)).payloadName().equals(payloadName)) {
                try {
                    list.add((Plugin) pluginClass.newInstance());
                } catch (Exception e) {
                    Log.error(e);
                }
            }
        }
        return (Plugin[]) list.toArray(new Plugin[0]);
    }

    public static String[] getAllCryption(String payloadName) {
        ArrayList<String> list = new ArrayList<>();
        for (String cryptionName : cryptionMap.keySet()) {
            Class<?> cryptionClass = cryptionMap.get(cryptionName);
            if (cryptionClass != null && ((CryptionAnnotation) cryptionClass.getAnnotation(CryptionAnnotation.class)).payloadName().equals(payloadName)) {
                list.add(cryptionName);
            }
        }
        return (String[]) list.toArray(new String[0]);
    }

    public static Cryption getCryption(String payloadName, String crytionName) {
        Class<?> cryptionClass = cryptionMap.get(crytionName);
        if (cryptionMap == null || !((CryptionAnnotation) cryptionClass.getAnnotation(CryptionAnnotation.class)).payloadName().equals(payloadName)) {
            return null;
        }
        try {
            return (Cryption) cryptionClass.newInstance();
        } catch (Exception e) {
            Log.error(e);
            return null;
        }
    }

    private static void addJar(File jarPath) {
        try {
            URLClassLoader classLoader = (URLClassLoader) ApplicationContext.class.getClassLoader();
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            method.invoke(classLoader, jarPath.toURI().toURL());
        } catch (Exception e) {
            Log.error(e);
        }
    }

    private static void InitGlobalFont(Font font2) {
        FontUIResource fontRes = new FontUIResource(font2);
        Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            if (UIManager.get(key) instanceof FontUIResource) {
                UIManager.put(key, fontRes);
            }
        }
    }

    public static Proxy getProxy(ShellEntity shellContext) {
        return ProxyT.getProxy(shellContext);
    }

    public static String[] getAllProxy() {
        return ProxyT.getAllProxyType();
    }

    public static String[] getAllEncodingTypes() {
        return Encoding.getAllEncodingTypes();
    }

    public static Http getHttp(ShellEntity shellEntity) {
        return new Http(shellEntity);
    }

    public static Font getFont() {
        return font;
    }

    public static void setFont(Font font2) {
        Db.updateSetingKV("font-name", font2.getName());
        Db.updateSetingKV("font-type", Integer.toString(font2.getStyle()));
        Db.updateSetingKV("font-size", Integer.toString(font2.getSize()));
        font = font2;
    }

    public static void resetFont() {
        Db.removeSetingK("font-name");
        Db.removeSetingK("font-type");
        Db.removeSetingK("font-size");
    }

    public static String getGloballHttpHeader() {
        return Db.getSetingValue("globallHttpHeader");
    }

    public static Map<String, String> getGloballHttpHeaderX() {
        return headerMap;
    }

    public static boolean updateGloballHttpHeader(String header) {
        boolean state = Db.updateSetingKV("globallHttpHeader", header);
        initHttpHeader();
        return state;
    }

    public static boolean isGodMode() {
        return Boolean.valueOf(Db.getSetingValue("godMode")).booleanValue();
    }

    public static boolean setGodMode(boolean state) {
        return Db.updateSetingKV("godMode", String.valueOf(state));
    }

    public static void initUi() {
        if (SystemInfo.isMacOS && System.getProperty("apple.laf.useScreenMenuBar") == null) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
        }
        UIManager.put("Table.showHorizontalLines", true);
        UIManager.put("Table.showVerticalLines", true);
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);
        String resourceNameString = Db.getSetingValue("ui-resourceName");
        String lafClassNameString = Db.getSetingValue("ui-lafClassName");
        if (resourceNameString == null && lafClassNameString == null) {
            Db.updateSetingKV("ui-lafClassName", "com.formdev.flatlaf.FlatIntelliJLaf");
        }
        IJThemesPanel.setTheme(new IJThemeInfo(resourceNameString, Db.getSetingValue("ui-lafClassName")));
    }

    public static boolean saveUi(IJThemeInfo themeInfo) {
        try {
            String resourceNameString = themeInfo.getResourceName();
            String lafClassNameString = themeInfo.getLafClassName();
            if (resourceNameString != null && lafClassNameString == null) {
                Db.updateSetingKV("ui-resourceName", resourceNameString);
                Db.removeSetingK("ui-lafClassName");
            }
            if (lafClassNameString != null && resourceNameString == null) {
                Db.updateSetingKV("ui-lafClassName", lafClassNameString);
                Db.removeSetingK("ui-resourceName");
            }
            if (lafClassNameString == null && resourceNameString == null) {
                return false;
            }
            return true;
        } catch (Exception e) {
            Log.error(e);
            return false;
        }
    }

    public static boolean isOpenC(String k) {
        return Boolean.valueOf(Db.getSetingValue(k)).booleanValue();
    }

    public static boolean setOpenC(String k, boolean state) {
        return Db.updateSetingKV(k, String.valueOf(state));
    }
}
