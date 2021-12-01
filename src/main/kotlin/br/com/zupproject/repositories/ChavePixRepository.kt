package br.com.zupproject.repositories

import br.com.zupproject.entities.ChavePix
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository

@Repository
interface ChavePixRepository : JpaRepository<ChavePix, String> {

    fun existsByPixId(pixId: String): Boolean
}