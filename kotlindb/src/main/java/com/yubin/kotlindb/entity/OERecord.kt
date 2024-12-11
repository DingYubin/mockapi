package com.yubin.kotlindb.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.yubin.baselibrary.bean.BaseModel
import java.io.Serializable

@Entity(
    tableName = "oe_record",
    indices = [Index("timestamp"), Index(value = arrayOf("oe"), unique = true)]
)
class OERecord : BaseModel(), Serializable {

    @PrimaryKey(autoGenerate = true) var id: Int = 0

    /**
     * oe 用于联表查询图片
     */
    @ColumnInfo(name = "oe") var oe: String? = null

    @ColumnInfo(name = "longer") var longer: String? = null

    @ColumnInfo(name = "width") var width: String? = null

    @ColumnInfo(name = "high") var high: String? = null

    @ColumnInfo(name = "weight") var weight: String? = null

    @ColumnInfo(name = "images") var images: Array<String>? = null

    @ColumnInfo(name = "timestamp") var timeStamp: Long = 0

    companion object {
        private const val serialVersionUID: Long = 1640048968462510846L
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OERecord

        if (id != other.id) return false
        if (oe != other.oe) return false
        if (longer != other.longer) return false
        if (width != other.width) return false
        if (high != other.high) return false
        if (weight != other.weight) return false
        if (images != null) {
            if (other.images == null) return false
            if (!images.contentEquals(other.images)) return false
        } else if (other.images != null) return false
        if (timeStamp != other.timeStamp) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + (oe?.hashCode() ?: 0)
        result = 31 * result + (longer?.hashCode() ?: 0)
        result = 31 * result + (width?.hashCode() ?: 0)
        result = 31 * result + (high?.hashCode() ?: 0)
        result = 31 * result + (weight?.hashCode() ?: 0)
        result = 31 * result + (images?.contentHashCode() ?: 0)
        result = 31 * result + timeStamp.hashCode()
        return result
    }

    override fun toString(): String {
        return "OERecord(id=$id, oe=$oe, longer=$longer, width=$width, high=$high, weight=$weight, images=${images?.contentToString()}, timeStamp=$timeStamp)"
    }


}