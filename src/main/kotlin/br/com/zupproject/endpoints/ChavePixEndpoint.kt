package br.com.zupproject.endpoints

import br.com.zupproject.PixRequest
import br.com.zupproject.PixResponse
import br.com.zupproject.PixServiceGrpc
import br.com.zupproject.commons.exceptions.ChaveExistenteException
import br.com.zupproject.commons.exceptions.ChaveInvalidaException
import br.com.zupproject.commons.exceptions.ClienteNaoEncontradoException
import br.com.zupproject.services.ChavePixService
import br.com.zupproject.services.toModel
import io.grpc.Status
import io.grpc.stub.StreamObserver
import jakarta.inject.Singleton
import javax.validation.ConstraintViolationException

@Singleton
class ChavePixEndpoint(
    val chavePixService: ChavePixService,
) : PixServiceGrpc.PixServiceImplBase() {

    override fun cadastraChave(request: PixRequest?, responseObserver: StreamObserver<PixResponse>) {

        val novaChave = request?.toModel()

        try {
            val chavePix = chavePixService.registraNovaChave(novaChave!!)

            responseObserver.onNext(
                PixResponse.newBuilder()
                    .setChavePix(chavePix.pixId)
                    .setClientId(chavePix.idCliente.toString())
                    .build()
            )
            responseObserver.onCompleted()
        } catch (e: ConstraintViolationException) {
            responseObserver.onError(
                Status.INVALID_ARGUMENT
                    .withDescription(e.message)
                    .asRuntimeException()
            )
        } catch (e: ChaveInvalidaException) {
            responseObserver.onError(
                Status.INVALID_ARGUMENT
                    .withDescription(e.message)
                    .asRuntimeException()
            )

        } catch (e: ChaveExistenteException) {
            responseObserver.onError(
                Status.ALREADY_EXISTS
                    .withDescription(e.message)
                    .asRuntimeException()
            )
        } catch (e: ClienteNaoEncontradoException) {
            responseObserver.onError(
                Status.NOT_FOUND
                    .withDescription(e.message)
                    .asRuntimeException()
            )
        }

    }
}

