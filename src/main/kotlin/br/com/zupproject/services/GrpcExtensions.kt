package br.com.zupproject.services

import br.com.zupproject.PixRequest
import br.com.zupproject.dtos.NovaChavePix
import br.com.zupproject.entities.ChavePix
import br.com.zupproject.entities.TipoDeChave
import br.com.zupproject.entities.TipoDeConta

fun PixRequest.toModel() : NovaChavePix {
    return NovaChavePix(
        idCliente = codigoCliente,
        chaveInformada = chave,
        tipoDeConta = when(tipoConta) {
            PixRequest.TipoConta.DESCONHECIDO -> null
            else -> TipoDeConta.getTipoDeConta(tipoConta)
        },
        tipoDeChave = when(tipoChave) {
            PixRequest.TipoChave.INDEFINIDO -> null
            else -> TipoDeChave.getTipoDeChave(tipoChave)
        }

    )
}