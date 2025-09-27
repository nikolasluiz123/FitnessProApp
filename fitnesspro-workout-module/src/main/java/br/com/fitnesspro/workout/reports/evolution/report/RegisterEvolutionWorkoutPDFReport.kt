package br.com.fitnesspro.workout.reports.evolution.report

import android.content.Context
import br.com.fitnesspro.local.data.access.dao.filters.RegisterEvolutionWorkoutReportFilter
import br.com.fitnesspro.pdf.generator.footer.DefaultReportFooter
import br.com.fitnesspro.pdf.generator.report.AbstractPDFReport
import br.com.fitnesspro.workout.injection.IWorkoutReportsEntryPoint
import br.com.fitnesspro.workout.reports.evolution.body.RegisterEvolutionWorkoutReportBody
import br.com.fitnesspro.workout.reports.evolution.header.RegisterEvolutionWorkoutReportHeader
import br.com.fitnesspro.workout.reports.evolution.sessions.RegisterEvolutionWorkoutExerciseSession
import br.com.fitnesspro.workout.reports.evolution.sessions.RegisterEvolutionWorkoutGroupSession
import br.com.fitnesspro.workout.reports.evolution.sessions.RegisterEvolutionWorkoutResumeSession
import dagger.hilt.android.EntryPointAccessors

/**
 * Este relatório fornece uma evolução detalhada de um treino específico.
 * É estruturado com sessões dinâmicas baseadas nos dados recuperados do banco de dados.
 *
 * @param context O contexto da aplicação.
 * @param filter O filtro contendo o workoutId para o qual o relatório deve ser gerado.
 * @author Seu Nome
 */
class RegisterEvolutionWorkoutPDFReport(
    private val context: Context,
    filter: RegisterEvolutionWorkoutReportFilter
) : AbstractPDFReport<RegisterEvolutionWorkoutReportFilter>(filter) {

    private val entryPoint = EntryPointAccessors.fromApplication(context, IWorkoutReportsEntryPoint::class.java)

    /**
     * Inicializa o cabeçalho, corpo e rodapé do relatório.
     * O corpo é preenchido com uma sessão de resumo e, em seguida, dinamicamente com sessões de grupo e exercício
     * com base nos dados buscados no repositório. Isso permite uma estrutura de relatório flexível
     * onde as sessões são adicionadas apenas se houver dados relevantes.
     */
    override suspend fun initialize() {
        this.header = RegisterEvolutionWorkoutReportHeader(context)
        this.body = RegisterEvolutionWorkoutReportBody()
        this.footer = DefaultReportFooter(context)

        body.addSession(RegisterEvolutionWorkoutResumeSession(context))

        val workoutGroups = entryPoint.getRegisterEvolutionWorkoutRepository().getWorkoutGroupInfosTuple(filter)

        workoutGroups.forEach { group ->
            body.addSession(RegisterEvolutionWorkoutGroupSession(context, group))

            val exercises = entryPoint.getRegisterEvolutionWorkoutRepository().getExerciseInfosTuple(group.id, filter)

            exercises.forEach { exercise ->
                body.addSession(RegisterEvolutionWorkoutExerciseSession(context, exercise))
            }
        }
    }
}