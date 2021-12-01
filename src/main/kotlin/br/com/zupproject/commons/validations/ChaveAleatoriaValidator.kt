package br.com.zupproject.commons.validations

import br.com.zupproject.commons.exceptions.ChaveInvalidaException
import br.com.zupproject.dtos.NovaChavePix
import br.com.zupproject.entities.TipoDeChave
import jakarta.inject.Singleton

@Singleton
class ChaveAleatoriaValidator : ChavePixValidator {
    override fun valida(request: NovaChavePix) {
        if( request.tipoDeChave!!.equals(TipoDeChave.ALEATORIO) && !request.chaveInformada.isNullOrBlank())
            throw ChaveInvalidaException("Não se deve informar uma chave ao selecionar chave aleatória")
    }
}