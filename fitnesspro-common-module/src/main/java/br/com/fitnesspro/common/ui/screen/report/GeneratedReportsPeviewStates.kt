package br.com.fitnesspro.common.ui.screen.report

import br.com.fitnesspro.common.ui.state.GeneratedReportsUIState
import br.com.fitnesspro.to.TOReport
import java.time.LocalDateTime

val generatedReportsEmptyState = GeneratedReportsUIState(
    title = "Relatórios Gerados",
    subtitle = "Agendamentos"
)

val generatedReportsState = GeneratedReportsUIState(
    title = "Relatórios Gerados",
    subtitle = "Agendamentos",
    reports = listOf(
        TOReport(
            name = "Relatório com um nome extremamente grande que ninguém vai colocar mas tem que testar",
            extension = "pdf",
            date = LocalDateTime.of(2025, 6, 22, 10, 40),
            filePath = "",
            kbSize = 10000
        ),
        TOReport(
            name = "Relatório 2",
            extension = "pdf",
            date = LocalDateTime.of(2025, 6, 23, 10, 0),
            filePath = "",
            kbSize = 12000
        )
    )
)