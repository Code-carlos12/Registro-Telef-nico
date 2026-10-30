# Registro Telefónico

Aplicación de escritorio en Java Swing para registrar personas (nombre, edad y teléfono) en una base de datos MySQL.
El número telefónico se valida con un analizador léxico y sintáctico propio que exige el formato:

```
(123) 456-7890
```

---

## 1. Requisitos

Necesitas tener instalado:

| Programa                | Versión mínima | Para qué sirve                       |
|-------------------------|----------------|--------------------------------------|
| Git                     | cualquiera     | Clonar el repositorio                |
| Java JDK                | 17             | Compilar y ejecutar el programa      |
| Maven                   | 3.9            | Descargar dependencias y compilar    |
| MySQL o MariaDB         | 8.0 / 10.6     | Guardar los registros                |

Instalación según tu sistema:

- **Windows:** instala [Git](https://git-scm.com/download/win), el [JDK 17 o superior](https://adoptium.net/), [Maven](https://maven.apache.org/download.cgi) (agrega su carpeta `bin` al `PATH`) y [MySQL Community Server](https://dev.mysql.com/downloads/mysql/).
- **Ubuntu / Debian:**
  ```bash
  sudo apt install git openjdk-17-jdk maven mysql-server
  ```
- **Arch Linux:**
  ```bash
  sudo pacman -S git jdk17-openjdk maven mariadb
  sudo mariadb-install-db --user=mysql --basedir=/usr --datadir=/var/lib/mysql
  ```
- **macOS (Homebrew):**
  ```bash
  brew install git openjdk@17 maven mysql
  ```

Comprueba que todo quedó instalado:

```bash
git --version
java -version
mvn -version
mysql --version
```

---

## 2. Clonar el proyecto

```bash
git clone https://github.com/Code-carlos12/Registro-Telef-nico.git
cd Registro-Telef-nico
```

---

## 3. Preparar la base de datos

### 3.1 Iniciar el servidor de base de datos

- **Windows:** MySQL se inicia como servicio automáticamente después de instalarlo.
- **Linux:**
  ```bash
  sudo systemctl start mysqld      # o "mariadb" según tu distribución
  ```
- **macOS:**
  ```bash
  brew services start mysql
  ```

### 3.2 Crear la base de datos y la tabla

El programa necesita **una base de datos** llamada `registrotelefonico` con **una sola tabla** llamada `persona`.

| Columna    | Tipo           | Descripción                                         |
|------------|----------------|-----------------------------------------------------|
| `id`       | `INT`          | Identificador, se genera solo (`AUTO_INCREMENT`)    |
| `nombre`   | `VARCHAR(100)` | Nombre de la persona                                |
| `edad`     | `INT`          | Edad (el programa acepta de 0 a 150)                |
| `telefono` | `VARCHAR(20)`  | Teléfono en formato `(123) 456-7890`                |

El archivo [`schema.sql`](schema.sql) crea todo automáticamente. Desde la carpeta del proyecto ejecuta:

```bash
mysql -u root -p < schema.sql
```

(Escribe la contraseña de `root` cuando la pida; si no tiene contraseña, solo presiona Enter.)

Si prefieres hacerlo a mano, entra con `mysql -u root -p` y pega:

```sql
CREATE DATABASE IF NOT EXISTS registrotelefonico;
USE registrotelefonico;

CREATE TABLE IF NOT EXISTS persona (
    id       INT AUTO_INCREMENT PRIMARY KEY,
    nombre   VARCHAR(100) NOT NULL,
    edad     INT          NOT NULL,
    telefono VARCHAR(20)  NOT NULL
);
```

Verifica que la tabla exista:

```sql
USE registrotelefonico;
DESCRIBE persona;
```

### 3.3 (Opcional) Crear un usuario propio

Es más seguro no usar `root`. Dentro de `mysql`:

```sql
CREATE USER 'registro'@'localhost' IDENTIFIED BY 'mi_contraseña';
GRANT SELECT, INSERT ON registrotelefonico.* TO 'registro'@'localhost';
```

---

## 4. Configurar la conexión

Por defecto el programa se conecta así:

| Variable      | Valor por defecto                                                   |
|---------------|---------------------------------------------------------------------|
| `DB_URL`      | `jdbc:mysql://localhost:3306/registrotelefonico?serverTimezone=UTC` |
| `DB_USER`     | `root`                                                              |
| `DB_PASSWORD` | *(vacía)*                                                           |

Si tu usuario o contraseña son distintos, define las variables de entorno **antes** de ejecutar el programa:

- **Linux / macOS:**
  ```bash
  export DB_USER=registro
  export DB_PASSWORD=mi_contraseña
  ```
- **Windows (PowerShell):**
  ```powershell
  $env:DB_USER="registro"
  $env:DB_PASSWORD="mi_contraseña"
  ```
- **Windows (CMD):**
  ```cmd
  set DB_USER=registro
  set DB_PASSWORD=mi_contraseña
  ```

---

## 5. Compilar

Desde la carpeta del proyecto:

```bash
mvn package
```

La primera vez Maven descarga las dependencias (driver de MySQL y JUnit), así que necesita internet.
Al terminar se crea el archivo `target/registro-telefonico-1.0.0.jar`, que ya incluye el driver de MySQL.

Para ejecutar solo las pruebas:

```bash
mvn test
```

---

## 6. Ejecutar

```bash
java -jar target/registro-telefonico-1.0.0.jar
```

Para comprobar únicamente que la conexión a la base de datos funciona:

```bash
java -cp target/registro-telefonico-1.0.0.jar backend.TestDatabaseConnection
```

Debe mostrar `Conexión exitosa!`.

---

## 7. Cómo usar el programa

Al abrirlo verás una ventana con tres campos y dos botones:

1. **Nombre:** escribe el nombre de la persona.
2. **Edad:** escribe un número entre 0 y 150.
3. **Número Telefónico:** escríbelo exactamente con este formato:
   ```
   (555) 123-4567
   ```
   - 3 dígitos entre paréntesis
   - un espacio
   - 3 dígitos, un guion y 4 dígitos
4. Presiona **Guardar**. En la parte inferior aparece `Datos guardados exitosamente` y los campos se vacían.
5. Presiona **Mostrar Registros** para abrir una ventana con todas las personas guardadas.

### Mensajes de error

| Mensaje                                               | Causa y solución                                                        |
|-------------------------------------------------------|-------------------------------------------------------------------------|
| `Todos los campos son obligatorios.`                  | Hay un campo vacío. Llénalos todos.                                     |
| `La edad debe ser un número.`                         | La edad tiene letras o símbolos.                                        |
| `La edad debe estar entre 0 y 150.`                   | La edad está fuera de rango.                                            |
| `Formato de número telefónico incorrecto ...`         | El teléfono no sigue el formato `(123) 456-7890`.                       |
| `Error al guardar en la base de datos: ...`           | Problema con MySQL; revisa la sección de problemas comunes.             |

Ejemplos de teléfonos:

| Teléfono           | ¿Válido? | Motivo                       |
|--------------------|----------|------------------------------|
| `(555) 123-4567`   | ✅        |                              |
| `555 123-4567`     | ❌        | Faltan los paréntesis        |
| `(555)123-4567`    | ❌        | Falta el espacio             |
| `(555) 1234567`    | ❌        | Falta el guion               |
| `(55) 123-4567`    | ❌        | Faltan dígitos               |

---

## 8. Problemas comunes

| Error                                                   | Solución                                                                                       |
|---------------------------------------------------------|------------------------------------------------------------------------------------------------|
| `Communications link failure`                           | El servidor MySQL no está encendido. Inícialo (paso 3.1).                                      |
| `Access denied for user 'root'@'localhost'`             | Usuario o contraseña incorrectos. Configura `DB_USER` y `DB_PASSWORD` (paso 4).                |
| `Unknown database 'registrotelefonico'`                 | No se creó la base de datos. Ejecuta `schema.sql` (paso 3.2).                                  |
| `Table 'registrotelefonico.persona' doesn't exist`      | No se creó la tabla. Ejecuta `schema.sql` (paso 3.2).                                          |
| `mvn: command not found` / `'mvn' no se reconoce`       | Maven no está instalado o no está en el `PATH` (paso 1).                                       |
| `UnsupportedClassVersionError`                          | Tu Java es anterior a la 17. Instala el JDK 17 o superior.                                     |

---

## Estructura del proyecto

```
src/main/java/backend/   Lexer, Parser, Token, Persona, Database
src/main/java/vista/     Ventana principal (Swing)
src/test/java/backend/   Pruebas JUnit 5
schema.sql               Creación de la base de datos y la tabla
pom.xml                  Configuración de Maven
```
