package devall.malla.data

fun formatCreditos(valor: Double): String =
    if (valor % 1.0 == 0.0) valor.toInt().toString() else valor.toString()

data class CursoConProgreso(
    val curso: Curso,
    val creditosAprobados: Double
) {
    val porcentaje: Float
        get() = if (curso.creditosCurso <= 0.0) 0f
        else (creditosAprobados / curso.creditosCurso).toFloat().coerceIn(0f, 1f)
}

data class ProgresoTotal(
    val creditosAprobados: Double,
    val creditosTotales: Double
) {
    val porcentaje: Float
        get() = if (creditosTotales <= 0.0) 0f
        else (creditosAprobados / creditosTotales).toFloat().coerceIn(0f, 1f)
}

/**
 * Nota media del expediente según el RD 1125/2003 (el mismo que aplica la US y el resto
 * de universidades españolas): media de las notas aprobadas ponderada por sus créditos.
 * Devuelve null si todavía no hay ninguna asignatura aprobada con nota.
 */
fun notaMedia(asignaturas: List<Asignatura>): Double? {
    val aprobadasConNota = asignaturas.filter { it.estado == EstadoAsignatura.APROBADA && it.nota != null }
    val creditosTotales = aprobadasConNota.sumOf { it.creditos }
    if (creditosTotales <= 0.0) return null
    val sumaPonderada = aprobadasConNota.sumOf { it.nota!! * it.creditos }
    return sumaPonderada / creditosTotales
}

private fun prioridadEstado(estado: EstadoAsignatura): Int = when (estado) {
    EstadoAsignatura.ESTUDIANDO -> 0
    EstadoAsignatura.MATRICULADA -> 1
    EstadoAsignatura.VACIO -> 2
    EstadoAsignatura.APROBADA -> 3
}

/** Estudiando primero, luego matriculadas, luego vacías; alfabético como criterio de desempate. */
fun List<Asignatura>.ordenadasNoAprobadas(): List<Asignatura> =
    sortedWith(compareBy({ prioridadEstado(it.estado) }, { it.nombre }))

fun List<Asignatura>.ordenadasAprobadas(): List<Asignatura> =
    sortedBy { it.nombre }
