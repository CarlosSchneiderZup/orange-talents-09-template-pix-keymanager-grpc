package br.com.zupproject.dtos

import br.com.zupproject.entities.Conta

data class ContaItauResponse(
    val tipo: String,
    val instituicao: InstituicaoResponse,
    val agencia: String,
    val numero: String,
    val titular: TitularResponse
) {

    fun toModel(): Conta {
        return Conta(
            instituicao = this.instituicao.nome,
            nomeTitular = this.titular.nome,
            cpfTitular = this.titular.cpf,
            agencia = this.agencia,
            numero = this.numero
        )
    }
}

data class InstituicaoResponse(
    val nome: String,
    val ispb: String
)

data class TitularResponse(
    val id: String,
    val nome: String,
    val cpf: String
)