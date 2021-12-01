package br.com.zupproject.dtos

import br.com.zupproject.entities.ChavePix
import br.com.zupproject.entities.Conta
import br.com.zupproject.entities.TipoDeChave
import br.com.zupproject.entities.TipoDeConta
import io.micronaut.core.annotation.Introspected
import java.util.*
import javax.validation.constraints.Max
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Introspected
data class NovaChavePix(
    @field:NotBlank
    val idCliente : String?,
    @field:NotNull
    val tipoDeChave: TipoDeChave?,
    @field:Max(77)
    val chaveInformada : String?,
    @field:NotNull
    val tipoDeConta: TipoDeConta?
) {

    fun toModel(conta: Conta): ChavePix {
        return ChavePix(
            idCliente = UUID.fromString(idCliente),
            tipoDeChave = tipoDeChave,
            pixId = if(this.tipoDeChave == TipoDeChave.ALEATORIO) UUID.randomUUID().toString() else chaveInformada,
            tipoDeConta = tipoDeConta,
            conta = conta
        )
    }
}