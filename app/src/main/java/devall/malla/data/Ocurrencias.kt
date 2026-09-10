package devall.malla.data

import java.time.LocalDate

data class OcurrenciaBloque(val bloque: BloqueHorario, val fecha: LocalDate)

/**
 * Expande las series de bloques a las ocurrencias reales que caen en la semana
 * que empieza en [lunes]. Se usa tanto en la pantalla de Planificación como en
 * el widget, para que ambos apliquen exactamente la misma regla de fechaInicio/fechaFin.
 */
fun ocurrenciasEnSemana(bloques: List<BloqueHorario>, lunes: LocalDate): List<OcurrenciaBloque> =
    bloques.mapNotNull { bloque ->
        val fecha = lunes.plusDays((bloque.diaSemana - 1).toLong())
        if (!fecha.isBefore(bloque.fechaInicio) && !fecha.isAfter(bloque.fechaFin)) {
            OcurrenciaBloque(bloque, fecha)
        } else {
            null
        }
    }

fun lunesDeSemana(fecha: LocalDate): LocalDate = fecha.minusDays((fecha.dayOfWeek.value - 1).toLong())
