package devall.malla.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "carrera_config")
data class CarreraConfig(
    @PrimaryKey val id: Int = 0,
    val creditosTotales: Double
)
