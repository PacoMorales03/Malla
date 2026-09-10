package devall.malla.data

import androidx.room.TypeConverter
import java.time.LocalDate

class Converters {
    @TypeConverter
    fun fromEstado(estado: EstadoAsignatura): String = estado.name

    @TypeConverter
    fun toEstado(valor: String): EstadoAsignatura = EstadoAsignatura.valueOf(valor)

    @TypeConverter
    fun fromTipoBloque(tipo: TipoBloque): String = tipo.name

    @TypeConverter
    fun toTipoBloque(valor: String): TipoBloque = TipoBloque.valueOf(valor)

    @TypeConverter
    fun fromLocalDate(fecha: LocalDate): Long = fecha.toEpochDay()

    @TypeConverter
    fun toLocalDate(valor: Long): LocalDate = LocalDate.ofEpochDay(valor)
}
