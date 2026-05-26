package org.emanuelpeg.nabu.controller.data

import org.emanuelpeg.nabu.service.DataService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/data")
class DataController(
    private val dataService: DataService
) {

    @PostMapping("/{tableName}")
    fun insertRow(
        @PathVariable tableName: String,
        @RequestBody payload: Map<String, Any>
    ): ResponseEntity<Map<String, Any>> {
        val rowsAffected = dataService.insert(tableName, payload)
        return ResponseEntity.status(HttpStatus.CREATED).body(
            mapOf("message" to "Registro insertado", "rowsAffected" to rowsAffected)
        )
    }

    @GetMapping("/{tableName}")
    fun getAllRows(@PathVariable tableName: String): ResponseEntity<List<Map<String, Any?>>> {
        val data = dataService.findAll(tableName)
        return ResponseEntity.ok(data)
    }

    @GetMapping("/{tableName}/{idColumn}/{idValue}")
    fun getRowById(
        @PathVariable tableName: String,
        @PathVariable idColumn: String,
        @PathVariable idValue: String
    ): ResponseEntity<Any> {
        val row = dataService.findById(tableName, idColumn, idValue)
        return if (row != null) {
            ResponseEntity.ok(row)
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("error" to "Registro no encontrado"))
        }
    }

    @PutMapping("/{tableName}/{idColumn}/{idValue}")
    fun updateRow(
        @PathVariable tableName: String,
        @PathVariable idColumn: String,
        @PathVariable idValue: String,
        @RequestBody payload: Map<String, Any>
    ): ResponseEntity<Map<String, Any>> {
        val rowsAffected = dataService.update(tableName, idColumn, idValue, payload)
        return if (rowsAffected > 0) {
            ResponseEntity.ok(mapOf("message" to "Registro actualizado", "rowsAffected" to rowsAffected))
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("error" to "Registro no encontrado para actualizar"))
        }
    }

    @DeleteMapping("/{tableName}/{idColumn}/{idValue}")
    fun deleteRow(
        @PathVariable tableName: String,
        @PathVariable idColumn: String,
        @PathVariable idValue: String
    ): ResponseEntity<Map<String, Any>> {
        val rowsAffected = dataService.delete(tableName, idColumn, idValue)
        return if (rowsAffected > 0) {
            ResponseEntity.ok(mapOf("message" to "Registro eliminado", "rowsAffected" to rowsAffected))
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("error" to "Registro no encontrado para eliminar"))
        }
    }
}