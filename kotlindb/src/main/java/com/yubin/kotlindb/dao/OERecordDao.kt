package com.yubin.kotlindb.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.yubin.kotlindb.entity.OERecord

@Dao
interface OERecordDao {

    /**
     * 新增操作
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(oeRecord: OERecord?): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(oeRecord: List<OERecord?>?)

    /**
     * 删除操作
     */
    @Delete
    suspend fun delete(oeRecord: OERecord?)

    @Query("delete from oe_record where oe = :oe")
    suspend fun delOEByOe(oe: String?)

    @Query("delete from oe_record")
    suspend fun delAllOE()

    /**
     * 更新操作
     */
    @Query("update oe_record set timestamp = :timestamp  where oe = :oe")
    suspend fun updateRecord(oe: String, timestamp: Long)

    /**
     *  查询操作
     */
    @Query("select * from oe_record")
    suspend fun queryAllOERecord(): Array<OERecord>

    @Query("select * from oe_record where oe = :oe")
    suspend fun queryOERecordByOe(oe: String): OERecord?
}