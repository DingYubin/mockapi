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

    class UiPage {
        companion object {
            /**
             * draw页面
             */
            const val PATH_UI_DRAW = "/ui/draw"

            /**
             * 回调事例页面
             */
            const val PATH_UI_CALLBACK = "/ui/callback"
        }
    }
}