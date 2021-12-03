package br.com.zupproject.endpoints

import br.com.zupproject.RemoveChavePixRequest
import br.com.zupproject.RemoveChavePixResponse
import br.com.zupproject.RemoveChavePixServiceGrpc
import br.com.zupproject.commons.exceptions.ChaveNaoEncontradaException
import br.com.zupproject.services.RemoveChavePixService
import io.grpc.Status
import io.grpc.stub.StreamObserver
import jakarta.inject.Singleton

@Singleton
class RemoveChavePixEndpoint(val deletaChaveService : RemoveChavePixService) : RemoveChavePixServiceGrpc.RemoveChavePixServiceImplBase() {

    override fun removeChave(
        request: RemoveChavePixRequest,
        responseObserver: StreamObserver<RemoveChavePixResponse>
    ) {

        try {
            deletaChaveService.remove(request.idUsuario, request.chavePix)

            responseObserver.onNext(RemoveChavePixResponse.newBuilder()
                .setIdUsuario(request.idUsuario)
                .setConfirmacaoRemocao(true)
                .build())
            responseObserver.onCompleted()
        } catch(e : IllegalArgumentException) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                .withDescription("A chave informada não é válida")
                .asRuntimeException())
        } catch (e: ChaveNaoEncontradaException) {
            responseObserver.onError(Status.NOT_FOUND
                .withDescription(e.message)
                .asRuntimeException())
        }
    }
}