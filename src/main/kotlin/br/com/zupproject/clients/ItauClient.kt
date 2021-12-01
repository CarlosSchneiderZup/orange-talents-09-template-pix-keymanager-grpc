package br.com.zupproject.clients
import io.micronaut.http.client.annotation.Client
import br.com.zupproject.dtos.ContaItauResponse
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue

@Client("\${itau.contas.url}")
interface ItauClient {

    @Get(value = "/api/v1/clientes/{idCliente}/contas{?tipo}")
    fun buscaContaItau(@PathVariable idCliente : String, @QueryValue tipo : String) : HttpResponse<ContaItauResponse>
}