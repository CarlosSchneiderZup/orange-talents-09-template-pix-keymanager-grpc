package br.com.zupproject.commons.validations

import br.com.zupproject.commons.exceptions.ChaveInvalidaException
import br.com.zupproject.dtos.NovaChavePix
import br.com.zupproject.entities.TipoDeChave
import jakarta.inject.Singleton

@Singleton
class CpfValidator : ChavePixValidator {
    override fun valida(request: NovaChavePix) {
        if (request.tipoDeChave!!.equals(TipoDeChave.CPF) && !request.chaveInformada!!.matches(Regex("^[0-9]{11}$"))) {
            throw ChaveInvalidaException("Formato de CPF inválido")
        }
    }
}