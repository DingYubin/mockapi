package com.yubin.mvvm.net.converter

import android.text.TextUtils
import android.util.Log
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.internal.Excluder
import com.google.gson.internal.Primitives
import com.google.gson.internal.`$Gson$Types`
import com.google.gson.reflect.TypeToken
import com.yubin.mvvm.BuildConfig
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.Serializable
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.concurrent.ConcurrentHashMap

/**
 *
 *
 * 自定义数据转化工厂类，检验数据实体是否实现Serializable
 */
class GsonConverterFactory private constructor(private val gson: Gson) : Converter.Factory() {

    private val typeTokenCache: MutableMap<String, Any> = ConcurrentHashMap()

    override fun responseBodyConverter(
        t: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *> {
        val type: TypeToken<*> = TypeToken.get(t)
        verifyInterface(type, null)
        val adapter: TypeAdapter<*> = gson.getAdapter(type)
        return ResponseBodyConverter(gson, adapter)
    }

    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<Annotation>,
        methodAnnotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<*, RequestBody> {
        val adapter = gson.getAdapter(TypeToken.get(type))
        return RequestBodyConverter(gson, adapter)
    }

    /**
     * 检查该类及其成员变量、泛型参数类型是否实现 Serializable
     *
     * @param typeToken
     * @param name
     */
    private fun verifyInterface(typeToken: TypeToken<*>?, name: String?) {
        if (!BuildConfig.DEBUG) {
            return
        }
        if (typeToken == null) {
            return
        }
        val type = typeToken.type
        val rawType = typeToken.rawType

        //处理带泛型的类
        if (type is ParameterizedType) {
            val arguments = type.actualTypeArguments
            verifyTypeArguments(arguments)
        }
        if (type == null) {
            return
        }
        if (rawType == null) {
            return
        }

        //忽略基本数据类型及其封装类、String类型
        if (isBaseType(rawType)) {
            return
        }

        //忽略java类
        if (isJavaClass(rawType)) {
            return
        }

        //跳过@Since并@Until 的类
        val skipSerialize = Excluder.DEFAULT.excludeClass(rawType, true)
        val skipDeserialize = Excluder.DEFAULT.excludeClass(rawType, false)
        if (skipSerialize || skipDeserialize) {
            Log.d(TAG, rawType.name + SKIP_NOTE)
            return
        }

        //跳过检查过的类
        val cached = typeTokenCache[rawType.name]
        if (cached != null) {
//            Log.d(TAG, typeToken.getRawType().getName() + SKIP_NOTE);
            return
        }

        //检查自己的接口实现
        verifySelfInterface(typeToken, name)

        //处理成员变量
        verifyFieldInterface(typeToken)
    }

    /**
     * 检查成员变量是否实现了 Serializable
     */
    private fun verifyFieldInterface(typeToken: TypeToken<*>) {
        var typeToken = typeToken
        var raw = typeToken.rawType
        while (raw != Any::class.java) {
            val fields = raw.declaredFields
            for (field in fields) {
                val serialize = Excluder.DEFAULT.excludeField(field, true)
                val deserialize = Excluder.DEFAULT.excludeField(field, false)
                if (serialize || deserialize) {
                    Log.d(TAG, typeToken.rawType.name + "#" + field.name + SKIP_NOTE)
                    continue
                }
                val fieldType: Type = `$Gson$Types`.resolve(typeToken.type, raw, field.genericType)
                val fileTypeToken = TypeToken.get(fieldType)
                verifyInterface(fileTypeToken, typeToken.rawType.name + "#" + field.name)
            }
            val resolve: Type = `$Gson$Types`.resolve(typeToken.type, raw, raw.genericSuperclass)
                ?: break
            typeToken = TypeToken.get(resolve)
            raw = typeToken.rawType
        }
    }

    /**
     * 检查该类是否实现了 Serializable
     *
     * @param typeToken
     */
    private fun verifySelfInterface(typeToken: TypeToken<*>, name: String?) {
        var typeToken = typeToken
        var isImplemented = false
        var rawType = typeToken.rawType
        if (rawType == Unit::class.java || rawType!!.name.startsWith("kotlin.jvm.functions.Function")) {
            //排除Unit和Function
            return
        }
        while (rawType != null && rawType != Any::class.java) {
            val interfaces = rawType.genericInterfaces
            for (impl in interfaces) {
                if (impl is Serializable) {
                    isImplemented = true
                    typeTokenCache[typeToken.rawType.name] = Any::class.java
                    Log.d(TAG, typeToken.rawType.name + " implemented " + impl.toString())
                    break
                }
            }
            if (isImplemented) {
                break
            }
            val resolve: Type =
                `$Gson$Types`.resolve(typeToken.type, rawType, rawType.genericSuperclass)
                    ?: break
            typeToken = TypeToken.get(resolve)
            rawType = typeToken.rawType
        }
        if (!isImplemented) {
            require(!TextUtils.isEmpty(name)) { typeToken.rawType.name + " must be implemented Serializable" }
            throw IllegalArgumentException("$name may be transient")
        }
    }

    /**
     * 检查泛型参数是否实现了 Serializable
     *
     * @param arguments Type
     */
    private fun verifyTypeArguments(arguments: Array<Type>) {
        if (arguments.size <= 0) {
            return
        }
        for (argument in arguments) {
            val argumentToken = TypeToken.get(argument)
            if (argument is Class<*>) {
                //忽略基本数据类型及其封装类、String类型
                verifyInterface(argumentToken, null)
            } else if (argument is ParameterizedType) {
                //检验嵌套的泛型
                val actualTypeArguments = argument.actualTypeArguments
                verifyTypeArguments(actualTypeArguments)
            }
        }
    }

    /**
     * 判断是否基本数据类型及其封装类、String类型、Object类型
     *
     * @param type Type
     * @return boolean
     */
    private fun isBaseType(type: Type): Boolean {
        return if (type !is Class<*>) {
            false
        } else type === Any::class.java || type === String::class.java || Primitives.isPrimitive(
            type
        )
                || Primitives.isWrapperType(type)
    }

    /**
     * 判断一个类是JAVA类型还是用户定义类型
     */
    private fun isJavaClass(clz: Class<*>?): Boolean {
        return clz != null && clz.getPackage() != null && clz.getPackage().name.startsWith("java")
    }

    companion object {
        private val TAG = GsonConverterFactory::class.java.simpleName
        private const val SKIP_NOTE = " skip verify Interface"
        /**
         * Create an instance using `gson` for conversion. Encoding to JSON and
         * decoding from JSON (when no charset is specified by a header) will use UTF-8.
         */
        /**
         * Create an instance using a default [Gson] instance for conversion. Encoding to JSON and
         * decoding from JSON (when no charset is specified by a header) will use UTF-8.
         */
        @JvmOverloads  // Guarding public API nullability.
        fun create(gson: Gson? = Gson()): GsonConverterFactory {
            if (gson == null) {
                throw NullPointerException("gson == null")
            }
            return GsonConverterFactory(gson)
        }
    }
}