package br.com.fitnesspro.core.worker

object WorkerDelay {
    /**
     * Tempo padrão em minutos utilizado nos workers que fazem sincronização dos dados (importação e exportação).
     */
    const val FITNESS_PRO_DEFAULT_SYNC_WORKER_DELAY_MINS = 3L

    /**
     * Tempo padrão em horas utilizado nos workers que fazem integração com o Health Connect (importação).
     */
    const val FITNESS_PRO_DEFAULT_HC_IMPORT_WORKER_DELAY_HOURS = 8L
}