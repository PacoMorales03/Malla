package devall.malla.ui.cursos

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import devall.malla.MallaApplication
import devall.malla.data.Asignatura
import devall.malla.data.Curso
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CursoDetalleViewModel(
    application: Application,
    private val cursoId: Long
) : AndroidViewModel(application) {
    private val repository = (application as MallaApplication).repository

    val curso: StateFlow<Curso?> = repository.curso(cursoId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val asignaturas: StateFlow<List<Asignatura>> = repository.asignaturasDeCurso(cursoId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun crearAsignatura(nombre: String, creditos: Double) = viewModelScope.launch {
        repository.crearAsignatura(cursoId, nombre, creditos)
    }

    fun actualizarAsignatura(asignatura: Asignatura) = viewModelScope.launch {
        repository.actualizarAsignatura(asignatura)
    }

    fun eliminarAsignatura(asignatura: Asignatura) = viewModelScope.launch {
        repository.eliminarAsignatura(asignatura)
    }

    fun actualizarCurso(curso: Curso) = viewModelScope.launch {
        repository.actualizarCurso(curso)
    }

    fun eliminarCurso(curso: Curso, alTerminar: () -> Unit) = viewModelScope.launch {
        repository.eliminarCurso(curso)
        alTerminar()
    }

    class Factory(
        private val application: Application,
        private val cursoId: Long
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            CursoDetalleViewModel(application, cursoId) as T
    }
}
