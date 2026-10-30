# Registro Telefónico

Aplicación de escritorio en Java Swing para registrar personas (nombre, edad y teléfono) en MySQL.
El número telefónico se valida con un analizador léxico y sintáctico propio que exige el formato:

```
(123) 456-7890
```

## Requisitos

- Java 17 o superior
- Maven 3.9+
- MySQL o MariaDB

## Base de datos

```bash
mysql -u root -p < schema.sql
```

La conexión usa estas variables de entorno (entre paréntesis, el valor por defecto):

| Variable      | Por defecto                                                        |
|---------------|--------------------------------------------------------------------|
| `DB_URL`      | `jdbc:mysql://localhost:3306/registrotelefonico?serverTimezone=UTC` |
| `DB_USER`     | `root`                                                             |
| `DB_PASSWORD` | *(vacía)*                                                          |

## Compilar y ejecutar

```bash
mvn test                                       # pruebas del lexer y parser
mvn package                                    # genera el jar con el driver incluido
java -jar target/registro-telefonico-1.0.0.jar
```

Para comprobar solo la conexión a la base de datos:

```bash
java -cp target/registro-telefonico-1.0.0.jar backend.TestDatabaseConnection
```

## Estructura

```
src/main/java/backend/   Lexer, Parser, Token, Persona, Database
src/main/java/vista/     Ventana principal (Swing)
src/test/java/backend/   Pruebas JUnit 5
schema.sql               Creación de la base de datos y la tabla
```
