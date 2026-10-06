# SpeedFast

**Asignatura:** Desarrollo Orientado a Objetos II  
**Evaluación:** Sumativa 3 - Semana 8  
**Estudiante:** Mariana Benavides

## Tecnologías

- Java 17
- Swing
- MySQL
- JDBC
- Maven

## Funcionalidades

- Registrar, consultar, modificar y eliminar repartidores.
- Registrar, consultar, modificar y eliminar pedidos.
- Registrar, consultar, modificar y eliminar entregas.
- Filtrar pedidos por tipo y estado.
- Filtrar entregas por pedido y repartidor.
- Validar campos y mostrar mensajes de confirmación.
- Guardar los datos en MySQL.

## Ejecución

1. Abrir el proyecto en IntelliJ IDEA y cargar las dependencias de Maven.
2. Ejecutar el archivo `sql/01_schema.sql` en MySQL.
3. Copiar `src/main/resources/db.properties.example` a la raíz del proyecto con el nombre `db.properties`.
4. Configurar el usuario y la contraseña de MySQL.
5. Ejecutar `Main.java`.

## Estructura

- `modelo`: entidades del sistema.
- `dao`: operaciones con la base de datos.
- `config`: conexión a MySQL.
- `vista`: interfaz gráfica.
- `sql`: script de creación de la base de datos.