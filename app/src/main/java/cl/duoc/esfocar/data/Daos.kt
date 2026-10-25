package cl.duoc.esfocar.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: UserEntity): Long

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun findByEmail(email: String): UserEntity?
}

@Dao
interface DocumentDao {
    @Query("SELECT * FROM documents WHERE userId = :userId ORDER BY createdAt DESC")
    fun observeAll(userId: Long): Flow<List<DocumentEntity>>

    @Insert
    suspend fun insert(doc: DocumentEntity): Long

    @Delete
    suspend fun delete(doc: DocumentEntity)
}
