package br.com.fitnesspro.model.nutrition.avaliation

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.fitnesspro.model.base.IntegratedModel
import java.time.LocalDateTime
import java.util.UUID

@Entity(tableName = "physic_evaluation")
data class PhysicEvaluation(
    @PrimaryKey
    override val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "transmission_date")
    override var transmissionDate: LocalDateTime? = null,

    var date: LocalDateTime? = LocalDateTime.now(),
    var weight: Double? = null,
    var height: Double? = null,

    @ColumnInfo(name = "measure_neck")
    var measureNeck: Double? = null,
    @ColumnInfo(name = "measure_shoulders")
    var measureShoulders: Double? = null,
    @ColumnInfo(name = "measure_chest")
    var measureChest: Double? = null,
    @ColumnInfo(name = "measure_abdomen")
    var measureAbdomen: Double? = null,
    @ColumnInfo(name = "measure_waist")
    var measureWaist: Double? = null,

    @ColumnInfo(name = "measure_arm_relaxed_right")
    var measureArmRelaxedRight: Double? = null,
    @ColumnInfo(name = "measure_arm_relaxed_left")
    var measureArmRelaxedLeft: Double? = null,
    @ColumnInfo(name = "measure_arm_contracted_right")
    var measureArmContractedRight: Double? = null,
    @ColumnInfo(name = "measure_arm_contracted_left")
    var measureArmContractedLeft: Double? = null,

    @ColumnInfo(name = "measure_forearm_right")
    var measureForearmRight: Double? = null,
    @ColumnInfo(name = "measure_forearm_left")
    var measureForearmLeft: Double? = null,

    @ColumnInfo(name = "measure_wrist_right")
    var measureWristRight: Double? = null,
    @ColumnInfo(name = "measure_wrist_left")
    var measureWristLeft: Double? = null,

    @ColumnInfo(name = "measure_hip")
    var measureHip: Double? = null,

    @ColumnInfo(name = "measure_thigh_right")
    var measureThighRight: Double? = null,
    @ColumnInfo(name = "measure_thigh_left")
    var measureThighLeft: Double? = null,

    @ColumnInfo(name = "measure_calf_right")
    var measureCalfRight: Double? = null,
    @ColumnInfo(name = "measure_calf_left")
    var measureCalfLeft: Double? = null,

    @ColumnInfo(name = "measure_femur")
    var measureFemur: Double? = null,

    @ColumnInfo(name = "skin_fold_biceps")
    var skinFoldBiceps: Double? = null,
    @ColumnInfo(name = "skin_fold_triceps")
    var skinFoldTriceps: Double? = null,
    @ColumnInfo(name = "skin_fold_middle_axillary")
    var skinFoldMiddleAxillary: Double? = null,
    @ColumnInfo(name = "skin_fold_chest")
    var skinFoldChest: Double? = null,
    @ColumnInfo(name = "skin_fold_abdomen")
    var skinFoldAbdomen: Double? = null,
    @ColumnInfo(name = "skin_fold_suprailiac")
    var skinFoldSuprailiac: Double? = null,
    @ColumnInfo(name = "skin_fold_subescapularis")
    var skinFoldSubscapularis: Double? = null,
    @ColumnInfo(name = "skin_fold_thigh")
    var skinFoldThigh: Double? = null,
    @ColumnInfo(name = "skin_fold_medial_calf")
    var skinFoldMedialCalf: Double? = null,
): IntegratedModel()