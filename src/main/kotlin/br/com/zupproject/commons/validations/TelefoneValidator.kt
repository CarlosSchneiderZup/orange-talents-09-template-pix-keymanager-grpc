package br.com.zupproject.commons.validations

import br.com.zupproject.commons.exceptions.ChaveInvalidaException
import br.com.zupproject.dtos.NovaChavePix
import br.com.zupproject.entities.TipoDeChave
import jakarta.inject.Singleton

@Singleton
class TelefoneValidator : ChavePixValidator {
    override fun valida(request: NovaChavePix) {
        if (request.tipoDeChave!!.equals(TipoDeChave.TELEFONE) && !request.chaveInformada!!.matches(Regex("^\\+[1-9][0-9]\\d{1,14}\$"))) {
            throw ChaveInvalidaException("Formato de telefone inválido")
        }
    }
}