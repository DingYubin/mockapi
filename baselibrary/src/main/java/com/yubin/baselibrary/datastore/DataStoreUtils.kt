package com.yubin.baselibrary.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.createDataStore
import com.yubin.baselibrary.provider.BasicsContentProvider.Companion.basicsContext

import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

object DataStoreUtils {
    private val dataStoreMap = hashMapOf<String, DataStore<Preferences>>()

    private const val defaultName = "yubin"

    suspend fun <T> writeDataToDataStore(name: String, key: Preferences.Key<T>, msg: T) {
        var dataStore: DataStore<Preferences>?
        if (null == dataStoreMap[name]) {
            dataStore = basicsContext.createDataStore(name = name)
            dataStoreMap[name] = dataStore
        }
        dataStore = dataStoreMap[name]
        dataStore?.edit { user ->
            user[key] = msg
        }
    }

    suspend fun <T> readDataFromDataStore(
        name: String,
        key: Preferences.Key<T>,
        defaultValue: T
    ): T {
        var dataStore: DataStore<Preferences>? = dataStoreMap[name]
        if (null == dataStore) {
            dataStore = basicsContext.createDataStore(name = name)
            dataStoreMap[name] = dataStore
        }
        return dataStore.data.catch {
            // 当读取数据遇到错误时，如果是 `IOException` 异常，发送一个 emptyPreferences 来重新使用
            // 但是如果是其他的异常，最好将它抛出去，不要隐藏问题
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }.map { preferences -> preferences[key] ?: defaultValue }.first()
            ?: defaultValue
    }

    suspend fun <T> writeDataToDataStore(key: Preferences.Key<T>, msg: T) {
        writeDataToDataStore(defaultName, key, msg)
    }

    suspend fun <T> readDataFromDataStore(key: Preferences.Key<T>, defaultValue: T): T {
        return readDataFromDataStore(defaultName, key, defaultValue)
    }
}