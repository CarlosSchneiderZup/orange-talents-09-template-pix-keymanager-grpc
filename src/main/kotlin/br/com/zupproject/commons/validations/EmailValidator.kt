package br.com.zupproject.commons.validations

import br.com.zupproject.commons.exceptions.ChaveInvalidaException
import br.com.zupproject.dtos.NovaChavePix
import br.com.zupproject.entities.TipoDeChave
import jakarta.inject.Singleton

@Singleton
class EmailValidator : ChavePixValidator {
    override fun valida(request: NovaChavePix) {
        if (request.tipoDeChave!!.equals(TipoDeChave.EMAIL) && !request.chaveInformada!!.matches(
                Regex(
                    "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
                    RegexOption.IGNORE_CASE
                )
            )
        ) {
            throw ChaveInvalidaException("Formato de email inválido")
        }
    }
}