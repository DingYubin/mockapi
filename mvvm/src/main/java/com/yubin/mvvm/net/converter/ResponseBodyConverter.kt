package com.yubin.mvvm.net.converter

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonIOException
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonToken
import com.yubin.mvvm.net.model.BaseResponse
import okhttp3.ResponseBody
import retrofit2.Converter
import java.io.IOException

/**
 * 响应数据转换，在这里把接口的json字符串转换成数据结构实体
 */
class ResponseBodyConverter<T> internal constructor(
    private val gson: Gson,
    private val adapter: TypeAdapter<T>
) : Converter<ResponseBody, T> {
    @Throws(IOException::class)
    override fun convert(value: ResponseBody): T {
        val jsonReader = gson.newJsonReader(value.charStream())
        return try {
            val result = adapter.read(jsonReader)
            if (jsonReader.peek() != JsonToken.END_DOCUMENT) {
                throw JsonIOException("JSON document was not fully consumed.")
            }
            Log.d("Converter", "convert() called : result = [$result]")

            if (result is BaseResponse<*> && (result.errorCode == 655 || result.errorCode == 652)) {
                //当error为655或652的时候，不提示toast，故不设置message为空
                result.aliasMessage = result.message
                result.message = ""
            }
            result
        } finally {
            value.close()
        }
    }
}