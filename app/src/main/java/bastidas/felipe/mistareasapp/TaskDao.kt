package bastidas.felipe.mistareasapp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query(
        "SELECT * FROM tasks " + "ORDER BY creado_en DESC"
    )
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Insert
    suspend fun insert(task: TaskEntity): Long

    @Update
    suspend fun update(task: TaskEntity)

    @Delete
    suspend fun delete(task: TaskEntity)

    @Query("""
        SELECT * FROM tasks
        WHERE titulo LIKE '%' || :query || '%'
        ORDER BY 
            CASE WHEN :sortBy = 'REC' THEN creado_en END DESC,
            CASE WHEN :sortBy = 'OLD' THEN creado_en END ASC,
            CASE WHEN :sortBy = 'AZ' THEN titulo END ASC,
            CASE WHEN :sortBy = 'ZA' THEN titulo END DESC
    """)
    fun searchTasks(query: String, sortBy: String): Flow<List<TaskEntity>>
}