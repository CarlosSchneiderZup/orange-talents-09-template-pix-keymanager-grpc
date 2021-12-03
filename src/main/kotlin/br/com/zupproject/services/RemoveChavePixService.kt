package br.com.zupproject.services

import br.com.zupproject.commons.exceptions.ChaveNaoEncontradaException
import br.com.zupproject.repositories.ChavePixRepository
import io.micronaut.validation.Validated
import jakarta.inject.Singleton
import java.util.*
import javax.validation.constraints.NotBlank

@Singleton
@Validated
class RemoveChavePixService(val chavePixRepository: ChavePixRepository) {

    fun remove(
        @NotBlank idCliente: String,
        @NotBlank chavePix: String
    ) {

        val idClienteUuid = UUID.fromString(idCliente)

        val chave = chavePixRepository.findByPixIdAndIdCliente(pixId = chavePix, idCliente = idClienteUuid)
            .orElseThrow { ChaveNaoEncontradaException("Chave pix não encontrada para este cliente, ou inexistente") }

        chavePixRepository.deleteById(chavePix)
    }
}
