package util;

import core.ApplicationContext;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JComboBox;
import util.http.Http;

public class functions {
    private static final double CURRENT_HEIGHT;
    private static final double CURRENT_WIDTH;
    private static final double TOOLSKIT_HEIGHT = 1080.0d;
    private static final double TOOLSKIT_WIDTH = 1920.0d;
    private static final char[] toBase64 = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};
    private static final char[] toBase64URL = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_'};

    static {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        if (1080.0D < screenSize.getHeight() || 1920.0D < screenSize.getWidth()) {
            CURRENT_WIDTH = 1920.0D;
            CURRENT_HEIGHT = 1080.0D;
        } else {
            CURRENT_WIDTH = screenSize.getWidth();
            CURRENT_HEIGHT = screenSize.getHeight();
        }
    }

    public static void concatMap(Map<String, List<String>> receiveMap, Map<String, List<String>> map) {
        for (String key : map.keySet()) {
            receiveMap.put(key, map.get(key));
        }
    }

    public static void setWindowSize(Window window, int width, int height) {
        window.setSize((int) ((((double) width) / TOOLSKIT_WIDTH) * CURRENT_WIDTH), (int) ((((double) height) / TOOLSKIT_HEIGHT) * CURRENT_HEIGHT));
    }

    public static void fireActionEventByJComboBox(JComboBox comboBox) {
        try {
            comboBox.setSelectedIndex(0);
        } catch (Exception e) {
            Log.error(e);
        }
    }

    public static byte[] gzipE(byte[] data) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream);
            gzipOutputStream.write(data);
            gzipOutputStream.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] gzipD(byte[] data) {
        if (data.length == 0) {
            return data;
        }
        try {
            return readInputStream(new GZIPInputStream(new ByteArrayInputStream(data), data.length));
        } catch (Exception e) {
            if (data.length < 200) {
                Log.error(new String(data));
            }
            throw new RuntimeException(e);
        }
    }

    public static boolean isGzipStream(byte[] data) {
        if (data == null || data.length < 2) {
            return false;
        }
        return ((data[0] & 255) | ((data[1] & 255) << 8)) == 35615;
    }

    public static int bytesToInt(byte[] bytes) {
        return (bytes[0] & 255) | ((bytes[1] & 255) << 8) | ((bytes[2] & 255) << 16) | ((bytes[3] & 255) << 24);
    }

    public static boolean isMatch(String s, String p, boolean us) {
        if (us) {
            return isMatch(s, p);
        }
        return isMatch(s.toLowerCase(), p.toLowerCase());
    }

    public static boolean isMatch(String s, String p) {
        int i = 0;
        int j = 0;
        int starIndex = -1;
        int iIndex = -1;
        while (i < s.length()) {
            if (j < p.length() && (p.charAt(j) == '?' || p.charAt(j) == s.charAt(i))) {
                i++;
                j++;
            } else if (j < p.length() && p.charAt(j) == '*') {
                starIndex = j;
                iIndex = i;
                j++;
            } else if (starIndex == -1) {
                return false;
            } else {
                j = starIndex + 1;
                i = iIndex + 1;
                iIndex++;
            }
        }
        while (j < p.length() && p.charAt(j) == '*') {
            j++;
        }
        if (j == p.length()) {
            return true;
        }
        return false;
    }

    public static boolean toBoolean(String s) {
        try {
            return Boolean.parseBoolean(s);
        } catch (Exception e) {
            Log.error(e);
            return false;
        }
    }

    public static Long stringToLong(String intString, long defaultValue) {
        try {
            return Long.valueOf(Long.parseLong(intString.trim()));
        } catch (Exception e) {
            return Long.valueOf(defaultValue);
        }
    }

    public static int byteToInt2(byte[] b) {
        int n = 0;
        for (byte b2 : b) {
            n = (n << 8) | (b2 & 255);
        }
        return n;
    }

    public static byte[] ipToByteArray(String paramString) {
        String[] array2 = paramString.split("\\.");
        byte[] array = new byte[4];
        for (int i = 0; i < array2.length; i++) {
            array[i] = (byte) Integer.parseInt(array2[i]);
        }
        return array;
    }

    public static byte[] shortToByteArray(short s) {
        byte[] targets = new byte[2];
        for (int i = 0; i < 2; i++) {
            targets[i] = (byte) ((s >>> (((targets.length - 1) - i) * 8)) & 255);
        }
        return targets;
    }

    public static byte[] intToBytes(int value) {
        return new byte[]{(byte) (value & 255), (byte) ((value >> 8) & 255), (byte) ((value >> 16) & 255), (byte) ((value >> 24) & 255)};
    }

    public static String byteArrayToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder("");
        for (byte b : bytes) {
            String strHex = Integer.toHexString(b & 255);
            sb.append(strHex.length() == 1 ? "0" + strHex : strHex);
        }
        return sb.toString().trim();
    }

    public static byte[] hexToByte(String hex) {
        int byteLen = hex.length() / 2;
        byte[] ret = new byte[byteLen];
        for (int i = 0; i < byteLen; i++) {
            int m = (i * 2) + 1;
            ret[i] = Byte.valueOf((byte) Integer.decode("0x" + hex.substring(i * 2, m) + hex.substring(m, m + 1)).intValue()).byteValue();
        }
        return ret;
    }

    public static Object concatArrays(Object array1, int array1_Start, int array1_End, Object array2, int array2_Start, int array2_End) {
        int array2len;
        if (!array1.getClass().isArray() || !array2.getClass().isArray() || array1_Start < 0 || array1_Start < 0 || array2_End < 0 || array2_Start < 0) {
            return null;
        }
        int array1len = array1_Start != array1_End ? (array1_End - array1_Start) + 1 : 0;
        if (array2_Start != array2_End) {
            array2len = (array2_End - array2_Start) + 1;
        } else {
            array2len = 0;
        }
        byte[] data = new byte[(array1len + array2len)];
        System.arraycopy(array1, array1_Start, data, 0, array1len);
        System.arraycopy(array2, array2_Start, data, array1len, array2len);
        return data;
    }

    public static void addShutdownHook(final Class<?> cls, final Object object) {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
             

            public void run() {
                try {
                    cls.getMethod("Tclose", null).invoke(object, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }));
    }

    public static Method getMethodByClass(Class cs, String methodName, Class... parameters) {
        Method method = null;
        while (cs != null) {
            try {
                method = cs.getDeclaredMethod(methodName, parameters);
                method.setAccessible(true);
                cs = null;
            } catch (Exception e) {
                cs = cs.getSuperclass();
            }
        }
        return method;
    }

    public static Object getFieldValue(Object obj, String fieldName) throws Exception {
        Field f = null;
        if (obj instanceof Field) {
            f = (Field) obj;
        } else {
            Class cs = obj.getClass();
            while (cs != null) {
                try {
                    f = cs.getDeclaredField(fieldName);
                    cs = null;
                } catch (Exception e) {
                    cs = cs.getSuperclass();
                }
            }
        }
        f.setAccessible(true);
        return f.get(obj);
    }

    public static Object invoke(Object obj, String methodName, Object... parameters) {
        try {
            ArrayList classes = new ArrayList();
            if (parameters != null) {
                for (Object o1 : parameters) {
                    if (o1 != null) {
                        classes.add(o1.getClass());
                    } else {
                        classes.add(null);
                    }
                }
            }
            return getMethodByClass(obj.getClass(), methodName, (Class[]) classes.toArray(new Class[0])).invoke(obj, parameters);
        } catch (Exception e) {
            return null;
        }
    }

    public static short bytesToShort(byte[] bytes) {
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getShort();
    }

    public static int stringToint(String intString) {
        try {
            return Integer.parseInt(intString);
        } catch (Exception e) {
            return 0;
        }
    }

    public static byte[] aes(int opmode, byte[] key, byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(opmode, new SecretKeySpec(key, "AES"), new IvParameterSpec(key));
            return cipher.doFinal(data);
        } catch (Exception e) {
            Log.error(e);
            return null;
        }
    }

    public static void openBrowseUrl(String url) {
        if (Desktop.isDesktopSupported()) {
            try {
                URI uri = URI.create(url);
                Desktop dp = Desktop.getDesktop();
                if (dp.isSupported(Desktop.Action.BROWSE)) {
                    dp.browse(uri);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] temp = new byte[5120];
        int readOneNum = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((readOneNum = inputStream.read(temp)) != -1){
            bos.write(temp, 0, readOneNum);
        }

        return bos.toByteArray();
    }

    public static byte[] readInputStreamAutoClose(InputStream inputStream) {
        byte[] bArr = new byte[0];
        try {
            byte[] ret = readInputStream(inputStream);
            inputStream.close();
            return ret;
        } catch (IOException e) {
            Log.error(e);
            throw new RuntimeException(e);
        }
    }

    public static HashMap<String, String> matcherTwoChild(String data, String regex) {
        Matcher m = Pattern.compile(regex).matcher(data);
        HashMap<String, String> hashMap = new HashMap<>();
        while (m.find()) {
            try {
                hashMap.put(m.group(1), m.group(2));
            } catch (Exception e) {
                Log.error(e);
            }
        }
        return hashMap;
    }

    public static String getJarFileByClass(Class cs) {
        if (cs == null) {
            return null;
        }
        String tmpString = cs.getProtectionDomain().getCodeSource().getLocation().getFile();
        if (!tmpString.endsWith(".jar")) {
            return null;
        }
        try {
            return URLDecoder.decode(tmpString, "utf-8");
        } catch (UnsupportedEncodingException e) {
            Log.error(e);
            return URLDecoder.decode(tmpString);
        }
    }

    public static short[] toShortArray(byte[] src) {
        int count = src.length >> 1;
        short[] dest = new short[count];
        for (int i = 0; i < count; i++) {
            dest[i] = (short) ((src[i * 2] << 8) | (src[(i * 2) + 1] & 255));
        }
        return dest;
    }

    public static byte[] stringToByteArray(String data, String encodng) {
        try {
            return data.getBytes(encodng);
        } catch (Exception e) {
            return data.getBytes();
        }
    }

    public static byte[] httpReqest(String urlString, String method, HashMap<String, String> headers, byte[] data) {
        boolean z = true;
        try {
            HttpURLConnection httpConn = (HttpURLConnection) new URL(urlString).openConnection();
            httpConn.setDoInput(true);
            if ("GET".equals(method.toUpperCase())) {
                z = false;
            }
            httpConn.setDoOutput(z);
            httpConn.setConnectTimeout(3000);
            httpConn.setReadTimeout(3000);
            httpConn.setRequestMethod(method.toUpperCase());
            Http.addHttpHeader(httpConn, headers);
            if (httpConn.getDoOutput() && data != null) {
                httpConn.getOutputStream().write(data);
            }
            return readInputStream(httpConn.getInputStream());
        } catch (Exception e) {
            Log.error(e);
            return null;
        }
    }

    public static String formatDir(String dirString) {
        if (dirString == null || dirString.length() <= 0) {
            return "";
        }
        String dirString2 = dirString.trim().replaceAll("\\\\+", "/").replaceAll("/+", "/").trim();
        if (!dirString2.substring(dirString2.length() - 1, dirString2.length()).equals("/")) {
            return dirString2 + "/";
        }
        return dirString2;
    }

    public static int randomInt(int max, int min) {
        return ((int) (Math.random() * ((double) ((max - min) + 1)))) + min;
    }

    public static boolean filePutContent(String file, byte[] data) {
        return filePutContent(new File(file), data);
    }

    public static boolean filePutContent(File file, byte[] data) {
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(data);
            outputStream.close();
            return true;
        } catch (Exception e) {
            Log.error(e);
            return false;
        }
    }

    public static String concatCookie(String oldCookie, String newCookie) {
        String[] tmpA;
        String[] tmpA2;
        String newCookie2 = newCookie + ";";
        StringBuffer cookieBuffer = new StringBuffer();
        Map<String, String> cookieMap = new HashMap<>();
        for (String str : (oldCookie + ";").split(";")) {
            String[] temB = str.split("=");
            cookieMap.put(temB[0], temB[1]);
        }
        for (String str2 : newCookie2.split(";")) {
            String[] temB2 = str2.split("=");
            cookieMap.put(temB2[0], temB2[1]);
        }
        for (String keyString : cookieMap.keySet()) {
            cookieBuffer.append(keyString);
            cookieBuffer.append("=");
            cookieBuffer.append(cookieMap.get(keyString));
            cookieBuffer.append(";");
        }
        return cookieBuffer.toString();
    }

    public static String md5(String s) {
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(s.getBytes(), 0, s.length());
            return byteArrayToHex(m.digest());
        } catch (NoSuchAlgorithmException e) {
            Log.error(e);
            return null;
        }
    }

    public static String base64Encode(byte[] src) {
        int dp;
        int sp;
        int end = src.length;
        byte[] dst = new byte[(((src.length + 2) / 3) * 4)];
        char[] base64 = toBase64;
        int sp2 = 0;
        int slen = ((end - 0) / 3) * 3;
        int sl = 0 + slen;
        if (-1 > 0 && slen > 0) {
            slen = 0 * 3;
        }
        int dp2 = 0;
        while (true) {
            dp = dp2;
            sp = sp2;
            if (sp >= sl) {
                break;
            }
            int sl0 = Math.min(sp + slen, sl);
            int sp0 = sp;
            int dp0 = dp;
            while (sp0 < sl0) {
                int sp02 = sp0 + 1;
                int sp03 = sp02 + 1;
                sp0 = sp03 + 1;
                int bits = ((src[sp0] & 255) << 16) | ((src[sp02] & 255) << 8) | (src[sp03] & 255);
                int dp02 = dp0 + 1;
                dst[dp0] = (byte) base64[(bits >>> 18) & 63];
                int dp03 = dp02 + 1;
                dst[dp02] = (byte) base64[(bits >>> 12) & 63];
                int dp04 = dp03 + 1;
                dst[dp03] = (byte) base64[(bits >>> 6) & 63];
                dp0 = dp04 + 1;
                dst[dp04] = (byte) base64[bits & 63];
            }
            dp2 = dp + (((sl0 - sp) / 3) * 4);
            sp2 = sl0;
        }
        if (sp < end) {
            int sp3 = sp + 1;
            int b0 = src[sp] & 255;
            int dp3 = dp + 1;
            dst[dp] = (byte) base64[b0 >> 2];
            if (sp3 == end) {
                int dp4 = dp3 + 1;
                dst[dp3] = (byte) base64[(b0 << 4) & 63];
                if (1 != 0) {
                    int dp5 = dp4 + 1;
                    dst[dp4] = 61;
                    int i = dp5 + 1;
                    dst[dp5] = 61;
                }
            } else {
                sp = sp3 + 1;
                int b1 = src[sp3] & 255;
                int dp6 = dp3 + 1;
                dst[dp3] = (byte) base64[((b0 << 4) & 63) | (b1 >> 4)];
                int dp7 = dp6 + 1;
                dst[dp6] = (byte) base64[(b1 << 2) & 63];
                if (1 != 0) {
                    dp = dp7 + 1;
                    dst[dp7] = 61;
                }
            }
            return new String(dst).replace("\r", "").replace("\n", "");
        }
        return new String(dst).replace("\r", "").replace("\n", "");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:33:0x00da, code lost:
        if (r13 == 18) goto L_0x00dc;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static byte[] base64Decode(java.lang.String r21) {
        /*
        // Method dump skipped, instructions count: 375
        */
        throw new UnsupportedOperationException("Method not decompiled: util.functions.base64Decode(java.lang.String):byte[]");
    }

    public static String subMiddleStr(String data, String leftStr, String rightStr) {
        int leftIndex = data.indexOf(leftStr) + leftStr.length();
        int rightIndex = data.indexOf(rightStr);
        if (leftIndex == -1 || rightIndex == -1) {
            return null;
        }
        return data.substring(leftIndex, rightIndex);
    }

    public static byte[] getResourceAsByteArray(Class cl, String name) {
        InputStream inputStream = cl.getResourceAsStream(name);
        byte[] data = null;
        try {
            data = readInputStream(inputStream);
        } catch (IOException e) {
            Log.error(e);
        }
        try {
            inputStream.close();
        } catch (Exception e2) {
            Log.error(e2);
        }
        return data;
    }

    public static byte[] getResourceAsByteArray(Object o, String name) {
        return getResourceAsByteArray((Class) o.getClass(), name);
    }

    public static boolean saveDataViewToCsv(Vector columnVector, Vector dataRows, String saveFile) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(saveFile);
            int columnNum = columnVector.size();
            int rowNum = dataRows.size();
            new StringBuilder();
            for (int i = 0; i < columnNum - 1; i++) {
                fileOutputStream.write(formatStringByCsv(columnVector.get(i).toString()).getBytes());
                fileOutputStream.write(44);
            }
            fileOutputStream.write(formatStringByCsv(columnVector.get(columnNum - 1).toString()).getBytes());
            fileOutputStream.write(10);
            for (int i2 = 0; i2 < rowNum; i2++) {
                Vector row = (Vector) dataRows.get(i2);
                for (int j = 0; j < columnNum - 1; j++) {
                    fileOutputStream.write(formatStringByCsv(String.valueOf(row.get(j))).getBytes());
                    fileOutputStream.write(44);
                }
                fileOutputStream.write(formatStringByCsv(String.valueOf(row.get(columnNum - 1))).getBytes());
                fileOutputStream.write(10);
            }
            fileOutputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String stringToUnicode(String unicode) {
        char[] chars = unicode.toCharArray();
        StringBuilder builder = new StringBuilder();
        for (char c : chars) {
            builder.append("\\u");
            String hx = Integer.toString(c, 16);
            if (hx.length() < 4) {
                builder.append("0000".substring(hx.length())).append(hx);
            } else {
                builder.append(hx);
            }
        }
        return builder.toString();
    }

    public static String unicodeToString(String s) {
        String[] split = s.split("\\\\");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < split.length; i++) {
            if (split[i].startsWith("u")) {
                builder.append((char) Integer.parseInt(split[i].substring(1, 5), 16));
                if (split[i].length() > 5) {
                    builder.append(split[i].substring(5));
                }
            } else {
                builder.append(split[i]);
            }
        }
        return builder.toString();
    }

    public static boolean sleep(int time) {
        try {
            Thread.sleep((long) time);
            return true;
        } catch (InterruptedException e) {
            Log.error(e);
            return false;
        }
    }

    public static String SHA(byte[] data, String strType) {
        if (data == null || data.length <= 0) {
            return null;
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(strType);
            messageDigest.update(data);
            byte[] byteBuffer = messageDigest.digest();
            StringBuffer strHexString = new StringBuffer();
            for (byte b : byteBuffer) {
                String hex = Integer.toHexString(b & 255);
                if (hex.length() == 1) {
                    strHexString.append('0');
                }
                strHexString.append(hex);
            }
            return strHexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static File getCurrentJarFile() {
        String jarFileString = getJarFileByClass(ApplicationContext.class);
        if (jarFileString != null) {
            return new File(jarFileString);
        }
        return null;
    }

    public static String toString(Object object) {
        return object == null ? "null" : object.toString();
    }

    public static String getLastFileName(String file) {
        String[] fs = formatDir(file).split("/");
        return fs[fs.length - 1];
    }

    private static String formatStringByCsv(String string) {
        return "\"" + string.replace("\"", "\"\"") + "\"";
    }
}