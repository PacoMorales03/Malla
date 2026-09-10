package devall.malla.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import devall.malla.data.EstadoAsignatura

data class EstadoColores(
    val vacio: Color,
    val matriculada: Color,
    val estudiando: Color,
    val aprobada: Color
) {
    fun de(estado: EstadoAsignatura): Color = when (estado) {
        EstadoAsignatura.VACIO -> vacio
        EstadoAsignatura.MATRICULADA -> matriculada
        EstadoAsignatura.ESTUDIANDO -> estudiando
        EstadoAsignatura.APROBADA -> aprobada
    }
}

val EstadoColoresClaro = EstadoColores(
    vacio = EstadoVacio,
    matriculada = EstadoMatriculada,
    estudiando = EstadoEstudiando,
    aprobada = EstadoAprobada
)

val EstadoColoresOscuro = EstadoColores(
    vacio = EstadoVacioDark,
    matriculada = EstadoMatriculadaDark,
    estudiando = EstadoEstudiandoDark,
    aprobada = EstadoAprobadaDark
)

val LocalEstadoColores = staticCompositionLocalOf { EstadoColoresClaro }
