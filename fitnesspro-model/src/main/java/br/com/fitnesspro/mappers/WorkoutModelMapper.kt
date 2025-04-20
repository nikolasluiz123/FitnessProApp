package br.com.fitnesspro.mappers

import br.com.fitnesspro.model.workout.Workout
import br.com.fitnesspro.model.workout.WorkoutGroup
import br.com.fitnesspro.shared.communication.dtos.workout.WorkoutDTO
import br.com.fitnesspro.shared.communication.dtos.workout.WorkoutGroupDTO

class WorkoutModelMapper: AbstractModelMapper() {

    fun getWorkoutDTO(workout: Workout): WorkoutDTO {
        return mapper.map(workout, WorkoutDTO::class.java)
    }

    fun getWorkoutGroupDTO(workoutGroup: WorkoutGroup): WorkoutGroupDTO {
        return mapper.map(workoutGroup, WorkoutGroupDTO::class.java)
    }
}