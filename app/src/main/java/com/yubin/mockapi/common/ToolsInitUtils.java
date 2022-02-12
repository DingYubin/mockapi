package com.yubin.mockapi.common;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.interfaces.BetaPatchListener;
import com.tencent.bugly.crashreport.CrashReport;
import com.yubin.mockapi.common.utils.CECAppHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;

/**
 * <pre>
 *     @author : dingyubin
 *     e-mail : dingyubin@gmail.com
 *     time    : 2022/02/11
 *     desc    :
 *     version : 1.0
 * </pre>
 */
public class ToolsInitUtils {

    public static void initBugly(Context context) {
        // 设置是否开启热更新能力，默认为true   Dalvik 容易反射失败崩溃
        Beta.enableHotfix = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
        // 设置是否自动下载补丁，默认为true
        Beta.canAutoDownloadPatch = true;
        // 设置是否自动合成补丁，默认为true
        Beta.canAutoPatch = true;
        // 设置是否提示用户重启，默认为false
        Beta.canNotifyUserRestart = false;
        // 补丁回调接口
        Beta.betaPatchListener = new BetaPatchListener() {
            @Override
            public void onPatchReceived(String patchFile) {
                //Toast.makeText(getApplication(), "补丁下载地址" + patchFile, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onDownloadReceived(long savedLength, long totalLength) {
                Toast.makeText(context,
                        String.format(Locale.getDefault(), "%s %d%%",
                                "资源正在下载中",
                                (int) (totalLength == 0 ? 0 : savedLength * 100 / totalLength)),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDownloadSuccess(String msg) {
                Toast.makeText(context, "资源下载成功，正在准备解压, msg: ", Toast.LENGTH_SHORT).show();
                Beta.applyDownloadedPatch();
            }

            @Override
            public void onDownloadFailure(String msg) {
                Toast.makeText(context, "资源下载失败，请重新启动应用尝试, msg: " + msg, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onApplySuccess(String msg) {
                Toast.makeText(context, "资源解压完成，请关闭应用，点击桌面图标启动, msg: " + msg, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onApplyFailure(String msg) {
                Toast.makeText(context, "资源解压失败，请稍后尝试", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onPatchRollback() {

            }
        };

        //是否打开tinker调试设备配置
        Bugly.setIsDevelopmentDevice(context.getApplicationContext(),  false);
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
        strategy.setAppChannel("BuildConfig.FLAVOR");
        strategy.setUploadProcess(CECAppHelper.isMainProcess(context));
        Bugly.init(context.getApplicationContext(),
                "cba1db8169", false, strategy);
        //添加Jenkins构建号到数据追踪
        CrashReport.putUserData(context, "Jenkinsbuildnumber", "local_build");
    }

    /**
     * 从asset中获取内容
     */
    public static String stringFromAssets(@NonNull Context context, String fileName) {
        BufferedReader bufReader = null;
        InputStreamReader inputReader = null;
        try {
            String line;
            inputReader = new InputStreamReader(context.getResources().getAssets().open(fileName));
            bufReader = new BufferedReader(inputReader);
            StringBuilder builder = new StringBuilder();
            while ((line = bufReader.readLine()) != null) {
                builder.append(line);
            }
            return builder.toString();
        } catch (Exception ignored) {
        } finally {
            if (inputReader != null) {
                try {
                    inputReader.close();
                } catch (IOException ignored) {
                }
            }
            if (bufReader != null) {
                try {
                    bufReader.close();
                } catch (IOException ignored) {
                }
            }
        }
        return Uri.EMPTY.toString();
    }
}
