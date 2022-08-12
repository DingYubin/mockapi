package com.yubin.mockapi.tinker;

/**
 * <pre>
 *     @author : dingyubin
 *     e-mail : dingyubin@gmail.com
 *     time    : 2022/08/10
 *     desc    : tinker初始化类
 *     version : 1.0
 * </pre>
 */
public class TinkerBeta {

    public static boolean setPatchRestartOnScreenOff = true;

    public static void installTinker() {
        installTinker(TinkerApplicationLike.getTinkerPatchApplicationLike());

    }

    public static void installTinker(Object applicationLike) {
        TinkerManager.setPatchRestartOnScreenOff(setPatchRestartOnScreenOff);
        TinkerManager.installTinker(applicationLike);
    }
}
