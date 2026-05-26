package org.emanuelpeg.nabu.service

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DataServiceImpl(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) : DataService {

    @Transactional
    override fun insert(tableName: String, payload: Map<String, Any>): Int {
        if (payload.isEmpty()) throw IllegalArgumentException("El payload no puede estar vacío")

        val columns = payload.keys.joinToString(", ")
        val placeholders = payload.keys.joinToString(", ") { ":$it" }

        val sql = "INSERT INTO $tableName ($columns) VALUES ($placeholders)"

        return jdbcTemplate.update(sql, payload)
    }

    override fun findAll(tableName: String): List<Map<String, Any?>> {
        val sql = "SELECT * FROM $tableName"
        return jdbcTemplate.queryForList(sql, emptyMap<String, Any>())
    }

    override fun findById(tableName: String, idColumn: String, idValue: Any): Map<String, Any?>? {
        val sql = "SELECT * FROM $tableName WHERE $idColumn = :id"
        val params = mapOf("id" to idValue)

        return jdbcTemplate.queryForList(sql, params).firstOrNull()
    }

    @Transactional
    override fun update(tableName: String, idColumn: String, idValue: Any, payload: Map<String, Any>): Int {
        if (payload.isEmpty()) return 0

        val setClause = payload.keys.joinToString(", ") { "$it = :$it" }
        val sql = "UPDATE $tableName SET $setClause WHERE $idColumn = :_id_param"

        val params = payload.toMutableMap()
        params["_id_param"] = idValue

        return jdbcTemplate.update(sql, params)
    }

    @Transactional
    override fun delete(tableName: String, idColumn: String, idValue: Any): Int {
        val sql = "DELETE FROM $tableName WHERE $idColumn = :id"
        val params = mapOf("id" to idValue)

        return jdbcTemplate.update(sql, params)
    }
}