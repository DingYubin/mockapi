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

            /**
             * OE页面
             */
            const val PATH_MVVM_OE = "/mvvm/oe"
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

    class RoomPage {
        companion object {
            /**
             * kotlin携程测试页面
             */
            const val PATH_ROOM_DB = "/room/db"
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
             * window页面
             */
            const val PATH_UI_WEB_WINDOW = "/ui/web/window"

            /**
             * 回调事例页面
             */
            const val PATH_UI_CALLBACK = "/ui/callback"

            /**
             * 曝光页面
             */
            const val PATH_UI_EXPOSURE = "/ui/exposure"

            /**
             * tab
             */
            const val PATH_UI_TAB = "/ui/tab"

            /**
             * im
             */
            const val PATH_UI_IM = "/ui/im"
        }
    }
}