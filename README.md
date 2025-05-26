# Inventory - API de Gestión

Este proyecto es una API RESTful desarrollada con Spring Boot para la gestión de un inventario, permitiendo administrar usuarios y productos.

## Tecnologías Utilizadas

*   Java 17
*   Spring Boot 3.5.0
    *   Spring Web
    *   Spring Data JPA
*   Maven (para la gestión de dependencias y construcción del proyecto)
*   PostgreSQL (como sistema de gestión de base de datos)

## Prerrequisitos

*   JDK 17 o superior instalado.
*   Maven instalado.
*   Una instancia de PostgreSQL en ejecución y accesible. Deberás configurar los detalles de la conexión en `src/main/resources/application.properties` (o el archivo de configuración correspondiente).

## Cómo Construir y Ejecutar

1.  **Clonar el repositorio (si aplica):**
    ```bash
    git clone https://github.com/Tulio-Rangel/nexus-inventory-backend.git
    cd Inventory
    ```

2.  **Configurar la base de datos:**
    Asegúrate de que tu instancia de PostgreSQL esté corriendo y actualiza las propiedades de conexión de la base de datos en `src/main/resources/application.properties`:
    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/nombre_tu_bd
    spring.datasource.username=tu_usuario_bd
    spring.datasource.password=tu_contraseña_bd
    spring.jpa.hibernate.ddl-auto=update # O 'create' para la primera ejecución si quieres que Hibernate cree las tablas
    ```

3.  **Construir el proyecto:**
    Utiliza Maven para compilar y empaquetar la aplicación:
    ```bash
    ./mvnw clean package
    ```
    (En Windows, usa `mvnw.cmd clean package`)
    
    Este comando también ejecutará todas las pruebas unitarias. Si solo quieres compilar sin ejecutar las pruebas, puedes usar:
    ```bash
    ./mvnw clean package -DskipTests
    ```

4.  **Ejecutar la aplicación:**
    Once construida, puedes ejecutar el archivo JAR generado:
    ```bash
    java -jar target/Inventory-0.0.1-SNAPSHOT.jar
    ```
    La API estará disponible por defecto en `http://localhost:8080`.

## Endpoints de la API

La URL base para todos los endpoints es `http://localhost:8080/api`.

### Probar la API con Postman
En este repositorio se incluye una colección de Postman (`Nexos.postman_collection.json`) que puedes importar en tu aplicación Postman para probar fácilmente todos los endpoints de la API descritos anteriormente. Asegúrate de que la aplicación esté corriendo localmente en `http://localhost:8080` antes de enviar las solicitudes desde Postman.

### Gestión de Usuarios

#### `POST /users`
Crea un nuevo usuario.
*   **Request Body:**
    ```json
    {
        "name": "Nombre Completo del Usuario",
        "age": 30,
        "position": "Cargo del Usuario",
        "hireDate": "YYYY-MM-DD" // Ejemplo: "2023-01-15"
    }
    ```
*   **Validaciones:**
    *   La fecha de contratación (`hireDate`) no puede ser una fecha futura.

#### `GET /users`
Obtiene una lista de todos los usuarios.

#### `GET /users/{id}`
Obtiene un usuario específico por su ID.
*   **Path Variable:** `id` (long) - ID del usuario.

#### `PUT /users/{id}`
Actualiza un usuario existente.
*   **Path Variable:** `id` (long) - ID del usuario a actualizar.
*   **Request Body:** Similar al de creación.

#### `DELETE /users/{id}`
Elimina un usuario.
*   **Path Variable:** `id` (long) - ID del usuario a eliminar.
*   **Restricciones:** No se puede eliminar un usuario si tiene productos registrados a su nombre.

### Gestión de Productos

#### `POST /products`
Crea un nuevo producto.
*   **Request Body:**
    ```json
    {
        "productName": "Nombre del Producto",
        "quantity": 100,
        "entryDate": "YYYY-MM-DD", // Ejemplo: "2024-01-20"
        "registeredByUserId": 1 // ID del usuario que registra el producto
    }
    ```
*   **Validaciones:**
    *   `productName` no puede estar vacío.
    *   `quantity` debe ser un entero positivo.
    *   `registeredByUserId` debe corresponder a un usuario existente.
    *   No se permite registrar un producto con un `productName` que ya existe.

#### `GET /products`
Obtiene una lista de productos. Permite filtrar por los siguientes query parameters:
*   `productName` (String): Filtra por el nombre exacto del producto.
*   `userId` (Long): Filtra por el ID del usuario que registró el producto (`registeredByUserId`).
*   `entryDate` (String, formato `YYYY-MM-DD`): Filtra por la fecha de entrada del producto.
    *   Se pueden combinar estos filtros.
*   **Ejemplos de URLs:**
    *   `GET /api/products` (Obtiene todos los productos)
    *   `GET /api/products?productName=Llanta%2016`
    *   `GET /api/products?userId=1`
    *   `GET /api/products?entryDate=2024-05-20`

#### `PUT /products/{id}`
Actualiza un producto existente.
*   **Path Variable:** `id` (long) - ID del producto a actualizar.
*   **Request Body:**
    ```json
    {
        "productName": "Nuevo Nombre del Producto",
        "quantity": 150,
        "entryDate": "YYYY-MM-DD",
        "lastModifiedByUserId": 2 // ID del usuario que modifica el producto
    }
    ```

#### `DELETE /products/{id}`
Elimina un producto.
*   **Path Variable:** `id` (long) - ID del producto a eliminar.
*   **Query Parameter Requerido:** `requestingUserId` (long) - ID del usuario que solicita la eliminación.
        *   **Restricciones:** Solo el usuario que originalmente registró el producto (`registeredByUserId`) puede eliminarlo. El valor de `requestingUserId` se compara con el `registeredByUserId` del producto.
        *   **Ejemplo de URL:** `DELETE /api/products/5?requestingUserId=1`

## Pruebas Unitarias

El proyecto cuenta con un conjunto completo de pruebas unitarias y de integración que garantizan la calidad y robustez del código.

### Tipos de Pruebas Implementadas

1. **Pruebas de Servicios**
   * `UserServiceTest`: Verifica todas las operaciones CRUD y validaciones del servicio de usuarios.
   * `ProductServiceTest`: Verifica todas las operaciones CRUD, búsquedas y validaciones del servicio de productos.

2. **Pruebas de Controladores**
   * `UserControllerTest`: Verifica los endpoints REST relacionados con los usuarios.
   * `ProductControllerTest`: Verifica los endpoints REST relacionados con los productos.

3. **Prueba de Integración Básica**
   * `InventoryApplicationTests`: Verifica que el contexto de Spring se carga correctamente y que los componentes se inyectan adecuadamente.

### Cómo Ejecutar las Pruebas

Para ejecutar todas las pruebas unitarias del proyecto:

```bash
./mvnw test
```

Para ejecutar una clase de prueba específica:

```bash
./mvnw test -Dtest=UserServiceTest
```

Para ejecutar un método de prueba específico:

```bash
./mvnw test -Dtest=UserServiceTest#createUser_WithValidData_ShouldReturnCreatedUser
```

Las pruebas también se ejecutan automáticamente durante la fase de construcción del proyecto (`./mvnw package`).

