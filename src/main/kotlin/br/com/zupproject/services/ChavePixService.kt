package br.com.zupproject.services

import br.com.zupproject.clients.ItauClient
import br.com.zupproject.commons.exceptions.ChaveExistenteException
import br.com.zupproject.commons.exceptions.ClienteNaoEncontradoException
import br.com.zupproject.commons.validations.ChavePixValidator
import br.com.zupproject.dtos.NovaChavePix
import br.com.zupproject.entities.ChavePix
import br.com.zupproject.repositories.ChavePixRepository
import io.micronaut.validation.Validated
import jakarta.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Singleton
@Validated
class ChavePixService(
    val repository: ChavePixRepository,
    val itauClient: ItauClient,
    val validadorDeChavePix: List<ChavePixValidator>
) {

    @Transactional
    fun registraNovaChave(
        @Valid novaChavePix: NovaChavePix
    ): ChavePix {

        for (validador in validadorDeChavePix) {
            validador.valida(request = novaChavePix)
        }

        if (novaChavePix.chaveInformada != null && repository.existsByPixId(novaChavePix.chaveInformada)) {
            throw ChaveExistenteException("A chave informada já foi registrada")
        }

        val itauResponse = itauClient.buscaContaItau(novaChavePix.idCliente!!, novaChavePix.tipoDeConta!!.name)

        val conta = itauResponse.body()?.toModel() ?: throw ClienteNaoEncontradoException("Conta não encontrada")

        val chavePix = novaChavePix.toModel(conta)
        repository.save(chavePix)

        return chavePix
    }

}
