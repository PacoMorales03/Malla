package devall.malla.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "asignaturas",
    foreignKeys = [
        ForeignKey(
            entity = Curso::class,
            parentColumns = ["id"],
            childColumns = ["cursoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("cursoId")]
)
data class Asignatura(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cursoId: Long,
    val nombre: String,
    val creditos: Double,
    val estado: EstadoAsignatura = EstadoAsignatura.VACIO,
    val convocatoriasGastadas: Int = 0
)
