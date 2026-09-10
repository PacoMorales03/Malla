package devall.malla.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BloqueHorarioDao {
    @Query("SELECT * FROM bloques_horario ORDER BY diaSemana ASC, horaInicioMinutos ASC")
    fun observarTodos(): Flow<List<BloqueHorario>>

    @Insert
    suspend fun insertar(bloque: BloqueHorario): Long

    @Update
    suspend fun actualizar(bloque: BloqueHorario)

    @Delete
    suspend fun eliminar(bloque: BloqueHorario)
}
