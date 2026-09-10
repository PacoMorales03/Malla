package devall.malla.ui.cursos

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import devall.malla.MallaApplication
import devall.malla.data.Curso
import devall.malla.data.CursoConProgreso
import devall.malla.data.ProgresoTotal
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CursosViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = (application as MallaApplication).repository

    val cursos: StateFlow<List<CursoConProgreso>> = repository.cursosConProgreso
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val progresoTotal: StateFlow<ProgresoTotal> = repository.progresoTotal
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProgresoTotal(0.0, 0.0))

    /** null hasta que Room entrega el primer valor real (carrera aún sin configurar). */
    val creditosTotalesConfigurados: StateFlow<Double?> = repository.configCarrera
        .map { it?.creditosTotales }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun crearCurso(nombre: String, creditos: Double) = viewModelScope.launch {
        repository.crearCurso(nombre, creditos)
    }

    fun actualizarCurso(curso: Curso) = viewModelScope.launch {
        repository.actualizarCurso(curso)
    }

    fun eliminarCurso(curso: Curso) = viewModelScope.launch {
        repository.eliminarCurso(curso)
    }

    fun guardarCreditosTotales(creditos: Double) = viewModelScope.launch {
        repository.guardarCreditosTotales(creditos)
    }
}
