package org.emanuelpeg.nabu.adapter

import org.emanuelpeg.nabu.model.*

interface SchemaAdapter {

    val dbType: DbType

    fun generateCreateTableSql(table: Table): String

    fun generateDropTableSql(tableName: String): String

    fun generateRenameTableSql(oldName: String, newName: String): String

    fun generateAddColumnSql(tableName: String, column: Column): String

    fun generateDropColumnSql(tableName: String, columnName: String): String

    fun generateAlterColumnSql(tableName: String, oldColumnName: String, newColumn: Column): String

    fun generateAddPrimaryKeySql(tableName: String, primaryKey: PrimaryKey): String

    fun generateAddForeignKeySql(tableName: String, foreignKey: ForeignKey): String

    fun generateDropConstraintSql(tableName: String, constraintName: String): String

    fun generateCreateIndexSql(tableName: String, index: Index): String

    fun generateDropIndexSql(tableName: String, indexName: String): String
}