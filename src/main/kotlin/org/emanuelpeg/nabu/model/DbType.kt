package org.emanuelpeg.nabu.model

enum class DbType {
    H2,
    POSTGRES,
    MYSQL,
    SQLITE;

    companion object {
        fun fromString(value: String): DbType {
            return entries.find { it.name.equals(value, ignoreCase = true) }
                ?: throw IllegalArgumentException("Database not supported : $value")
        }
    }
}