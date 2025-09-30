package br.com.fitnesspro.core.notification

import android.content.Context
import br.com.core.android.utils.notification.AbstractAndroidNotification
import br.com.fitnesspro.core.R

abstract class FitnessProNotification(context: Context): AbstractAndroidNotification(context) {

    override fun getSmallIcon(): Int = R.drawable.ic_notification
}