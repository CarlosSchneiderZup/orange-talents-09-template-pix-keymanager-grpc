package br.com.zupproject.entities

import br.com.zupproject.PixRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

internal class TipoDeContaTest {
    @Test
    fun `deve receber uma conta no formato TipoConta e transformar em uma conta do TipoDeConta`() {

        val contaCorrente = PixRequest.TipoConta.CONTA_CORRENTE
        val contaPoupanca = PixRequest.TipoConta.CONTA_POUPANCA

        assertEquals(TipoDeConta.CONTA_CORRENTE, TipoDeConta.getTipoDeConta(contaCorrente))
        assertEquals(TipoDeConta.CONTA_POUPANCA, TipoDeConta.getTipoDeConta(contaPoupanca))
    }

    @Test
    fun `deve devolver nulo quando recebe um TipoConta desconhecido`() {
        val contaNula = PixRequest.TipoConta.DESCONHECIDO
        assertNull(TipoDeConta.getTipoDeConta(contaNula))
    }
}