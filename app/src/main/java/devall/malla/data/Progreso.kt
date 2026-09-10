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
