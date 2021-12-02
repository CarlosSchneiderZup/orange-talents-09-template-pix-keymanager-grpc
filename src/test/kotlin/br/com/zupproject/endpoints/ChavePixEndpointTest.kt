package br.com.zupproject.endpoints

import br.com.zupproject.PixRequest
import br.com.zupproject.PixServiceGrpc
import br.com.zupproject.clients.ItauClient
import br.com.zupproject.commons.validations.ChavePixValidator
import br.com.zupproject.dtos.ContaItauResponse
import br.com.zupproject.dtos.InstituicaoResponse
import br.com.zupproject.dtos.NovaChavePix
import br.com.zupproject.dtos.TitularResponse
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
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import java.util.*
import javax.persistence.Column
import javax.persistence.Embedded
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.validation.constraints.NotNull

@MicronautTest(transactional = false)
internal class ChavePixEndpointTest(
    val repository: ChavePixRepository,
    val grpcClient: PixServiceGrpc.PixServiceBlockingStub
) {

    @Inject
    lateinit var itauClient: ItauClient

    companion object {
        val ID_CLIENTE_TESTE = UUID.randomUUID()
    }

    @BeforeEach
    fun setUp() {
        repository.deleteAll()
    }

    @Test
    fun `deve cadastrar uma nova chave e receber o retorno com a chave e o cliente`() {
        Mockito.`when`(itauClient.buscaContaItau(idCliente = ID_CLIENTE_TESTE.toString(), tipo = "CONTA_CORRENTE"))
            .thenReturn(HttpResponse.ok(dadosDeConta()))

        val novaChave = "novoemail@gmail.com"

        val response = grpcClient.cadastraChave(
            PixRequest.newBuilder()
                .setCodigoCliente(ID_CLIENTE_TESTE.toString())
                .setChave(novaChave)
                .setTipoChave(PixRequest.TipoChave.EMAIL)
                .setTipoConta(PixRequest.TipoConta.CONTA_CORRENTE)
                .build()
        )

        with(response) {
            assertEquals(ID_CLIENTE_TESTE.toString(), clientId)
            assertEquals(novaChave, chavePix)
        }
    }

    @Test
    fun `deve cadastrar uma nova chave aleatoria e receber o retorno com a chave e o cliente`() {
        Mockito.`when`(itauClient.buscaContaItau(idCliente = ID_CLIENTE_TESTE.toString(), tipo = "CONTA_CORRENTE"))
            .thenReturn(HttpResponse.ok(dadosDeConta()))

        val response = grpcClient.cadastraChave(
            PixRequest.newBuilder()
                .setCodigoCliente(ID_CLIENTE_TESTE.toString())
                .setTipoChave(PixRequest.TipoChave.CHAVE_ALEATORIA)
                .setTipoConta(PixRequest.TipoConta.CONTA_CORRENTE)
                .build()
        )

        with(response) {
            assertEquals(ID_CLIENTE_TESTE.toString(), clientId)
            assertNotNull(chavePix)
        }
    }

    @Test
    fun `nao deve cadastrar uma nova chave quando a chave foi informada mas e chave aleatoria `() {
        val resposta = assertThrows<StatusRuntimeException> {
            grpcClient.cadastraChave(
                PixRequest.newBuilder()
                    .setCodigoCliente(ID_CLIENTE_TESTE.toString())
                    .setChave("53119801122")
                    .setTipoChave(PixRequest.TipoChave.CHAVE_ALEATORIA)
                    .setTipoConta(PixRequest.TipoConta.CONTA_CORRENTE)
                    .build()
            )
        }

        with(resposta) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("Não se deve informar uma chave ao selecionar chave aleatória", status.description)
        }
    }

    @Test
    fun `nao deve cadastrar uma chave de cpf com cpf invalido`() {
        val resposta = assertThrows<StatusRuntimeException> {
            grpcClient.cadastraChave(
                PixRequest.newBuilder()
                    .setCodigoCliente(ID_CLIENTE_TESTE.toString())
                    .setChave("531198011oo")
                    .setTipoChave(PixRequest.TipoChave.CPF)
                    .setTipoConta(PixRequest.TipoConta.CONTA_POUPANCA)
                    .build()
            )
        }

        with(resposta) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("Formato de CPF inválido", status.description)
        }
    }

    @Test
    fun `nao deve cadastrar uma chave de telefone com numero invalido`() {
        val resposta = assertThrows<StatusRuntimeException> {
            grpcClient.cadastraChave(
                PixRequest.newBuilder()
                    .setCodigoCliente(ID_CLIENTE_TESTE.toString())
                    .setChave("15")
                    .setTipoChave(PixRequest.TipoChave.TELEFONE_CELULAR)
                    .setTipoConta(PixRequest.TipoConta.CONTA_POUPANCA)
                    .build()
            )
        }

        with(resposta) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("Formato de telefone inválido", status.description)
        }
    }

    @Test
    fun `nao deve cadastrar uma chave de email com endereco invalido`() {
        val resposta = assertThrows<StatusRuntimeException> {
            grpcClient.cadastraChave(
                PixRequest.newBuilder()
                    .setCodigoCliente(ID_CLIENTE_TESTE.toString())
                    .setChave("novoemail.gmail.com")
                    .setTipoChave(PixRequest.TipoChave.EMAIL)
                    .setTipoConta(PixRequest.TipoConta.CONTA_POUPANCA)
                    .build()
            )
        }

        with(resposta) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("Formato de email inválido", status.description)
        }
    }

    @Test
    fun `nao deve cadastrar uma nova chave quando o objeto de envio estiver montado de forma incorreta `() {
        val resposta = assertThrows<StatusRuntimeException> {
            grpcClient.cadastraChave(
                PixRequest.newBuilder()
                    .build()
            )
        }

        with(resposta) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
        }
    }


    @Test
    fun `nao deve cadastrar uma nova chave quando a chave ja existe na base de dados e retornar erro`() {
        val chaveCpf = "12345678910"

        val conta = Conta(
            instituicao = "ITAU UNIBANCO SA",
            nomeTitular = "João dos testes",
            cpfTitular = "12345678910",
            agencia = "1234",
            numero = "010101"
        )

        val chavePix = ChavePix(
            pixId = chaveCpf,
            idCliente = ID_CLIENTE_TESTE,
            tipoDeChave = TipoDeChave.CPF,
            tipoDeConta = TipoDeConta.CONTA_CORRENTE,
            conta = conta
        )
        repository.save(chavePix)

        val resposta = assertThrows<StatusRuntimeException> {
            grpcClient.cadastraChave(
                PixRequest.newBuilder()
                    .setCodigoCliente(ID_CLIENTE_TESTE.toString())
                    .setChave(chaveCpf)
                    .setTipoChave(PixRequest.TipoChave.CPF)
                    .setTipoConta(PixRequest.TipoConta.CONTA_CORRENTE)
                    .build()
            )
        }

        with(resposta) {
            assertEquals(Status.ALREADY_EXISTS.code, status.code)
            assertEquals("A chave informada já foi registrada", status.description)
        }
    }

    @Test
    fun `nao deve cadastrar uma nova chave quando o cliente nao for encontrado no sistema itau`() {
        Mockito.`when`(itauClient.buscaContaItau(idCliente = ID_CLIENTE_TESTE.toString(), tipo = "CONTA_CORRENTE"))
            .thenReturn(HttpResponse.notFound())

        val resposta = assertThrows<StatusRuntimeException> {
            grpcClient.cadastraChave(
                PixRequest.newBuilder()
                    .setCodigoCliente(ID_CLIENTE_TESTE.toString())
                    .setChave("24680135799")
                    .setTipoChave(PixRequest.TipoChave.CPF)
                    .setTipoConta(PixRequest.TipoConta.CONTA_CORRENTE)
                    .build()
            )
        }

        with(resposta) {
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Conta não encontrada", status.description)
        }
    }

    private fun dadosDeConta(): ContaItauResponse? {
        return ContaItauResponse(
            tipo = "CONTA_CORRENTE",
            instituicao = InstituicaoResponse("UNIBANCO ITAU SA", "60701190"),
            agencia = "2180",
            numero = "212233",
            titular = TitularResponse(ID_CLIENTE_TESTE.toString(), "João dos testes", "12345678910")
        )
    }

    @MockBean(ItauClient::class)
    fun consultaItauClient(): ItauClient? {
        return Mockito.mock(ItauClient::class.java)
    }

    @Factory
    class client {
        @Bean
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): PixServiceGrpc.PixServiceBlockingStub {
            return PixServiceGrpc.newBlockingStub(channel)
        }
    }
}

