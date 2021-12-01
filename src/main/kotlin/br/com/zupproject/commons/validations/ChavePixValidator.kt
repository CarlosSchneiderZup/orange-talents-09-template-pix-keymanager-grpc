package br.com.zupproject.commons.validations

import br.com.zupproject.dtos.NovaChavePix
import jakarta.inject.Singleton

@Singleton
interface ChavePixValidator {

    fun valida(request: NovaChavePix)

}