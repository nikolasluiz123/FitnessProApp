package br.com.fitnesspro.workout.injection

import br.com.fitnesspro.workout.repository.sync.exportation.WorkoutModuleExportationRepository
import br.com.fitnesspro.workout.repository.sync.importation.WorkoutModuleImportationRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface IWorkoutWorkersEntryPoint {

    fun getWorkoutModuleImportationRepository(): WorkoutModuleImportationRepository

    fun getWorkoutModuleExportationRepository(): WorkoutModuleExportationRepository

}