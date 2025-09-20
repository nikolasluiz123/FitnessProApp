package br.com.fitnesspro.model.base

import java.time.Instant

interface IHealthDataRangeEntity : IHealthDataCollected {
    val rangeStartTime: Instant
    val rangeEndTime: Instant?
}