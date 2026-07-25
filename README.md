# Proyecto Base Implementando Clean Architecture

## Antes de Iniciar

Empezaremos por explicar los diferentes componentes del proyectos y partiremos de los componentes externos, continuando con los componentes core de negocio (dominio) y por último el inicio y configuración de la aplicación.

Lee el artículo [Clean Architecture — Aislando los detalles](https://medium.com/bancolombia-tech/clean-architecture-aislando-los-detalles-4f9530f35d7a)

# Arquitectura

![Clean Architecture](https://miro.medium.com/max/1400/1*ZdlHz8B0-qu9Y-QO3AXR_w.png)

## Domain

Es el módulo más interno de la arquitectura, pertenece a la capa del dominio y encapsula la lógica y reglas del negocio mediante modelos y entidades del dominio.

## Usecases

Este módulo gradle perteneciente a la capa del dominio, implementa los casos de uso del sistema, define lógica de aplicación y reacciona a las invocaciones desde el módulo de entry points, orquestando los flujos hacia el módulo de entities.

## Infrastructure

### Helpers

En el apartado de helpers tendremos utilidades generales para los Driven Adapters y Entry Points.

Estas utilidades no están arraigadas a objetos concretos, se realiza el uso de generics para modelar comportamientos
genéricos de los diferentes objetos de persistencia que puedan existir, este tipo de implementaciones se realizan
basadas en el patrón de diseño [Unit of Work y Repository](https://medium.com/@krzychukosobudzki/repository-design-pattern-bc490b256006)

Estas clases no puede existir solas y debe heredarse su compartimiento en los **Driven Adapters**

### Driven Adapters

Los driven adapter representan implementaciones externas a nuestro sistema, como lo son conexiones a servicios rest,
soap, bases de datos, lectura de archivos planos, y en concreto cualquier origen y fuente de datos con la que debamos
interactuar.

### Entry Points

Los entry points representan los puntos de entrada de la aplicación o el inicio de los flujos de negocio.

## Application

Este módulo es el más externo de la arquitectura, es el encargado de ensamblar los distintos módulos, resolver las dependencias y crear los beans de los casos de use (UseCases) de forma automática, inyectando en éstos instancias concretas de las dependencias declaradas. Además inicia la aplicación (es el único módulo del proyecto donde encontraremos la función “public static void main(String[] args)”.

**Los beans de los casos de uso se disponibilizan automaticamente gracias a un '@ComponentScan' ubicado en esta capa.**

## Configuración por entorno

La aplicación no contiene credenciales por defecto. Antes de iniciarla se deben definir como mínimo:

```text
DB_HOST
DB_PORT
DB_NAME
DB_SCHEMA
DB_USERNAME
DB_PASSWORD
DB_SSL
JWT_SECRET
JWT_ISSUER
CORS_ALLOWED_ORIGINS
```

`JWT_SECRET` debe ser una clave aleatoria de al menos 32 bytes. Para despliegues con escalado automático se recomienda
mantener `DB_POOL_INITIAL_SIZE=0` y un `DB_POOL_MAX_SIZE` bajo. La base de datos debe ubicarse en una región cercana a
la aplicación.

Las variables opcionales de importación son `IMPORT_MAX_FILE_SIZE_BYTES` e `IMPORT_MAX_ROWS`. El archivo XLSX se
procesa en memoria con límites explícitos, por lo que estos valores deben ajustarse a la memoria del runtime.
La exportación de posibles votantes se limita con `EXPORT_MAX_ROWS` (10.000 por defecto).

## Endpoints de sesión, actividad y exportación

- `GET /api/v1/auth/me`: consulta el usuario autenticado, roles, módulos y permisos vigentes a partir del `userId`
  verificado del JWT. Un `LIDER` recibe únicamente Posibles votantes; un `ADMINISTRADOR` recibe todos los módulos.
- `GET /api/v1/activity/recent?limit=10`: devuelve entre 1 y 50 movimientos recientes de usuarios y posibles
  votantes. El evento se deriva de sus fechas de creación y actualización; no reemplaza un historial de auditoría
  inmutable.
- `GET /api/v1/potential-voter/export`: descarga un XLSX y acepta los filtros opcionales `identification`,
  `pollingPlaceId`, `votingZoneId` y `assignedLeaderId`.

El listado y la exportación de posibles votantes se limitan automáticamente al usuario autenticado cuando no tiene
el rol activo `ADMINISTRADOR`. El filtro `assignedLeaderId` enviado por un líder se reemplaza por el `userId` del JWT.
La misma validación de propiedad se aplica al editar un posible votante.

Al crear un líder mediante `POST /api/v1/user`, la aplicación asigna el rol activo `LIDER` dentro de la misma
transacción. Para una base existente debe ejecutarse
`deployment/migrations/20260725_configure_role_modules.sql`.

## Limitación de solicitudes

El entry point aplica rate limiting a las rutas bajo `/api/v1`: 100 solicitudes por minuto de forma general,
5 solicitudes por minuto para el login y 2 solicitudes por minuto para la importación masiva. Los límites se pueden
ajustar mediante `RATE_LIMIT_DEFAULT_LIMIT_FOR_PERIOD`, `RATE_LIMIT_DEFAULT_LIMIT_REFRESH_PERIOD`,
`RATE_LIMIT_LOGIN_LIMIT_FOR_PERIOD`, `RATE_LIMIT_LOGIN_LIMIT_REFRESH_PERIOD`,
`RATE_LIMIT_IMPORT_LIMIT_FOR_PERIOD` y `RATE_LIMIT_IMPORT_LIMIT_REFRESH_PERIOD`. También están disponibles
`RATE_LIMIT_ENABLED`, `RATE_LIMIT_MAX_BUCKETS` y
`RATE_LIMIT_EXPIRE_AFTER_ACCESS`.

Los usuarios autenticados se identifican con el `userId` verificado del JWT; las solicitudes públicas se identifican
con la dirección remota de la conexión. No se confía en encabezados reenviados enviados directamente por el cliente.

## Base de datos

`deployment/local_environment/create_BD.sql` recrea completamente la base y solo debe utilizarse en desarrollo local.
