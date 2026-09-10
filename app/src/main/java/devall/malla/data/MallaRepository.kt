package devall.malla.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class MallaRepository(private val db: AppDatabase) {

    val cursos: Flow<List<Curso>> = db.cursoDao().observarTodos()
    val todasAsignaturas: Flow<List<Asignatura>> = db.asignaturaDao().observarTodas()
    val asignaturasEstudiando: Flow<List<Asignatura>> = db.asignaturaDao().observarEstudiando()
    val configCarrera: Flow<CarreraConfig?> = db.carreraDao().observarConfig()
    val bloques: Flow<List<BloqueHorario>> = db.bloqueHorarioDao().observarTodos()

    val cursosConProgreso: Flow<List<CursoConProgreso>> =
        combine(cursos, todasAsignaturas) { cursos, asignaturas ->
            cursos.map { curso ->
                val aprobados = asignaturas
                    .filter { it.cursoId == curso.id && it.estado == EstadoAsignatura.APROBADA }
                    .sumOf { it.creditos }
                CursoConProgreso(curso, aprobados)
            }
        }

    val progresoTotal: Flow<ProgresoTotal> =
        combine(todasAsignaturas, configCarrera) { asignaturas, config ->
            val aprobados = asignaturas
                .filter { it.estado == EstadoAsignatura.APROBADA }
                .sumOf { it.creditos }
            ProgresoTotal(aprobados, config?.creditosTotales ?: 0.0)
        }

    fun curso(cursoId: Long): Flow<Curso?> = db.cursoDao().observarPorId(cursoId)

    fun asignaturasDeCurso(cursoId: Long): Flow<List<Asignatura>> =
        db.asignaturaDao().observarPorCurso(cursoId)

    suspend fun crearCurso(nombre: String, creditosCurso: Double) {
        val orden = db.cursoDao().ordenMaximo() + 1
        db.cursoDao().insertar(Curso(nombre = nombre, orden = orden, creditosCurso = creditosCurso))
    }

    suspend fun actualizarCurso(curso: Curso) = db.cursoDao().actualizar(curso)

    suspend fun eliminarCurso(curso: Curso) = db.cursoDao().eliminar(curso)

    suspend fun crearAsignatura(cursoId: Long, nombre: String, creditos: Double) {
        db.asignaturaDao().insertar(
            Asignatura(cursoId = cursoId, nombre = nombre, creditos = creditos)
        )
    }

    suspend fun actualizarAsignatura(asignatura: Asignatura) =
        db.asignaturaDao().actualizar(asignatura)

    suspend fun eliminarAsignatura(asignatura: Asignatura) =
        db.asignaturaDao().eliminar(asignatura)

    suspend fun guardarCreditosTotales(creditos: Double) =
        db.carreraDao().guardar(CarreraConfig(creditosTotales = creditos))

    suspend fun crearBloque(bloque: BloqueHorario) = db.bloqueHorarioDao().insertar(bloque)

    suspend fun actualizarBloque(bloque: BloqueHorario) = db.bloqueHorarioDao().actualizar(bloque)

    suspend fun eliminarBloque(bloque: BloqueHorario) = db.bloqueHorarioDao().eliminar(bloque)
}
