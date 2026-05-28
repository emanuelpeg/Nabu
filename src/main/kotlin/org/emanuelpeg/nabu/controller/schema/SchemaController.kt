package org.emanuelpeg.nabu.controller.schema

import org.emanuelpeg.nabu.model.*
import org.emanuelpeg.nabu.service.SchemaService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/tables")
class SchemaController(
    private val schemaService: SchemaService
) {

    @GetMapping
    fun listTables(): ResponseEntity<List<String?>> {
        val tables = schemaService.listTables()
        return ResponseEntity.ok(tables)
    }

    @GetMapping("/{tableName}")
    fun getTableDetails(@PathVariable tableName: String): ResponseEntity<Any> {
        val tableInfo = schemaService.getTableDetails(tableName)
        return if (tableInfo != null) {
            ResponseEntity.ok(tableInfo)
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mapOf("error" to "La tabla '$tableName' no existe en el esquema."))
        }
    }

    @PostMapping
    fun createTable(@RequestBody table: Table): ResponseEntity<Map<String, String>> {
        schemaService.createTable(table)
        return ResponseEntity.status(HttpStatus.CREATED).body(mapOf("message" to "Table '${table.name}' created."))
    }

    @DeleteMapping("/{tableName}")
    fun dropTable(@PathVariable tableName: String): ResponseEntity<Map<String, String>> {
        schemaService.dropTable(tableName)
        return ResponseEntity.ok(mapOf("message" to "Table '$tableName' dropped."))
    }

    @PatchMapping("/{tableName}/rename")
    fun renameTable(
        @PathVariable tableName: String,
        @RequestParam newName: String
    ): ResponseEntity<Map<String, String>> {
        schemaService.renameTable(tableName, newName)
        return ResponseEntity.ok(mapOf("message" to "Table renamed to '$newName'."))
    }

    @PostMapping("/{tableName}/columns")
    fun addColumn(
        @PathVariable tableName: String,
        @RequestBody column: Column
    ): ResponseEntity<Map<String, String>> {
        schemaService.addColumn(tableName, column)
        return ResponseEntity.status(HttpStatus.CREATED).body(mapOf("message" to "Column '${column.name}' added."))
    }

    @DeleteMapping("/{tableName}/columns/{columnName}")
    fun dropColumn(
        @PathVariable tableName: String,
        @PathVariable columnName: String
    ): ResponseEntity<Map<String, String>> {
        schemaService.dropColumn(tableName, columnName)
        return ResponseEntity.ok(mapOf("message" to "Column '$columnName' dropped."))
    }

    @PutMapping("/{tableName}/columns/{oldColumnName}")
    fun alterColumn(
        @PathVariable tableName: String,
        @PathVariable oldColumnName: String,
        @RequestBody newColumn: Column
    ): ResponseEntity<Map<String, String>> {
        schemaService.alterColumn(tableName, oldColumnName, newColumn)
        return ResponseEntity.ok(mapOf("message" to "Column '$oldColumnName' altered."))
    }

    @PostMapping("/{tableName}/constraints/pk")
    fun addPrimaryKey(
        @PathVariable tableName: String,
        @RequestBody primaryKey: PrimaryKey
    ): ResponseEntity<Map<String, String>> {
        schemaService.addPrimaryKey(tableName, primaryKey)
        return ResponseEntity.status(HttpStatus.CREATED).body(mapOf("message" to "Primary key added."))
    }

    @PostMapping("/{tableName}/constraints/fk")
    fun addForeignKey(
        @PathVariable tableName: String,
        @RequestBody foreignKey: ForeignKey
    ): ResponseEntity<Map<String, String>> {
        schemaService.addForeignKey(tableName, foreignKey)
        return ResponseEntity.status(HttpStatus.CREATED).body(mapOf("message" to "Foreign key added."))
    }

    @DeleteMapping("/{tableName}/constraints/{constraintName}")
    fun dropConstraint(
        @PathVariable tableName: String,
        @PathVariable constraintName: String
    ): ResponseEntity<Map<String, String>> {
        schemaService.dropConstraint(tableName, constraintName)
        return ResponseEntity.ok(mapOf("message" to "Constraint '$constraintName' dropped."))
    }

    @PostMapping("/{tableName}/indices")
    fun createIndex(
        @PathVariable tableName: String,
        @RequestBody index: Index
    ): ResponseEntity<Map<String, String>> {
        schemaService.createIndex(tableName, index)
        return ResponseEntity.status(HttpStatus.CREATED).body(mapOf("message" to "Index '${index.name}' created."))
    }

    @DeleteMapping("/{tableName}/indices/{indexName}")
    fun dropIndex(
        @PathVariable tableName: String,
        @PathVariable indexName: String
    ): ResponseEntity<Map<String, String>> {
        schemaService.dropIndex(tableName, indexName)
        return ResponseEntity.ok(mapOf("message" to "Index '$indexName' dropped."))
    }
}