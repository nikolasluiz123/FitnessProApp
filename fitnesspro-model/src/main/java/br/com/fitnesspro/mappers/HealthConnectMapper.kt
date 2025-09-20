package br.com.fitnesspro.mappers

import br.com.fitnesspro.model.enums.EnumTransmissionState
import br.com.fitnesspro.model.workout.health.HealthConnectCaloriesBurned
import br.com.fitnesspro.model.workout.health.HealthConnectHeartRate
import br.com.fitnesspro.model.workout.health.HealthConnectHeartRateSamples
import br.com.fitnesspro.model.workout.health.HealthConnectMetadata
import br.com.fitnesspro.model.workout.health.HealthConnectSleepSession
import br.com.fitnesspro.model.workout.health.HealthConnectSleepStages
import br.com.fitnesspro.model.workout.health.HealthConnectSteps
import br.com.fitnesspro.model.workout.health.SleepSessionExerciseExecution
import br.com.fitnesspro.shared.communication.dtos.workout.health.HealthConnectCaloriesBurnedDTO
import br.com.fitnesspro.shared.communication.dtos.workout.health.HealthConnectHeartRateDTO
import br.com.fitnesspro.shared.communication.dtos.workout.health.HealthConnectHeartRateSamplesDTO
import br.com.fitnesspro.shared.communication.dtos.workout.health.HealthConnectMetadataDTO
import br.com.fitnesspro.shared.communication.dtos.workout.health.HealthConnectSleepSessionDTO
import br.com.fitnesspro.shared.communication.dtos.workout.health.HealthConnectSleepStagesDTO
import br.com.fitnesspro.shared.communication.dtos.workout.health.HealthConnectStepsDTO
import br.com.fitnesspro.shared.communication.dtos.workout.health.SleepSessionExerciseExecutionDTO
import br.com.fitnesspro.shared.communication.dtos.workout.interfaces.health.IHealthConnectCaloriesBurnedDTO
import br.com.fitnesspro.shared.communication.dtos.workout.interfaces.health.IHealthConnectHeartRateDTO
import br.com.fitnesspro.shared.communication.dtos.workout.interfaces.health.IHealthConnectHeartRateSamplesDTO
import br.com.fitnesspro.shared.communication.dtos.workout.interfaces.health.IHealthConnectMetadataDTO
import br.com.fitnesspro.shared.communication.dtos.workout.interfaces.health.IHealthConnectSleepSessionDTO
import br.com.fitnesspro.shared.communication.dtos.workout.interfaces.health.IHealthConnectSleepStagesDTO
import br.com.fitnesspro.shared.communication.dtos.workout.interfaces.health.IHealthConnectStepsDTO
import br.com.fitnesspro.shared.communication.dtos.workout.interfaces.health.ISleepSessionExerciseExecutionDTO
import br.com.fitnesspro.shared.communication.enums.health.EnumRecordingMethod
import br.com.fitnesspro.shared.communication.enums.health.EnumSleepStage
import br.com.fitnesspro.model.enums.health.EnumRecordingMethod as EnumRecordingMethodModel
import br.com.fitnesspro.model.enums.health.EnumSleepStage as EnumSleepStageModel

fun IHealthConnectMetadataDTO.getHealthConnectMetadata(): HealthConnectMetadata {
    return HealthConnectMetadata(
        id = this.id!!,
        transmissionState = EnumTransmissionState.TRANSMITTED,
        dataOriginPackage = this.dataOriginPackage,
        lastModifiedTime = this.lastModifiedTime,
        clientRecordId = this.clientRecordId,
        deviceManufacturer = this.deviceManufacturer,
        deviceModel = this.deviceModel,
        recordingMethod = this.recordingMethod?.let { getRecordingMethodModel(it) }
    )
}

fun HealthConnectMetadata.getHealthConnectMetadataDTO(): HealthConnectMetadataDTO {
    return HealthConnectMetadataDTO(
        id = this.id,
        active = true,
        dataOriginPackage = this.dataOriginPackage,
        lastModifiedTime = this.lastModifiedTime,
        clientRecordId = this.clientRecordId,
        deviceManufacturer = this.deviceManufacturer,
        deviceModel = this.deviceModel,
        recordingMethod = this.recordingMethod?.let { getRecordingMethodService(it) }
    )
}

fun IHealthConnectStepsDTO.getHealthConnectSteps(): HealthConnectSteps {
    return HealthConnectSteps(
        id = this.id!!,
        transmissionState = EnumTransmissionState.TRANSMITTED,
        healthConnectMetadataId = this.healthConnectMetadataId,
        exerciseExecutionId = this.exerciseExecutionId,
        count = this.count,
        startTime = this.startTime,
        endTime = this.endTime,
        startZoneOffset = this.startZoneOffset,
        endZoneOffset = this.endZoneOffset
    )
}

fun HealthConnectSteps.getHealthConnectStepsDTO(): HealthConnectStepsDTO {
    return HealthConnectStepsDTO(
        id = this.id,
        active = true,
        healthConnectMetadataId = this.healthConnectMetadataId,
        exerciseExecutionId = this.exerciseExecutionId,
        count = this.count,
        startTime = this.startTime,
        endTime = this.endTime,
        startZoneOffset = this.startZoneOffset,
        endZoneOffset = this.endZoneOffset
    )
}

fun IHealthConnectCaloriesBurnedDTO.getHealthConnectCaloriesBurned(): HealthConnectCaloriesBurned {
    return HealthConnectCaloriesBurned(
        id = this.id!!,
        transmissionState = EnumTransmissionState.TRANSMITTED,
        healthConnectMetadataId = this.healthConnectMetadataId,
        exerciseExecutionId = this.exerciseExecutionId,
        caloriesInKcal = this.caloriesInKcal,
        startTime = this.startTime,
        endTime = this.endTime,
        startZoneOffset = this.startZoneOffset,
        endZoneOffset = this.endZoneOffset
    )
}

fun HealthConnectCaloriesBurned.getHealthConnectCaloriesBurnedDTO(): HealthConnectCaloriesBurnedDTO {
    return HealthConnectCaloriesBurnedDTO(
        id = this.id,
        active = true,
        healthConnectMetadataId = this.healthConnectMetadataId,
        exerciseExecutionId = this.exerciseExecutionId,
        caloriesInKcal = this.caloriesInKcal,
        startTime = this.startTime,
        endTime = this.endTime,
        startZoneOffset = this.startZoneOffset,
        endZoneOffset = this.endZoneOffset
    )
}

fun IHealthConnectHeartRateDTO.getHealthConnectHeartRate(): HealthConnectHeartRate {
    return HealthConnectHeartRate(
        id = this.id!!,
        transmissionState = EnumTransmissionState.TRANSMITTED,
        healthConnectMetadataId = this.healthConnectMetadataId,
        exerciseExecutionId = this.exerciseExecutionId,
        startTime = this.startTime,
        endTime = this.endTime,
        startZoneOffset = this.startZoneOffset,
        endZoneOffset = this.endZoneOffset
    )
}

fun HealthConnectHeartRate.getHealthConnectHeartRateDTO(): HealthConnectHeartRateDTO {
    return HealthConnectHeartRateDTO(
        id = this.id,
        active = true,
        healthConnectMetadataId = this.healthConnectMetadataId,
        exerciseExecutionId = this.exerciseExecutionId,
        startTime = this.startTime,
        endTime = this.endTime,
        startZoneOffset = this.startZoneOffset,
        endZoneOffset = this.endZoneOffset
    )
}

fun IHealthConnectHeartRateSamplesDTO.getHealthConnectHeartRateSamples(): HealthConnectHeartRateSamples {
    return HealthConnectHeartRateSamples(
        id = this.id!!,
        transmissionState = EnumTransmissionState.TRANSMITTED,
        healthConnectHeartRateId = this.healthConnectHeartRateId,
        sampleTime = this.sampleTime,
        bpm = this.bpm
    )
}

fun HealthConnectHeartRateSamples.getHealthConnectHeartRateSamplesDTO(): HealthConnectHeartRateSamplesDTO {
    return HealthConnectHeartRateSamplesDTO(
        id = this.id,
        active = true,
        healthConnectHeartRateId = this.healthConnectHeartRateId,
        sampleTime = this.sampleTime,
        bpm = this.bpm
    )
}

fun IHealthConnectSleepSessionDTO.getHealthConnectSleepSession(): HealthConnectSleepSession {
    return HealthConnectSleepSession(
        id = this.id!!,
        transmissionState = EnumTransmissionState.TRANSMITTED,
        healthConnectMetadataId = this.healthConnectMetadataId,
        startTime = this.startTime,
        endTime = this.endTime,
        title = this.title,
        notes = this.notes
    )
}

fun HealthConnectSleepSession.getHealthConnectSleepSessionDTO(): HealthConnectSleepSessionDTO {
    return HealthConnectSleepSessionDTO(
        id = this.id,
        active = true,
        healthConnectMetadataId = this.healthConnectMetadataId,
        startTime = this.startTime,
        endTime = this.endTime,
        title = this.title,
        notes = this.notes
    )
}

fun IHealthConnectSleepStagesDTO.getHealthConnectSleepStages(): HealthConnectSleepStages {
    return HealthConnectSleepStages(
        id = this.id!!,
        transmissionState = EnumTransmissionState.TRANSMITTED,
        healthConnectSleepSessionId = this.healthConnectSleepSessionId,
        startTime = this.startTime,
        endTime = this.endTime,
        stage = this.stage?.let { getSleepStageModel(it) }
    )
}

fun HealthConnectSleepStages.getHealthConnectSleepStagesDTO(): HealthConnectSleepStagesDTO {
    return HealthConnectSleepStagesDTO(
        id = this.id,
        active = true,
        healthConnectSleepSessionId = this.healthConnectSleepSessionId,
        startTime = this.startTime,
        endTime = this.endTime,
        stage = this.stage?.let { getSleepStageService(it) }
    )
}

fun ISleepSessionExerciseExecutionDTO.getSleepSessionExerciseExecution(): SleepSessionExerciseExecution {
    return SleepSessionExerciseExecution(
        id = this.id!!,
        transmissionState = EnumTransmissionState.TRANSMITTED,
        healthConnectSleepSessionId = this.healthConnectSleepSessionId,
        exerciseExecutionId = this.exerciseExecutionId
    )
}

fun SleepSessionExerciseExecution.getSleepSessionExerciseExecutionDTO(): SleepSessionExerciseExecutionDTO {
    return SleepSessionExerciseExecutionDTO(
        id = this.id,
        active = true,
        healthConnectSleepSessionId = this.healthConnectSleepSessionId,
        exerciseExecutionId = this.exerciseExecutionId
    )
}

private fun getRecordingMethodService(model: EnumRecordingMethodModel): EnumRecordingMethod {
    return when (model) {
        EnumRecordingMethodModel.RECORDING_METHOD_UNKNOWN -> EnumRecordingMethod.RECORDING_METHOD_UNKNOWN
        EnumRecordingMethodModel.RECORDING_METHOD_ACTIVELY_RECORDED -> EnumRecordingMethod.RECORDING_METHOD_ACTIVELY_RECORDED
        EnumRecordingMethodModel.RECORDING_METHOD_AUTOMATICALLY_RECORDED -> EnumRecordingMethod.RECORDING_METHOD_AUTOMATICALLY_RECORDED
        EnumRecordingMethodModel.RECORDING_METHOD_MANUAL_ENTRY -> EnumRecordingMethod.RECORDING_METHOD_MANUAL_ENTRY
    }
}

private fun getRecordingMethodModel(service: EnumRecordingMethod): EnumRecordingMethodModel {
    return when (service) {
        EnumRecordingMethod.RECORDING_METHOD_UNKNOWN -> EnumRecordingMethodModel.RECORDING_METHOD_UNKNOWN
        EnumRecordingMethod.RECORDING_METHOD_ACTIVELY_RECORDED -> EnumRecordingMethodModel.RECORDING_METHOD_ACTIVELY_RECORDED
        EnumRecordingMethod.RECORDING_METHOD_AUTOMATICALLY_RECORDED -> EnumRecordingMethodModel.RECORDING_METHOD_AUTOMATICALLY_RECORDED
        EnumRecordingMethod.RECORDING_METHOD_MANUAL_ENTRY -> EnumRecordingMethodModel.RECORDING_METHOD_MANUAL_ENTRY
    }
}

private fun getSleepStageService(model: EnumSleepStageModel): EnumSleepStage {
    return when (model) {
        EnumSleepStageModel.AWAKE -> EnumSleepStage.AWAKE
        EnumSleepStageModel.LIGHT -> EnumSleepStage.LIGHT
        EnumSleepStageModel.DEEP -> EnumSleepStage.DEEP
        EnumSleepStageModel.REM -> EnumSleepStage.REM
        EnumSleepStageModel.UNKNOWN -> EnumSleepStage.UNKNOWN
        EnumSleepStageModel.SLEEPING -> EnumSleepStage.SLEEPING
        EnumSleepStageModel.OUT_OF_BED -> EnumSleepStage.OUT_OF_BED
        EnumSleepStageModel.AWAKE_IN_BED -> EnumSleepStage.AWAKE_IN_BED
    }
}

private fun getSleepStageModel(service: EnumSleepStage): EnumSleepStageModel {
    return when (service) {
        EnumSleepStage.AWAKE -> EnumSleepStageModel.AWAKE
        EnumSleepStage.LIGHT -> EnumSleepStageModel.LIGHT
        EnumSleepStage.DEEP -> EnumSleepStageModel.DEEP
        EnumSleepStage.REM -> EnumSleepStageModel.REM
        EnumSleepStage.UNKNOWN -> EnumSleepStageModel.UNKNOWN
        EnumSleepStage.SLEEPING -> EnumSleepStageModel.SLEEPING
        EnumSleepStage.OUT_OF_BED -> EnumSleepStageModel.OUT_OF_BED
        EnumSleepStage.AWAKE_IN_BED -> EnumSleepStageModel.AWAKE_IN_BED
    }
}