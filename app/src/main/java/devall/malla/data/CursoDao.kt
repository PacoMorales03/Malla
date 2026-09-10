package devall.malla.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CursoDao {
    @Query("SELECT * FROM cursos ORDER BY orden ASC")
    fun observarTodos(): Flow<List<Curso>>

    @Query("SELECT * FROM cursos WHERE id = :cursoId")
    fun observarPorId(cursoId: Long): Flow<Curso?>

    @Query("SELECT COALESCE(MAX(orden), 0) FROM cursos")
    suspend fun ordenMaximo(): Int

    @Insert
    suspend fun insertar(curso: Curso): Long

    @Update
    suspend fun actualizar(curso: Curso)

    @Delete
    suspend fun eliminar(curso: Curso)
}
