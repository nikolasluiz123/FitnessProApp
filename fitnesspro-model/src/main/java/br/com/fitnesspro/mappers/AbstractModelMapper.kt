package br.com.fitnesspro.mappers

import org.modelmapper.ModelMapper

abstract class AbstractModelMapper {

    protected val mapper = ModelMapper().apply {
        configuration.isSkipNullEnabled = true
    }
}