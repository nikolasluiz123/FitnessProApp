package br.com.fitnesspro.firebase.api.analytics

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent

fun FirebaseAnalytics.logButtonClick(enum: Enum<*>) {
    logEvent(EnumAnalyticsEvents.BUTTON_CLICK.name) {
        param(FirebaseAnalytics.Param.ITEM_ID, enum.name)
    }
}

fun FirebaseAnalytics.logBottomSheetItemClick(enum: Enum<*>) {
    logEvent(EnumAnalyticsEvents.BOTTOM_SHEET_ITEM_CLICK.name) {
        param(FirebaseAnalytics.Param.ITEM_ID, enum.name)
    }
}

fun FirebaseAnalytics.logTabClick(enum: Enum<*>) {
    logEvent(EnumAnalyticsEvents.TAB_CLICK.name) {
        param(FirebaseAnalytics.Param.ITEM_ID, enum.name)
    }
}

fun FirebaseAnalytics.logTabScroll(enum: Enum<*>) {
    logEvent(EnumAnalyticsEvents.TAB_SCROLL.name) {
        param(FirebaseAnalytics.Param.ITEM_ID, enum.name)
    }
}

fun FirebaseAnalytics.logListItemClick(enum: Enum<*>) {
    logEvent(EnumAnalyticsEvents.LIST_ITEM_CLICK.name) {
        param(FirebaseAnalytics.Param.ITEM_ID, enum.name)
    }
}