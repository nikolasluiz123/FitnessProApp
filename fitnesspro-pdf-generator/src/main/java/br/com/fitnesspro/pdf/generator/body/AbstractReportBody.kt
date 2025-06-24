package br.com.fitnesspro.pdf.generator.body

import br.com.fitnesspro.pdf.generator.common.IPageManager
import br.com.fitnesspro.pdf.generator.session.IReportSession

abstract class AbstractReportBody<FILTER: Any>: IReportBody<FILTER> {

    override val sessions = mutableListOf<IReportSession<FILTER>>()
    final override var filter: FILTER? = null
        private set

    override fun addSession(session: IReportSession<FILTER>) {
        this.sessions.add(session)
    }

    override suspend fun prepare(filter: FILTER) {
        super.prepare(filter)

        this.filter = filter
        sessions.forEach { it.prepare(filter) }
    }

    override suspend fun measureHeight(pageManager: IPageManager): Float {
        var totalHeight = 0f

        getRenderableSessions().forEach { session ->
            totalHeight += session.measureHeight(pageManager)
        }

        return totalHeight
    }

    override suspend fun draw(pageManager: IPageManager, yStart: Float): Float {
        var currentY = yStart

        getRenderableSessions().forEach { session ->
            currentY = session.draw(pageManager, currentY)
        }

        return currentY
    }

    private fun getRenderableSessions(): List<IReportSession<FILTER>> {
        return sessions.filter { it.shouldRender(filter!!) }
    }
}