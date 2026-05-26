package org.emanuelpeg.nabu.service

import org.emanuelpeg.nabu.adapter.SchemaAdapterFactory
import org.emanuelpeg.nabu.model.*
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SchemaServiceImpl(
    private val adapterFactory: SchemaAdapterFactory,
    private val jdbcTemplate: JdbcTemplate
) : SchemaService {

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