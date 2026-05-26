package org.emanuelpeg.nabu.adapter.impl

import org.emanuelpeg.nabu.adapter.SchemaAdapter
import org.emanuelpeg.nabu.model.*
import org.springframework.stereotype.Component

@Component
class SchemaAdapterH2 : SchemaAdapter {

    override val dbType: DbType = DbType.H2

    override fun generateCreateTableSql(table: Table): String {
        val elements = mutableListOf<String>()

        table.columns.forEach { col ->
            elements.add(buildColumnDefinition(col))
        }

        table.primaryKey?.let { pk ->
            val cols = pk.columns.joinToString(", ")
            val pkName = pk.name?.let { "CONSTRAINT $it " } ?: ""
            elements.add("${pkName}PRIMARY KEY ($cols)")
        }

        table.foreignKeys.forEach { fk ->
            elements.add(buildForeignKeyDefinition(fk))
        }

        return "CREATE TABLE ${table.name} (\n    ${elements.joinToString(",\n    ")}\n);"
    }

    override fun generateDropTableSql(tableName: String): String {
        return "DROP TABLE IF EXISTS $tableName;"
    }

    override fun generateRenameTableSql(oldName: String, newName: String): String {
        return "ALTER TABLE $oldName RENAME TO $newName;"
    }

    override fun generateAddColumnSql(tableName: String, column: Column): String {
        return "ALTER TABLE $tableName ADD COLUMN ${buildColumnDefinition(column)};"
    }

    override fun generateDropColumnSql(tableName: String, columnName: String): String {
        return "ALTER TABLE $tableName DROP COLUMN $columnName;"
    }

    override fun generateAlterColumnSql(tableName: String, oldColumnName: String, newColumn: Column): String {
        val statements = mutableListOf<String>()

        if (oldColumnName != newColumn.name) {
            statements.add("ALTER TABLE $tableName RENAME COLUMN $oldColumnName TO ${newColumn.name};")
        }

        val nullability = if (newColumn.isNullable) "DROP NOT NULL" else "SET NOT NULL"
        val dataType = mapToH2Type(newColumn.type)

        statements.add("ALTER TABLE $tableName ALTER COLUMN ${newColumn.name} SET DATA TYPE $dataType;")
        statements.add("ALTER TABLE $tableName ALTER COLUMN ${newColumn.name} $nullability;")

        return statements.joinToString("\n")
    }

    override fun generateAddPrimaryKeySql(tableName: String, primaryKey: PrimaryKey): String {
        val cols = primaryKey.columns.joinToString(", ")
        val pkName = primaryKey.name?.let { "CONSTRAINT $it " } ?: ""
        return "ALTER TABLE $tableName ADD ${pkName}PRIMARY KEY ($cols);"
    }

    override fun generateAddForeignKeySql(tableName: String, foreignKey: ForeignKey): String {
        return "ALTER TABLE $tableName ADD ${buildForeignKeyDefinition(foreignKey)};"
    }

    override fun generateDropConstraintSql(tableName: String, constraintName: String): String {
        return "ALTER TABLE $tableName DROP CONSTRAINT $constraintName;"
    }

    override fun generateCreateIndexSql(tableName: String, index: Index): String {
        val unique = if (index.isUnique) "UNIQUE " else ""
        val cols = index.columns.joinToString(", ")
        return "CREATE ${unique}INDEX ${index.name} ON $tableName ($cols);"
    }

    override fun generateDropIndexSql(tableName: String, indexName: String): String {
        return "DROP INDEX $indexName;"
    }

    private fun buildColumnDefinition(col: Column): String {
        val type = mapToH2Type(col.type)
        val nullability = if (col.isNullable) "" else " NOT NULL"
        val autoInc = if (col.isAutoIncrement) " AUTO_INCREMENT" else ""
        val defaultVal = col.defaultValue?.let { " DEFAULT $it" } ?: ""

        return "${col.name} $type$nullability$defaultVal$autoInc"
    }

    private fun buildForeignKeyDefinition(fk: ForeignKey): String {
        val name = fk.name?.let { "CONSTRAINT $it " } ?: ""
        val localCols = fk.columns.joinToString(", ")
        val refCols = fk.referencedColumns.joinToString(", ")
        val onDelete = " ON DELETE ${mapReferenceAction(fk.onDelete)}"
        val onUpdate = " ON UPDATE ${mapReferenceAction(fk.onUpdate)}"

        return "${name}FOREIGN KEY ($localCols) REFERENCES ${fk.referencedTable}($refCols)$onDelete$onUpdate"
    }

    private fun mapToH2Type(type: ColumnType): String {
        return when (type) {
            ColumnType.INTEGER -> "INT"
            ColumnType.DECIMAL -> "DECIMAL(19,4)"
            ColumnType.STRING -> "VARCHAR(255)"
            ColumnType.TEXT -> "TEXT"
            ColumnType.BOOLEAN -> "BOOLEAN"
            ColumnType.DATE -> "DATE"
            ColumnType.TIMESTAMP -> "TIMESTAMP"
            ColumnType.JSON -> "JSON"
        }
    }

    private fun mapReferenceAction(action: ReferenceAction): String {
        return when (action) {
            ReferenceAction.NO_ACTION -> "NO ACTION"
            ReferenceAction.RESTRICT -> "RESTRICT"
            ReferenceAction.CASCADE -> "CASCADE"
            ReferenceAction.SET_NULL -> "SET NULL"
            ReferenceAction.SET_DEFAULT -> "SET DEFAULT"
        }
    }
}