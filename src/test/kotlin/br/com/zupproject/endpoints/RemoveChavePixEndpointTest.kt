package br.com.zupproject.endpoints

import br.com.zupproject.RemoveChavePixRequest
import br.com.zupproject.RemoveChavePixServiceGrpc
import br.com.zupproject.entities.ChavePix
import br.com.zupproject.entities.Conta
import br.com.zupproject.entities.TipoDeChave
import br.com.zupproject.entities.TipoDeConta
import br.com.zupproject.repositories.ChavePixRepository
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

@MicronautTest(transactional = false)
internal class RemoveChavePixEndpointTest(
    val repository: ChavePixRepository,
    val grpcClient: RemoveChavePixServiceGrpc.RemoveChavePixServiceBlockingStub
) {

    companion object {
        val ID_CLIENTE_TESTE = UUID.randomUUID()
    }

    @BeforeEach
    fun setUp() {
        repository.deleteAll()

        val contaTeste = Conta(
            instituicao = "ITAU UNIBANCO SA",
            nomeTitular = "João dos testes",
            cpfTitular = "01234567891",
            agencia = "1234",
            numero = "10101038"
        )

        val chaveTeste = ChavePix(
            pixId = "01234567891",
            idCliente = ID_CLIENTE_TESTE,
            tipoDeChave = TipoDeChave.CPF,
            tipoDeConta = TipoDeConta.CONTA_POUPANCA,
            conta = contaTeste
        )

        repository.save(chaveTeste)
    }

    @Test
    fun `deve remover uma chave ao informar uma chave e um id existente`() {


        val response = grpcClient.removeChave(
            RemoveChavePixRequest.newBuilder()
                .setIdUsuario(ID_CLIENTE_TESTE.toString())
                .setChavePix("01234567891")
                .build()
        )

        with(response) {
            assertEquals(ID_CLIENTE_TESTE.toString(), idUsuario)
            assertTrue(confirmacaoRemocao)
        }
    }

    @Test
    fun `um cliente nao pode remover uma chave existente, mas de outro cliente e lancar not found`() {

        val exception = assertThrows<StatusRuntimeException> {
            grpcClient.removeChave(
                RemoveChavePixRequest.newBuilder()
                    .setIdUsuario(UUID.randomUUID().toString())
                    .setChavePix("01234567891")
                    .build()
            )
        }

        with(exception) {
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Chave pix não encontrada para este cliente, ou inexistente", status.description)
        }
    }

    @Test
    fun `nao deve remover uma chave nao existente e lancar not found`() {

        val exception = assertThrows<StatusRuntimeException> {
            grpcClient.removeChave(
                RemoveChavePixRequest.newBuilder()
                    .setIdUsuario(ID_CLIENTE_TESTE.toString())
                    .setChavePix("99999999999")
                    .build()
            )
        }

        with(exception) {
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Chave pix não encontrada para este cliente, ou inexistente", status.description)
        }
    }

    @Test
    fun `deve lancar uma invalid argument exception ao enviar um id que nao e um uuid`() {

        val exception = assertThrows<StatusRuntimeException> {
            grpcClient.removeChave(
                RemoveChavePixRequest.newBuilder()
                    .setIdUsuario("nao e um uuid valido")
                    .setChavePix("01234567891")
                    .build()
            )
        }

        with(exception) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("A chave informada não é válida", status.description)
        }
    }

    @Factory
    class client {
        @Bean
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): RemoveChavePixServiceGrpc.RemoveChavePixServiceBlockingStub {
            return RemoveChavePixServiceGrpc.newBlockingStub(channel)
        }
    }
}