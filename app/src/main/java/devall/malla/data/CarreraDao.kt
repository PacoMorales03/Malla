package devall.malla.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface CarreraDao {
    @Query("SELECT * FROM carrera_config WHERE id = 0")
    fun observarConfig(): Flow<CarreraConfig?>

    @Upsert
    suspend fun guardar(config: CarreraConfig)
}
