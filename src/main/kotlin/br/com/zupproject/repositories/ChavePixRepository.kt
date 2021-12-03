package br.com.zupproject.repositories

import br.com.zupproject.entities.ChavePix
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface ChavePixRepository : JpaRepository<ChavePix, String> {

    fun existsByPixId(pixId: String): Boolean

    fun findByPixIdAndIdCliente(pixId: String, idCliente: UUID): Optional<ChavePix>
}