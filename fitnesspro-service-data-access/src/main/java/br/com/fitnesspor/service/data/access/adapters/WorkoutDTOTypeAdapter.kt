package br.com.fitnesspor.service.data.access.adapters

import br.com.fitnesspor.service.data.access.extensions.defaultServiceGSon
import br.com.fitnesspro.shared.communication.dtos.workout.WorkoutDTO
import br.com.fitnesspro.shared.communication.dtos.workout.interfaces.IWorkoutDTO
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

class WorkoutDTOTypeAdapter : TypeAdapter<IWorkoutDTO>() {
    override fun write(out: JsonWriter, value: IWorkoutDTO?) {
        val gson = GsonBuilder().defaultServiceGSon()
        gson.toJson(value as WorkoutDTO?, WorkoutDTO::class.java, out)
    }

    override fun read(reader: JsonReader): IWorkoutDTO? {
        val gson = GsonBuilder().defaultServiceGSon()
        return gson.fromJson(reader, WorkoutDTO::class.java)
    }
}