package com.yubin.baselibrary.router.path

/**
 * Description 路由
 */
object RouterPath {

    class MvvmPage {
        companion object {
            /**
             * 登录页面
             */
            const val PATH_MVVM_LOGIN = "/mvvm/login"
        }
    }

    class AccountPage {
        companion object {
            /**
             * 登录页面
             */
            const val PATH_MVX_LOGIN = "/mvx/login"

        }
    }

    class ImPage {
        companion object {
            /**
             * im页面
             */
            const val PATH_IM_CONVERSATION = "/im/conversation"
        }
    }

    class MediaPage {
        companion object {
            /**
             * camera页面
             */
            const val PATH_MEDIA_CAMERA = "/media/camera"
        }
    }

    class RxPage {
        companion object {
            /**
             * rxjava测试页面
             */
            const val PATH_RX_JAVA = "/rx/java"
        }
    }

    class KotlinPage {
        companion object {
            /**
             * kotlin携程测试页面
             */
            const val PATH_KOTLIN_COROUTINE = "/kotlin/coroutine"
        }
    }

    class UiPage {
        companion object {
            /**
             * draw页面
             */
            const val PATH_UI_DRAW = "/ui/draw"

            /**
             * window页面
             */
            const val PATH_UI_WINDOW = "/ui/window"

            /**
             * 回调事例页面
             */
            const val PATH_UI_CALLBACK = "/ui/callback"

            /**
             * 曝光页面
             */
            const val PATH_UI_EXPOSURE = "/ui/exposure"

        }
    }
}