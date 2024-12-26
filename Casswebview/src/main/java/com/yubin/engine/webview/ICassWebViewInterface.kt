package com.yubin.engine.webview

interface ICassWebViewInterface {
    fun callAppFunction(methodName: String, callId: String,paramsJson: String): String?
}