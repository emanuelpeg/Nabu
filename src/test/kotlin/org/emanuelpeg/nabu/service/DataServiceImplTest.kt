package org.emanuelpeg.nabu.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate

@SpringBootTest
class DataServiceImplTest {

    @Autowired
    private lateinit var dataService: DataServiceImpl

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @BeforeEach
    fun setupDatabase() {
        // Aseguramos un estado limpio antes de cada test
        jdbcTemplate.execute("DROP TABLE IF EXISTS usuarios;")
        jdbcTemplate.execute(
            """
            CREATE TABLE usuarios (
                id INT AUTO_INCREMENT PRIMARY KEY,
                nombre VARCHAR(255) NOT NULL,
                email VARCHAR(255) UNIQUE,
                edad INT
            );
            """.trimIndent()
        )
    }

    @Test
    fun `debe insertar un registro y devolver el numero de filas afectadas`() {
        val payload = mapOf(
            "nombre" to "Emanuel",
            "email" to "emanuel@example.com",
            "edad" to 30
        )

        val rowsAffected = dataService.insert("usuarios", payload)

        assertEquals(1, rowsAffected)

        // Verificamos directamente con JdbcTemplate para asegurar que se guardó
        val count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM usuarios", Int::class.java)
        assertEquals(1, count)
    }

    @Test
    fun `debe recuperar todos los registros`() {
        // Insertamos datos iniciales manualmente
        jdbcTemplate.update("INSERT INTO usuarios (nombre, email, edad) VALUES ('Ana', 'ana@test.com', 25)")
        jdbcTemplate.update("INSERT INTO usuarios (nombre, email, edad) VALUES ('Carlos', 'carlos@test.com', 40)")

        val registros = dataService.findAll("usuarios")

        assertEquals(2, registros.size)
        // Verificamos que las claves (nombres de columna) estén presentes
        assertTrue(registros[0].keys.containsAll(listOf("ID", "NOMBRE", "EMAIL", "EDAD")))
    }

    @Test
    fun `debe recuperar un registro por ID`() {
        jdbcTemplate.update("INSERT INTO usuarios (nombre, email, edad) VALUES ('Luis', 'luis@test.com', 35)")

        // Asumiendo que es el primer registro, el ID autogenerado será 1
        val registro = dataService.findById("usuarios", "id", 1)

        assertNotNull(registro)
        assertEquals("Luis", registro?.get("NOMBRE"))
        assertEquals(35, registro?.get("EDAD"))
    }

    @Test
    fun `debe devolver null si el ID no existe`() {
        val registro = dataService.findById("usuarios", "id", 999)
        assertNull(registro)
    }

    @Test
    fun `debe actualizar un registro existente`() {
        jdbcTemplate.update("INSERT INTO usuarios (nombre, email, edad) VALUES ('Maria', 'maria@test.com', 28)")

        val payloadActualizacion = mapOf(
            "nombre" to "Maria Lopez",
            "edad" to 29
        )

        val rowsAffected = dataService.update("usuarios", "id", 1, payloadActualizacion)

        assertEquals(1, rowsAffected)

        // Verificamos que los cambios se aplicaron y el resto (email) se mantuvo
        val registroActualizado = dataService.findById("usuarios", "id", 1)
        assertEquals("Maria Lopez", registroActualizado?.get("NOMBRE"))
        assertEquals("maria@test.com", registroActualizado?.get("EMAIL"))
        assertEquals(29, registroActualizado?.get("EDAD"))
    }

    @Test
    fun `debe borrar un registro por ID`() {
        jdbcTemplate.update("INSERT INTO usuarios (nombre, email) VALUES ('Pedro', 'pedro@test.com')")

        val rowsAffected = dataService.delete("usuarios", "id", 1)

        assertEquals(1, rowsAffected)

        val count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM usuarios", Int::class.java)
        assertEquals(0, count)
    }
}