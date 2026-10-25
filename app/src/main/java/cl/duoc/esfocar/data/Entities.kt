package cl.duoc.esfocar.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "users", indices = [Index(value = ["email"], unique = true)])
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val email: String,
    val passwordHash: String,
    val salt: String
)

@Entity(tableName = "documents", indices = [Index("userId")])
data class DocumentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val title: String,
    val fileName: String,
    val path: String,
    val mimeType: String,
    val sizeBytes: Long,
    val createdAt: Long
)
