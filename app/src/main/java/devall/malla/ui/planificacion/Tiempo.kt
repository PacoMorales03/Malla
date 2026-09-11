package devall.malla.ui.planificacion

import devall.malla.data.TipoBloque
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

const val HORA_INICIO_AGENDA = 0
const val HORA_FIN_AGENDA = 24 * 60

val diasSemana = listOf(
    1 to "Lunes", 2 to "Martes", 3 to "Miércoles", 4 to "Jueves",
    5 to "Viernes", 6 to "Sábado", 7 to "Domingo"
)

val diasSemanaCortos = listOf(
    1 to "L", 2 to "M", 3 to "X", 4 to "J", 5 to "V", 6 to "S", 7 to "D"
)

fun nombreDia(dia: Int): String = diasSemana.first { it.first == dia }.second

fun opcionesHora(desde: Int = HORA_INICIO_AGENDA, hasta: Int = HORA_FIN_AGENDA): List<Int> =
    (desde..hasta step 30).toList()

fun minutoActualRedondeado(): Int {
    val ahora = LocalTime.now()
    val minutos = (ahora.hour * 60 + ahora.minute)
    return ((minutos / 30) * 30).coerceIn(HORA_INICIO_AGENDA, HORA_FIN_AGENDA - 30)
}

fun formatoHora(minutos: Int): String {
    val h = minutos / 60
    val m = minutos % 60
    return "%02d:%02d".format(h, m)
}

fun etiquetaTipoBloque(tipo: TipoBloque): String = when (tipo) {
    TipoBloque.ESTUDIO_PLANIFICADO -> "Estudio planificado"
    TipoBloque.ESTUDIO_REAL -> "Estudio real"
    TipoBloque.TRABAJO -> "Trabajo"
    TipoBloque.PERSONAL -> "Personal"
}

private val formateadorDia = DateTimeFormatter.ofPattern("d")
private val formateadorMes = DateTimeFormatter.ofPattern("MMM", Locale("es", "ES"))

fun formatoRangoSemana(lunes: LocalDate): String {
    val domingo = lunes.plusDays(6)
    val mesLunes = formateadorMes.format(lunes)
    val mesDomingo = formateadorMes.format(domingo)
    return if (mesLunes == mesDomingo) {
        "${formateadorDia.format(lunes)} - ${formateadorDia.format(domingo)} $mesLunes"
    } else {
        "${formateadorDia.format(lunes)} $mesLunes - ${formateadorDia.format(domingo)} $mesDomingo"
    }
}
