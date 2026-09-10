package devall.malla.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AsignaturaDao {
    @Query("SELECT * FROM asignaturas ORDER BY nombre ASC")
    fun observarTodas(): Flow<List<Asignatura>>

    @Query("SELECT * FROM asignaturas WHERE cursoId = :cursoId ORDER BY nombre ASC")
    fun observarPorCurso(cursoId: Long): Flow<List<Asignatura>>

    @Query("SELECT * FROM asignaturas WHERE estado = 'ESTUDIANDO' ORDER BY nombre ASC")
    fun observarEstudiando(): Flow<List<Asignatura>>

    @Insert
    suspend fun insertar(asignatura: Asignatura): Long

    @Update
    suspend fun actualizar(asignatura: Asignatura)

    @Delete
    suspend fun eliminar(asignatura: Asignatura)
}
