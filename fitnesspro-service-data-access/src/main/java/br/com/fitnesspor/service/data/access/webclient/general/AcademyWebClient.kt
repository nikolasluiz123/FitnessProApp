package br.com.fitnesspor.service.data.access.webclient.general

import android.content.Context
import br.com.fitnesspor.service.data.access.service.general.IAcademyService
import br.com.fitnesspor.service.data.access.webclient.common.FitnessProWebClient

class AcademyWebClient(
    context: Context,
    private val academyService: IAcademyService
): FitnessProWebClient(context) {

}