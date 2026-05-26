package org.emanuelpeg.nabu.service

interface DataService {

    fun insert(tableName: String, payload: Map<String, Any>): Int

    fun findAll(tableName: String): List<Map<String, Any?>>

    fun findById(tableName: String, idColumn: String, idValue: Any): Map<String, Any?>?

    fun update(tableName: String, idColumn: String, idValue: Any, payload: Map<String, Any>): Int

    fun delete(tableName: String, idColumn: String, idValue: Any): Int
}