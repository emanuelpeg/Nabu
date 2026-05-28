package org.emanuelpeg.nabu.service

import org.emanuelpeg.nabu.model.Column
import org.emanuelpeg.nabu.model.ForeignKey
import org.emanuelpeg.nabu.model.Index
import org.emanuelpeg.nabu.model.PrimaryKey
import org.emanuelpeg.nabu.model.Table

interface SchemaService {

    fun listTables(): List<String?>

    fun getTableDetails(tableName: String): Table?

    fun createTable(table: Table)

    fun dropTable(tableName: String)

    fun renameTable(oldName: String, newName: String)

    fun addColumn(tableName: String, column: Column)

    fun dropColumn(tableName: String, columnName: String)

    fun alterColumn(tableName: String, oldColumnName: String, newColumn: Column)

    fun addPrimaryKey(tableName: String, primaryKey: PrimaryKey)

    fun addForeignKey(tableName: String, foreignKey: ForeignKey)

    fun dropConstraint(tableName: String, constraintName: String)

    fun createIndex(tableName: String, index: Index)

    fun dropIndex(tableName: String, indexName: String)
}