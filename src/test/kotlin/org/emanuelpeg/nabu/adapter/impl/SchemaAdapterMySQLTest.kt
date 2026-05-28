package org.emanuelpeg.nabu.adapter.impl

import org.emanuelpeg.nabu.model.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SchemaAdapterMySQLTest {

    private val adapter = SchemaAdapterMySQL()

    // ==========================================
    // --- Pruebas a Nivel de Tabla ---
    // ==========================================

    @Test
    fun `debe generar el SQL correcto para crear una tabla compleja con PK y FK`() {
        val table = Table(
            name = "usuarios",
            columns = listOf(
                Column("id", ColumnType.INTEGER, isNullable = false, isAutoIncrement = true),
                Column("role_id", ColumnType.INTEGER, isNullable = false),
                Column("nombre", ColumnType.STRING, isNullable = false),
                Column("activo", ColumnType.BOOLEAN, defaultValue = "TRUE")
            ),
            primaryKey = PrimaryKey("pk_usuarios", listOf("id")),
            foreignKeys = listOf(
                ForeignKey(
                    name = "fk_usuarios_roles",
                    columns = listOf("role_id"),
                    referencedTable = "roles",
                    referencedColumns = listOf("id"),
                    onDelete = ReferenceAction.RESTRICT,
                    onUpdate = ReferenceAction.CASCADE
                )
            )
        )

        val sqlGenerado = adapter.generateCreateTableSql(table)

        val sqlEsperado = """
            CREATE TABLE usuarios (
                id INT NOT NULL AUTO_INCREMENT,
                role_id INT NOT NULL,
                nombre VARCHAR(255) NOT NULL,
                activo BOOLEAN DEFAULT TRUE,
                CONSTRAINT pk_usuarios PRIMARY KEY (id),
                CONSTRAINT fk_usuarios_roles FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE RESTRICT ON UPDATE CASCADE
            );
        """.trimIndent()

        assertEquals(sqlEsperado, sqlGenerado)
    }

    @Test
    fun `debe generar el SQL correcto para borrar una tabla`() {
        val sqlGenerado = adapter.generateDropTableSql("usuarios")
        assertEquals("DROP TABLE IF EXISTS usuarios;", sqlGenerado)
    }

    @Test
    fun `debe generar el SQL correcto para renombrar una tabla`() {
        val sqlGenerado = adapter.generateRenameTableSql("usuarios", "clientes")
        assertEquals("RENAME TABLE usuarios TO clientes;", sqlGenerado)
    }

    // ==========================================
    // --- Pruebas a Nivel de Columna ---
    // ==========================================

    @Test
    fun `debe generar el SQL correcto para agregar una columna`() {
        val nuevaColumna = Column("fecha_creacion", ColumnType.TIMESTAMP, isNullable = true)
        val sqlGenerado = adapter.generateAddColumnSql("usuarios", nuevaColumna)

        // Recordemos que mapeamos TIMESTAMP a DATETIME en MySQL
        assertEquals("ALTER TABLE usuarios ADD COLUMN fecha_creacion DATETIME;", sqlGenerado)
    }

    @Test
    fun `debe generar el SQL correcto para borrar una columna`() {
        val sqlGenerado = adapter.generateDropColumnSql("usuarios", "edad")
        assertEquals("ALTER TABLE usuarios DROP COLUMN edad;", sqlGenerado)
    }

    @Test
    fun `debe generar el SQL correcto usando CHANGE para alterar una columna`() {
        val columnaModificada = Column("nombre_completo", ColumnType.STRING, isNullable = false)

        val sqlGenerado = adapter.generateAlterColumnSql("usuarios", "nombre", columnaModificada)

        val sqlEsperado = "ALTER TABLE usuarios CHANGE nombre nombre_completo VARCHAR(255) NOT NULL;"

        assertEquals(sqlEsperado, sqlGenerado)
    }

    @Test
    fun `debe manejar strings correctamente en valores default`() {
        val nuevaColumna = Column("estado", ColumnType.STRING, isNullable = false, defaultValue = "ACTIVO")
        val sqlGenerado = adapter.generateAddColumnSql("usuarios", nuevaColumna)

        assertEquals("ALTER TABLE usuarios ADD COLUMN estado VARCHAR(255) NOT NULL DEFAULT 'ACTIVO';", sqlGenerado)
    }

    // ==========================================
    // --- Pruebas a Nivel de Restricciones e Índices ---
    // ==========================================

    @Test
    fun `debe generar el SQL correcto para agregar una llave primaria`() {
        val pk = PrimaryKey("pk_nueva", listOf("id", "tenant_id"))
        val sqlGenerado = adapter.generateAddPrimaryKeySql("usuarios", pk)

        assertEquals("ALTER TABLE usuarios ADD CONSTRAINT pk_nueva PRIMARY KEY (id, tenant_id);", sqlGenerado)
    }

    @Test
    fun `debe generar el SQL correcto para agregar una llave foranea`() {
        val fk = ForeignKey(
            name = "fk_externa",
            columns = listOf("empresa_id"),
            referencedTable = "empresas",
            referencedColumns = listOf("id")
        )
        val sqlGenerado = adapter.generateAddForeignKeySql("usuarios", fk)

        assertEquals(
            "ALTER TABLE usuarios ADD CONSTRAINT fk_externa FOREIGN KEY (empresa_id) REFERENCES empresas(id) ON DELETE NO ACTION ON UPDATE NO ACTION;",
            sqlGenerado
        )
    }

    @Test
    fun `debe generar el SQL correcto para borrar una restriccion`() {
        val sqlGenerado = adapter.generateDropConstraintSql("usuarios", "fk_externa")
        // Nota: asumiendo soporte para MySQL 8+
        assertEquals("ALTER TABLE usuarios DROP CONSTRAINT fk_externa;", sqlGenerado)
    }

    @Test
    fun `debe generar el SQL correcto para crear un indice normal`() {
        val index = Index("idx_apellido", listOf("apellido"))
        val sqlGenerado = adapter.generateCreateIndexSql("usuarios", index)

        assertEquals("CREATE INDEX idx_apellido ON usuarios (apellido);", sqlGenerado)
    }

    @Test
    fun `debe generar el SQL correcto para borrar un indice especificando la tabla`() {
        val sqlGenerado = adapter.generateDropIndexSql("usuarios", "idx_apellido")
        assertEquals("DROP INDEX idx_apellido ON usuarios;", sqlGenerado)
    }
}