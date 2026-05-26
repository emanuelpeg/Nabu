package org.emanuelpeg.nabu.adapter

import org.emanuelpeg.nabu.model.DbType
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import jakarta.annotation.PostConstruct

@Service
class SchemaAdapterFactory(
    private val adapters: List<SchemaAdapter>,
    @Value("\${nabu.db.type:H2}") private val dbTypeProperty: String
) {
    private lateinit var activeAdapter: SchemaAdapter

    @PostConstruct
    fun init() {
        val targetDbType = DbType.fromString(dbTypeProperty)
        activeAdapter = adapters.find { it.dbType == targetDbType }
            ?: throw IllegalStateException("No se encontró una implementación de SchemaAdapter para $targetDbType")
    }

    fun getActiveAdapter(): SchemaAdapter = activeAdapter
}