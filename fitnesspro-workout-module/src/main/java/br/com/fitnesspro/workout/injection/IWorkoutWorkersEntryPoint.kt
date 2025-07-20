package br.com.fitnesspro.workout.injection

import br.com.fitnesspro.workout.repository.sync.exportation.ExerciseExecutionExportationRepository
import br.com.fitnesspro.workout.repository.sync.exportation.ExerciseExportationRepository
import br.com.fitnesspro.workout.repository.sync.exportation.ExercisePreDefinitionExportationRepository
import br.com.fitnesspro.workout.repository.sync.exportation.VideoExerciseExecutionExportationRepository
import br.com.fitnesspro.workout.repository.sync.exportation.VideoExerciseExportationRepository
import br.com.fitnesspro.workout.repository.sync.exportation.VideoExercisePreDefinitionExportationRepository
import br.com.fitnesspro.workout.repository.sync.exportation.VideoExportationRepository
import br.com.fitnesspro.workout.repository.sync.exportation.WorkoutExportationRepository
import br.com.fitnesspro.workout.repository.sync.exportation.WorkoutGroupExportationRepository
import br.com.fitnesspro.workout.repository.sync.exportation.WorkoutGroupPreDefinitionExportationRepository
import br.com.fitnesspro.workout.repository.sync.importation.ExerciseExecutionImportationRepository
import br.com.fitnesspro.workout.repository.sync.importation.ExerciseImportationRepository
import br.com.fitnesspro.workout.repository.sync.importation.ExercisePreDefinitionImportationRepository
import br.com.fitnesspro.workout.repository.sync.importation.VideoExerciseExecutionImportationRepository
import br.com.fitnesspro.workout.repository.sync.importation.VideoExerciseImportationRepository
import br.com.fitnesspro.workout.repository.sync.importation.VideoExercisePreDefinitionImportationRepository
import br.com.fitnesspro.workout.repository.sync.importation.VideoImportationRepository
import br.com.fitnesspro.workout.repository.sync.importation.WorkoutGroupImportationRepository
import br.com.fitnesspro.workout.repository.sync.importation.WorkoutGroupPreDefinitionImportationRepository
import br.com.fitnesspro.workout.repository.sync.importation.WorkoutImportationRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface IWorkoutWorkersEntryPoint {
    
    fun getWorkoutImportationRepository(): WorkoutImportationRepository

    fun getWorkoutGroupImportationRepository(): WorkoutGroupImportationRepository

    fun getExerciseImportationRepository(): ExerciseImportationRepository

    fun getVideoExerciseImportationRepository(): VideoExerciseImportationRepository

    fun getVideoImportationRepository(): VideoImportationRepository

    fun getWorkoutExportationRepository(): WorkoutExportationRepository

    fun getWorkoutGroupExportationRepository(): WorkoutGroupExportationRepository

    fun getExerciseExportationRepository(): ExerciseExportationRepository

    fun getVideoExerciseExportationRepository(): VideoExerciseExportationRepository

    fun getVideoExportationRepository(): VideoExportationRepository

    fun getExercisePreDefinitionImportationRepository(): ExercisePreDefinitionImportationRepository

    fun getWorkoutGroupPreDefinitionImportationRepository(): WorkoutGroupPreDefinitionImportationRepository

    fun getVideoExercisePreDefinitionImportationRepository(): VideoExercisePreDefinitionImportationRepository

    fun getExerciseExecutionImportationRepository(): ExerciseExecutionImportationRepository

    fun getVideoExerciseExecutionImportationRepository(): VideoExerciseExecutionImportationRepository

    fun getExerciseExecutionExportationRepository(): ExerciseExecutionExportationRepository

    fun getVideoExerciseExecutionExportationRepository(): VideoExerciseExecutionExportationRepository

    fun getExercisePreDefinitionExportationRepository(): ExercisePreDefinitionExportationRepository

    fun getWorkoutGroupPreDefinitionExportationRepository(): WorkoutGroupPreDefinitionExportationRepository

    fun getVideoExercisePreDefinitionExportationRepository(): VideoExercisePreDefinitionExportationRepository

}