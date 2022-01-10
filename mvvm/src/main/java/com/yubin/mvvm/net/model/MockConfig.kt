package com.yubin.mvvm.net.model

import java.io.Serializable

data class MockConfig (val mocks : List<Mocks>) : Serializable

data class Mocks(val name: String, val mock : List<Mock>) : Serializable

data class Mock(val api: String, val method: String, val mockFile: String)  : Serializable