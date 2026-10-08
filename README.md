# Sistema de Fidelización de Clientes (MVP)

Aplicación de escritorio desarrollada en **Java SE (Swing)** y persistencia en **MariaDB/MySQL**, diseñada bajo el Proceso Unificado de Desarrollo (PUD) para la gestión integral de programas de lealtad en comercios minoristas.

---

## 🏗️ Arquitectura del Software

El sistema implementa una **arquitectura en capas** organizada por paquetes lógicos para garantizar alta cohesión y bajo acoplamiento:
*   `vista/`: Capa de presentación (Interfaces gráficas construidas en Java Swing y menús de navegación).
*   `control/`: Capa de aplicación y lógica de negocio (Gestores de clientes, compras y beneficios).
*   `modelo/`: Capa de dominio (Clases de entidad con encapsulamiento estricto).
*   `dao/`: Capa de acceso a datos (Patrón DAO, manejo de conexiones JDBC y sentencias SQL).

### Estrategia de Despliegue (Fat Client)
Siguiendo las decisiones del Prototipo Operacional (MVP), el sistema se despliega físicamente como un **Cliente Pesado (2-Tier)**. La interfaz de usuario, la lógica de negocio y las consultas DAO corren en el nodo cliente, comunicándose directamente con el servidor de base de datos MariaDB mediante el estándar **JDBC** (puerto `3306`).

---

## 📂 Estructura del Repositorio

```text
/
├── db/                 # Scripts SQL (DDL, DML y consultas de prueba)
├── app/                # Proyecto principal en Java (Maven / Gradle)
│   ├── src/
│   │   └── main/java/com/gpascarelli/fidelizacion/
│   │       ├── modelo/
│   │       ├── dao/
│   │       ├── control/
│   │       └── vista/
│   └── pom.xml         # Dependencias (incluye mysql-connector-j)
└── README.md
```

## Configuración de la conexión a la base de datos

Antes de ejecutar la aplicación, es necesario configurar las credenciales de MySQL en un archivo `.env`. ubicado en `app/sistemafidelizacion/src/main/resources`, al mismo nivel que `pom.xml`:

```text
app/sistemafidelizacion/src/main/resources
└── .env
```

El archivo debe contener estas variables:

```dotenv
DB_HOST=jdbc:mysql://localhost:3306/SistemaFidelizacion
DB_USER=su_usuario
DB_PASS=su_contraseña
```

`DB_HOST` debe ser la URL JDBC completa.
`tu_usuario` y `tu_contraseña` debe ser reemplazado por las credenciales de MySQL. 
La base de datos debe existir; puedes crearla y preparar sus tablas ejecutando `db/script-creacion.sql` desde la raíz del repositorio.
