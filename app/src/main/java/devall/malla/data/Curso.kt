package devall.malla.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cursos")
data class Curso(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val orden: Int,
    val creditosCurso: Double
)
