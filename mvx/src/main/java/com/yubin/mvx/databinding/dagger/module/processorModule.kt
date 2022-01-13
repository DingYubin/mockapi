package com.yubin.mvx.databinding.dagger.module

import dagger.Module

/**
 * <pre>
 * time   : 2018-1-15
 * desc   :
 * version: 1.0
</pre> *
 */
@Module
abstract class processorModule {
//    @Singleton
//    @Binds
//    abstract fun bindPushProcessor(pushProcessor: PushProcessorBase?): BasePushProcessor?
//    @Singleton
//    @Binds
//    abstract fun bindChatProcessor(chatMessageProcessor: ChatMessageProcessorBase?): ChatProcessorBase?
//
//    companion object {
//        @Singleton
//        @Provides
//        fun provideShake(
//            context: Context?,
//            chatProcessor: ChatProcessorBase?,
//            pushProcessor: BasePushProcessor?,
//            remindProcessor: RemindProcessorBase?,
//            deviceOnlineProcessor: DeviceOnlineProcessorBase?,
//            msgReadProcessor: MsgReadProcessorBase?,
//            errorStatusProcessor: ErrorStatusProcessor?,
//            sessionProcessor: SessionProcessorBase?,
//            appUpgradeProcessor: AppUpgradeProcessor?,
//            sessionTopProcessor: SessionTopProcessor?,
//            videoMeetingEventProcessor: VideoMeetingEventProcessor?,
//            unReadStatusProcessor: MessageUnReadStatusProcessor?,
//            slienceProcessor: SlienceProcessor?
//        ): ShakeUtil {
//            val isTest: Boolean = BuildConfig.IS_TEST
//            return ShakeUtil(
//                context,
//                chatProcessor,
//                pushProcessor,
//                remindProcessor,
//                deviceOnlineProcessor,
//                msgReadProcessor,
//                errorStatusProcessor,
//                sessionProcessor,
//                appUpgradeProcessor,
//                sessionTopProcessor,
//                videoMeetingEventProcessor,
//                unReadStatusProcessor,
//                slienceProcessor,
//                isTest
//            )
//        }
//    }
}