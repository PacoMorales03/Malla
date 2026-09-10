package devall.malla.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "bloques_horario",
    foreignKeys = [
        ForeignKey(
            entity = Asignatura::class,
            parentColumns = ["id"],
            childColumns = ["asignaturaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("asignaturaId")]
)
data class BloqueHorario(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tipo: TipoBloque,
    val asignaturaId: Long? = null,
    /** Solo se usa para bloques PERSONAL; TRABAJO se etiqueta siempre igual y los de estudio muestran la asignatura. */
    val nombre: String? = null,
    /** 1 = lunes ... 7 = domingo. Se deriva de fechaInicio al crear el bloque. */
    val diaSemana: Int,
    val horaInicioMinutos: Int,
    val horaFinMinutos: Int,
    /** Fecha de la primera ocurrencia. */
    val fechaInicio: LocalDate,
    /** Última fecha (incluida) en la que se repite. Igual a fechaInicio si no se repite. */
    val fechaFin: LocalDate
)
