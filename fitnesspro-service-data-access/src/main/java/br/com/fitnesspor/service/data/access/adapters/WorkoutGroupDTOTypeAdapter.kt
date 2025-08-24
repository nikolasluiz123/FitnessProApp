package br.com.fitnesspor.service.data.access.adapters

import br.com.fitnesspor.service.data.access.extensions.defaultServiceGSon
import br.com.fitnesspro.shared.communication.dtos.workout.WorkoutGroupDTO
import br.com.fitnesspro.shared.communication.dtos.workout.interfaces.IWorkoutGroupDTO
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

class WorkoutGroupDTOTypeAdapter : TypeAdapter<IWorkoutGroupDTO>() {
    override fun write(out: JsonWriter, value: IWorkoutGroupDTO?) {
        val gson = GsonBuilder().defaultServiceGSon()
        gson.toJson(value as WorkoutGroupDTO?, WorkoutGroupDTO::class.java, out)
    }

    override fun read(reader: JsonReader): IWorkoutGroupDTO {
        val gson = GsonBuilder().defaultServiceGSon()
        return gson.fromJson(reader, WorkoutGroupDTO::class.java)
    }
}