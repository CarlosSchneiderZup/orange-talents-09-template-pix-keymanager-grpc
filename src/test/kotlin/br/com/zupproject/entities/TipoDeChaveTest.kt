package br.com.zupproject.entities

import br.com.zupproject.PixRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

internal class TipoDeChaveTest {

    @Test
    fun `deve receber uma chave no formato TipoChave e transformar em uma chave do TipoDeChave `() {

        val chaveCpf = PixRequest.TipoChave.CPF
        val chaveEmail = PixRequest.TipoChave.EMAIL
        val chaveCelular = PixRequest.TipoChave.TELEFONE_CELULAR
        val chaveAleatoria = PixRequest.TipoChave.CHAVE_ALEATORIA

        assertEquals(TipoDeChave.CPF, TipoDeChave.getTipoDeChave(chaveCpf))
        assertEquals(TipoDeChave.EMAIL, TipoDeChave.getTipoDeChave(chaveEmail))
        assertEquals(TipoDeChave.TELEFONE, TipoDeChave.getTipoDeChave(chaveCelular))
        assertEquals(TipoDeChave.ALEATORIO, TipoDeChave.getTipoDeChave(chaveAleatoria))
    }

    @Test
    fun `deve devolver nulo quando recebe um TipoChave indefinido`() {
        val chaveNula = PixRequest.TipoChave.INDEFINIDO
        assertNull(TipoDeChave.getTipoDeChave(chaveNula))
    }
}