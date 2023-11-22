package com.getech.android.utils;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.os.Build;

public class YtuserAgentUtils {
    public String os;
    public String osv;
    public String pkg;
    public String appv;
    public String mf;
    public String bd;
    public String rm;
    public String rmv;
    public String hp;
    public String hv;

    public static String getYtUserAgent(Application application) {
        String appVersion = "";
        try {
            String packageName = application.getPackageName();
            PackageInfo info = application.getPackageManager().getPackageInfo(packageName, 0);
            appVersion = info.versionName;
        } catch (Exception e) {
        }
        YtuserAgentUtils userAgent = new YtuserAgentUtils();
        userAgent.os = "android";
        userAgent.osv = Build.VERSION.RELEASE;
        userAgent.pkg = application.getPackageName();
        userAgent.appv = appVersion;
        userAgent.mf = Build.MANUFACTURER;
        userAgent.bd = getModel();
        RomUtils.RomBean romBean = RomUtils.getRom();
        userAgent.rm = romBean.getRomName();
        userAgent.rmv = romBean.getRomVersion();
        userAgent.hp = "";
        userAgent.hv = "";
        return GsonUtils.toJson(userAgent);
    }

    public static String getModel() {
        String model = Build.MODEL;
        if (model != null) {
            model = model.trim().replaceAll("\\s*", "");
        } else {
            model = "";
        }
        return model;
    }


}
