package br.com.zupproject.entities

import javax.persistence.Embeddable

@Embeddable
class Conta(
    val instituicao: String,
    val nomeTitular: String,
    val cpfTitular: String,
    val agencia: String,
    val numero: String
) {

}