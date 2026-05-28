package org.emanuelpeg.nabu.adapter.impl

import org.emanuelpeg.nabu.model.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SchemaAdapterH2Test {

    private val adapter = SchemaAdapterH2()

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
        assertEquals("ALTER TABLE usuarios RENAME TO clientes;", sqlGenerado)
    }

    @Test
    fun `debe generar el SQL correcto para agregar una columna`() {
        val nuevaColumna = Column("fecha_creacion", ColumnType.TIMESTAMP, isNullable = true)
        val sqlGenerado = adapter.generateAddColumnSql("usuarios", nuevaColumna)

        assertEquals("ALTER TABLE usuarios ADD COLUMN fecha_creacion TIMESTAMP;", sqlGenerado)
    }

    @Test
    fun `debe generar el SQL correcto para borrar una columna`() {
        val sqlGenerado = adapter.generateDropColumnSql("usuarios", "edad")
        assertEquals("ALTER TABLE usuarios DROP COLUMN edad;", sqlGenerado)
    }

    @Test
    fun `debe generar sentencias multiples al alterar una columna completa`() {
        val columnaModificada = Column("nombre_completo", ColumnType.STRING, isNullable = false)

        val sqlGenerado = adapter.generateAlterColumnSql("usuarios", "nombre", columnaModificada)

        val sqlEsperado = """
            ALTER TABLE usuarios RENAME COLUMN nombre TO nombre_completo;
            ALTER TABLE usuarios ALTER COLUMN nombre_completo SET DATA TYPE VARCHAR(255);
            ALTER TABLE usuarios ALTER COLUMN nombre_completo SET NOT NULL;
        """.trimIndent()

        assertEquals(sqlEsperado, sqlGenerado)
    }

    @Test
    fun `no debe generar RENAME si el nombre de la columna no cambia`() {
        val columnaModificada = Column("edad", ColumnType.INTEGER, isNullable = true)

        val sqlGenerado = adapter.generateAlterColumnSql("usuarios", "edad", columnaModificada)

        val sqlEsperado = """
            ALTER TABLE usuarios ALTER COLUMN edad SET DATA TYPE INT;
            ALTER TABLE usuarios ALTER COLUMN edad DROP NOT NULL;
        """.trimIndent()

        assertEquals(sqlEsperado, sqlGenerado)
    }

    @Test
    fun `debe generar el SQL correcto para agregar una llave primaria`() {
        val pk = PrimaryKey("pk_nueva", listOf("id", "tenant_id"))
        val sqlGenerado = adapter.generateAddPrimaryKeySql("usuarios", pk)

        assertEquals("ALTER TABLE usuarios ADD CONSTRAINT pk_nueva PRIMARY KEY (id, tenant_id);", sqlGenerado)
    }

    @Test
    fun `debe generar el SQL correcto para agregar una llave foranea despues de crear la tabla`() {
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
        assertEquals("ALTER TABLE usuarios DROP CONSTRAINT fk_externa;", sqlGenerado)
    }

    @Test
    fun `debe generar el SQL correcto para crear un indice normal`() {
        val index = Index("idx_apellido", listOf("apellido"))
        val sqlGenerado = adapter.generateCreateIndexSql("usuarios", index)

        assertEquals("CREATE INDEX idx_apellido ON usuarios (apellido);", sqlGenerado)
    }

    @Test
    fun `debe generar el SQL correcto para crear un indice unico compuesto`() {
        val index = Index("idx_email_tenant", listOf("email", "tenant_id"), isUnique = true)
        val sqlGenerado = adapter.generateCreateIndexSql("usuarios", index)

        assertEquals("CREATE UNIQUE INDEX idx_email_tenant ON usuarios (email, tenant_id);", sqlGenerado)
    }

    @Test
    fun `debe generar el SQL correcto para borrar un indice`() {
        val sqlGenerado = adapter.generateDropIndexSql("usuarios", "idx_apellido")
        assertEquals("DROP INDEX idx_apellido;", sqlGenerado)
    }
}