package org.emanuelpeg.nabu.model

enum class ColumnType {
    INTEGER,
    DECIMAL,
    STRING,    // Se traducirá a VARCHAR
    TEXT,      // Se traducirá a TEXT/CLOB
    BOOLEAN,
    DATE,
    TIMESTAMP,
    JSON
}

data class Column(
    val name: String,
    val type: ColumnType,
    val isNullable: Boolean = true,
    val isAutoIncrement: Boolean = false,
    val defaultValue: String? = null
)

data class PrimaryKey(
    val name: String? = null, // Opcional: el motor DB le suele dar un nombre por defecto
    val columns: List<String>
)


enum class ReferenceAction {
    NO_ACTION, RESTRICT, CASCADE, SET_NULL, SET_DEFAULT
}

data class ForeignKey(
    val name: String? = null,
    val columns: List<String>,
    val referencedTable: String,
    val referencedColumns: List<String>,
    val onDelete: ReferenceAction = ReferenceAction.NO_ACTION,
    val onUpdate: ReferenceAction = ReferenceAction.NO_ACTION
)

data class Index(
    val name: String,
    val columns: List<String>,
    val isUnique: Boolean = false
)

data class Table(
    val name: String,
    val columns: List<Column>,
    val primaryKey: PrimaryKey? = null,
    val foreignKeys: List<ForeignKey> = emptyList(),
    val indices: List<Index> = emptyList()
)