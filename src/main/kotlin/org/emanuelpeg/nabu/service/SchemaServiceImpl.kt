package org.emanuelpeg.nabu.service

import org.emanuelpeg.nabu.adapter.SchemaAdapterFactory
import org.emanuelpeg.nabu.model.*
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.sql.ResultSet

@Service
class SchemaServiceImpl(
    private val adapterFactory: SchemaAdapterFactory,
    private val jdbcTemplate: JdbcTemplate
) : SchemaService {

    override fun listTables(): List<String?> {
        val sql = """
            SELECT TABLE_NAME 
            FROM INFORMATION_SCHEMA.TABLES 
            WHERE TABLE_TYPE = 'BASE TABLE' AND TABLE_SCHEMA = 'PUBLIC'
        """.trimIndent()

        return jdbcTemplate.queryForList(sql, String::class.java)
    }

    override fun getTableDetails(tableName: String): Table? {
        val tables = listTables()
        if (tables.none { it.equals(tableName, ignoreCase = true) }) {
            return null
        }

        val sql = """
            SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE, COLUMN_DEFAULT
            FROM INFORMATION_SCHEMA.COLUMNS
            WHERE TABLE_NAME = ? AND TABLE_SCHEMA = 'PUBLIC'
            ORDER BY ORDINAL_POSITION
        """.trimIndent()

        val tableParam = tableName.uppercase()

        val columns = jdbcTemplate.query(sql, { rs: ResultSet, _: Int ->
            Column(
                name = rs.getString("COLUMN_NAME"),
                type = ColumnType.STRING,
                isNullable = rs.getString("IS_NULLABLE") == "YES",
                isAutoIncrement = false, // Extraer esto suele depender del motor
                defaultValue = rs.getString("COLUMN_DEFAULT")
            )
        }, tableParam)

        return Table(
            name = tableName,
            columns = columns)
    }

    @Transactional
    override fun createTable(table: Table) {
        val sql = adapterFactory.getActiveAdapter().generateCreateTableSql(table)
        jdbcTemplate.execute(sql)
    }

    @Transactional
    override fun dropTable(tableName: String) {
        val sql = adapterFactory.getActiveAdapter().generateDropTableSql(tableName)
        jdbcTemplate.execute(sql)
    }

    @Transactional
    override fun renameTable(oldName: String, newName: String) {
        val sql = adapterFactory.getActiveAdapter().generateRenameTableSql(oldName, newName)
        jdbcTemplate.execute(sql)
    }

    @Transactional
    override fun addColumn(tableName: String, column: Column) {
        val sql = adapterFactory.getActiveAdapter().generateAddColumnSql(tableName, column)
        jdbcTemplate.execute(sql)
    }

    @Transactional
    override fun dropColumn(tableName: String, columnName: String) {
        val sql = adapterFactory.getActiveAdapter().generateDropColumnSql(tableName, columnName)
        jdbcTemplate.execute(sql)
    }

    @Transactional
    override fun alterColumn(tableName: String, oldColumnName: String, newColumn: Column) {
        val sql = adapterFactory.getActiveAdapter().generateAlterColumnSql(tableName, oldColumnName, newColumn)
        // El adapter podría devolver varias sentencias separadas por \n (ej. H2)
        sql.split(";").filter { it.isNotBlank() }.forEach { statement ->
            jdbcTemplate.execute("$statement;")
        }
    }

    @Transactional
    override fun addPrimaryKey(tableName: String, primaryKey: PrimaryKey) {
        val sql = adapterFactory.getActiveAdapter().generateAddPrimaryKeySql(tableName, primaryKey)
        jdbcTemplate.execute(sql)
    }

    @Transactional
    override fun addForeignKey(tableName: String, foreignKey: ForeignKey) {
        val sql = adapterFactory.getActiveAdapter().generateAddForeignKeySql(tableName, foreignKey)
        jdbcTemplate.execute(sql)
    }

    @Transactional
    override fun dropConstraint(tableName: String, constraintName: String) {
        val sql = adapterFactory.getActiveAdapter().generateDropConstraintSql(tableName, constraintName)
        jdbcTemplate.execute(sql)
    }

    @Transactional
    override fun createIndex(tableName: String, index: Index) {
        val sql = adapterFactory.getActiveAdapter().generateCreateIndexSql(tableName, index)
        jdbcTemplate.execute(sql)
    }

    @Transactional
    override fun dropIndex(tableName: String, indexName: String) {
        val sql = adapterFactory.getActiveAdapter().generateDropIndexSql(tableName, indexName)
        jdbcTemplate.execute(sql)
    }
}