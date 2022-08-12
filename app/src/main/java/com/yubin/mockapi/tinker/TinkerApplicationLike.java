package com.yubin.mockapi.tinker;

import android.app.Application;
import android.content.Intent;

import com.tencent.tinker.entry.ApplicationLike;
import com.tencent.tinker.entry.DefaultApplicationLike;

/**
 * * 使用DefaultLifeCycle注解生成Application（这种方式是Tinker官方推荐的）
 * * <p>
 * * Application的代理类:Tinker表示，Application无法动态修复，所以需要改代理类。
 * * <p>
 * * 程序启动时会加载默认的Application类，这导致补丁包无法对它做修改。所以Tinker官方说不建议自己去实现Application，而是由Tinker自动生成。
 * * 即需要创建一个TinkerApplicationLike类继承ApplicationLike，然后将我们自己的MyApplication中所有逻辑放在TinkerApplicationLike中的
 *
 * <pre>
 *     @author : dingyubin
 *     e-mail : dingyubin@gmail.com
 *     time    : 2022/08/09
 *     desc    : tink代理类
 *     version : 1.0
 * </pre>
 */

public class TinkerApplicationLike extends DefaultApplicationLike {
    private static ApplicationLike tinkerPatchApplicationLike;

    public TinkerApplicationLike(Application application, int tinkerFlags, boolean tinkerLoadVerifyFlag, long applicationStartElapsedTime, long applicationStartMillisTime, Intent tinkerResultIntent) {
        super(application, tinkerFlags, tinkerLoadVerifyFlag, applicationStartElapsedTime, applicationStartMillisTime, tinkerResultIntent);
        setTinkerPatchApplicationLike(this);
    }

    private static void setTinkerPatchApplicationLike(ApplicationLike applicationLike) {
        tinkerPatchApplicationLike = applicationLike;
    }

    public static ApplicationLike getTinkerPatchApplicationLike() {
        return tinkerPatchApplicationLike;
    }
}
