package br.com.zupproject.entities

import br.com.zupproject.PixRequest
import org.hibernate.annotations.Type
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
class ChavePix(

    @field:NotBlank
    @Column(nullable = false, unique = true, length = 77)
    @Id
    val pixId: String?,
    @field:NotNull
    @Type(type="org.hibernate.type.UUIDCharType")
    @Column(nullable = false)
    val idCliente: UUID?,
    @field:NotNull
    @Enumerated(EnumType.STRING)
    val tipoDeChave: TipoDeChave?,
    @field:NotNull
    @Enumerated(EnumType.STRING)
    val tipoDeConta: TipoDeConta?,

    @Embedded
    val conta: Conta
)

enum class TipoDeChave() {
    CPF(),
    TELEFONE(),
    EMAIL(),
    ALEATORIO();

    companion object {
        fun getTipoDeChave(tipoChave: PixRequest.TipoChave): TipoDeChave? {
            when (tipoChave) {
                PixRequest.TipoChave.CPF -> return CPF
                PixRequest.TipoChave.TELEFONE_CELULAR -> return TELEFONE
                PixRequest.TipoChave.EMAIL -> return EMAIL
                PixRequest.TipoChave.CHAVE_ALEATORIA -> return ALEATORIO
                else -> {
                    return null
                }
            }
        }
    }
}

enum class TipoDeConta(val tipoInformado: PixRequest.TipoConta) {
    CONTA_CORRENTE(PixRequest.TipoConta.CONTA_CORRENTE),
    CONTA_POUPANCA(PixRequest.TipoConta.CONTA_POUPANCA);

    companion object {
        fun getTipoDeConta(tipoConta: PixRequest.TipoConta): TipoDeConta? {
            when (tipoConta) {
                PixRequest.TipoConta.CONTA_CORRENTE -> return CONTA_CORRENTE
                PixRequest.TipoConta.CONTA_POUPANCA -> return CONTA_POUPANCA
                else -> {
                    return null
                }
            }
        }
    }
}