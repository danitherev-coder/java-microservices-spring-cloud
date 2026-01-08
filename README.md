# Practicando Microservicios – Java & Spring Boot

Proyecto personal orientado al **aprendizaje y práctica de arquitectura de microservicios** utilizando **Java 17+, Spring Boot y Spring Cloud**. El objetivo principal es comprender cómo diseñar, configurar y comunicar múltiples servicios backend de forma desacoplada, siguiendo buenas prácticas de desarrollo y principios de escalabilidad.

Este repositorio sirve como **portafolio técnico** y evidencia de experiencia práctica en microservicios para procesos de selección Junior / Trainee.

---

## 🧱 Arquitectura General

El sistema está compuesto por **múltiples microservicios independientes**, cada uno con una responsabilidad clara, integrados mediante componentes de infraestructura comunes:

* **Service Registry (Eureka Server)** para descubrimiento de servicios
* **API Gateway** como punto de entrada único
* **Config Server** para configuración centralizada
* Microservicios de negocio desacoplados por dominio

Arquitectura basada en principios de **separación de responsabilidades**, **bajo acoplamiento** y **escalabilidad horizontal**.

---

## 🧩 Microservicios Incluidos

El proyecto está organizado como un **multi‑módulo Maven**, que incluye los siguientes servicios:

* **config-server**
  Centraliza la configuración de los microservicios utilizando Spring Cloud Config.

* **eureka-server**
  Registro y descubrimiento de servicios con Spring Cloud Netflix Eureka.

* **api-gateway**
  Gateway basado en Spring Cloud Gateway para el enrutamiento de peticiones hacia los microservicios.

* **auth-service**
  Microservicio de autenticación y gestión de usuarios.

* **roles-permissions-service**
  Gestión de roles y permisos.

* **students-service**
  Gestión de estudiantes.

* **courses-service**
  Gestión de cursos.

Cada microservicio:

* Es independiente
* Tiene su propia lógica de negocio
* Se registra dinámicamente en Eureka
* Es accesible a través del API Gateway

---

## ⚙️ Tecnologías Utilizadas

### Backend

* **Java 17+**
* **Spring Boot**
* **Spring Cloud**

  * Eureka Server
  * Spring Cloud Gateway
  * Spring Cloud Config
* **Spring Data JPA**
* **Spring Security (conceptos básicos)**

### Persistencia

* Bases de datos relacionales y no relacionales (según el servicio)

### DevOps / Herramientas

* **Maven** (multi‑module)
* **Docker / Docker Compose** (configuración básica)
* **Git / GitHub**
* **Postman** (pruebas de endpoints)

---

## ▶️ Ejecución del Proyecto (Local)

⚡ **¿Prisa?** Ver [QUICKSTART.md](./QUICKSTART.md) para setup en 5 minutos.

### Requisitos Previos

* **Java 17+** (verificar con `java -version`)
* **Maven 3.8+** (verificar con `mvn -version`)
* **Docker & Docker Compose** (verificar con `docker --version` y `docker-compose --version`)
* **Git** (verificar con `git --version`)

### Paso 1: Clonar y Preparar el Proyecto

```bash
git clone https://github.com/danitherev-coder/java-microservices-spring-cloud.git
cd java-microservices-spring-cloud-main

# Compilar todos los módulos desde la raíz
mvn clean package
```

### Paso 1.5: Configurar Variables de Entorno (Importante)

```bash
# Copiar plantillas de variables de entorno
cp .env.example .env.local
cp .env.docker.example .env.docker

# Editar .env.local con tus credenciales (NO commitar este archivo)
# .env.local es personal para cada desarrollador
```

**Contenido de `.env.local` (ejemplo):**
```bash
MYSQL_ROOT_PASSWORD=tu_contraseña_mysql
POSTGRES_PASSWORD=tu_contraseña_postgres
DB_USERNAME=root
DB_PASSWORD=tu_contraseña_aqui
AUTH_DB_PASSWORD=tu_contraseña_postgres
STUDENTS_DB_PASSWORD=tu_contraseña_mysql
COURSES_DB_PASSWORD=tu_contraseña_postgres
ROLES_DB_PASSWORD=tu_contraseña_mysql
```

⚠️ **IMPORTANTE:** 
- **NO commites** `.env.local` o `.env.docker` (ya están en `.gitignore`)
- Usa `.env.example` solo como referencia
- Cada desarrollador debe tener su propio `.env.local`

### Paso 2: Levantar Infraestructura (Bases de Datos)

```bash
# Desde la raíz del proyecto
# Usar variables del archivo .env.docker
docker-compose --env-file .env.docker up -d
```

Verifica que los contenedores estén corriendo:
```bash
docker-compose ps
```

**Variables configurables en `.env.docker`:**
| Variable | Descripción | Default |
|----------|-------------|---------|
| `MYSQL_ROOT_PASSWORD` | Contraseña root de MySQL | 1234 |
| `MYSQL_STUDENT_DATABASE` | BD para students | microservice-student-db |
| `MYSQL_ROLES_DATABASE` | BD para roles | microservice-roles-db |
| `POSTGRES_PASSWORD` | Contraseña PostgreSQL | 1234 |
| `POSTGRES_AUTH_DATABASE` | BD para auth | microservice-auth-db |
| `POSTGRES_COURSES_DATABASE` | BD para courses | microservice-courses-db |

**Bases de datos disponibles:**
| Servicio | Puerto | Usuario | Variable de Entorno | BD |
|----------|--------|---------|------------|-----|
| MySQL (Students) | 3306 | root | `DB_USERNAME`/`DB_PASSWORD` | `MYSQL_STUDENT_DATABASE` |
| MySQL (Roles) | 3307 | root | `DB_USERNAME`/`DB_PASSWORD` | `MYSQL_ROLES_DATABASE` |
| PostgreSQL (Auth) | 5432 | postgres | `AUTH_DB_USERNAME`/`AUTH_DB_PASSWORD` | `POSTGRES_AUTH_DATABASE` |
| PostgreSQL (Courses) | 5433 | postgres | `COURSES_DB_USERNAME`/`COURSES_DB_PASSWORD` | `POSTGRES_COURSES_DATABASE` |

### Paso 3: Arrancar Servicios (Orden CRÍTICO)

**En IntelliJ IDEA - Usar EnvFile Plugin:**

1. Instala el plugin "EnvFile" (Preferences → Plugins)
2. Para cada servicio, Edit Configurations → Spring Boot:
   - ✓ Enable EnvFile
   - EnvFile: `.env.local`
3. Run cada servicio

**O desde Terminal (sin plugin):**

Exporta variables antes de arrancar:
```bash
# macOS/Linux
export $(cat .env.local | xargs)

# Windows PowerShell
Get-Content .env.local | ForEach-Object {
    if ($_ -notmatch '^#' -and $_ -notmatch '^\s*$') {
        $name, $value = $_.split('=')
        [Environment]::SetEnvironmentVariable($name, $value)
    }
}
```

**Arrancar servicios en orden:**

**Terminal 1 - Config Server** (Puerto 8888):
```bash
cd microservice-config
mvn spring-boot:run
```
✅ Espera a ver: `Tomcat started on port 8888`

**Terminal 2 - Eureka Server** (Puerto 8761):
```bash
cd microservice-eureka
mvn spring-boot:run
```
✅ Espera a ver: `Started MicroserviceEurekaApplication`
Dashboard: http://localhost:8761/

**Terminal 3 - API Gateway** (Puerto 8090):
```bash
cd microservice-gateway
mvn spring-boot:run
```
✅ Espera a ver: `Tomcat started on port 8090`

**Terminal 4+ - Microservicios** (Puertos 8081+):
```bash
# En nuevas terminales, ejecutar en cualquier orden
cd microservices-auth && mvn spring-boot:run
cd microservice.students && mvn spring-boot:run
cd microservice-courses && mvn spring-boot:run
cd microservice-roles-y-permisos && mvn spring-boot:run
```

### Verificación de Startup

✅ **Todos los servicios levantados cuando:**
- Eureka Dashboard muestra servicios registrados: http://localhost:8761/
- Logs no muestran errores de conexión a Config Server
- Puedes hacer request al Gateway: `curl http://localhost:8090/api/v1/students/all`

### Parar Todo
```bash
# Detener contenedores
docker-compose down

# Detener servicios Spring (Ctrl+C en cada terminal)
```

---

## 🔐 Seguridad - Variables de Entorno

### Archivos de Configuración

Este proyecto usa **variables de entorno** para gestionar credenciales de forma segura:

**Archivos a Commitar (Públicos):**
- `.env.example` ✅ Plantilla sin valores reales
- `.env.docker.example` ✅ Plantilla Docker sin valores reales
- Todos los YAML en `configurations/` ✅ Usan variables, no hardcodeadas

**Archivos NO a Commitar (Privados):**
- `.env.local` ❌ Tus credenciales personales (en `.gitignore`)
- `.env.docker` ❌ Credenciales Docker (en `.gitignore`)

### Cómo Funcionan las Variables

En los YAML de configuración (`configurations/*.yml`):
```yaml
spring:
  datasource:
    username: ${STUDENTS_DB_USERNAME:root}  # Lee variable, default "root"
    password: ${STUDENTS_DB_PASSWORD:1234}  # Lee variable, default "1234"
```

Syntax: `${VARIABLE_NAME:default_value}`

### Para Otros Desarrolladores

Cuando clonan el proyecto:
```bash
cp .env.example .env.local
cp .env.docker.example .env.docker
# Editar .env.local con SUS credenciales
```

---

## � Configuración Descentralizada

### Cómo Funciona Config Server

Todos los microservicios obtienen su configuración desde **Config Server** en `http://localhost:8888`. Las configuraciones están en:

```
configurations/
├── microservice-courses.yml
├── microservice-eureka.yml
├── microservice-gateway.yml
├── microservice-students.yml
├── microservice-roles-y-permisos.yml
└── microservices-auth.yml
```

Ejemplo: `microservices-auth/src/main/resources/application.yml`
```yaml
spring:
  application:
    name: microservices-auth  # Este nombre DEBE coincidir con el YAML en /configurations/
  config:
    import: optional:configserver:http://localhost:8888
```

**Nota:** Si editas un YAML en `/configurations/`, **debes reiniciar Config Server** para que los cambios se apliquen.

---

## 🛣️ API Gateway - Enrutamiento

El Gateway actúa como punto de entrada único. Todas las peticiones pasan por puerto 8090:

```yaml
# microservice-gateway/src/main/resources/application.yml
spring.cloud.gateway.routes:
  - id: microservice-students
    uri: lb://microservice-students  # Load-balanced por Eureka
    predicates:
      - Path=/api/v1/students/**
    filters:
      - AuthenticationFilter

  - id: microservice-courses
    uri: lb://microservice-courses
    predicates:
      - Path=/api/v1/courses/**
    filters:
      - AuthenticationFilter
```

**Ejemplos de requests:**
```bash
# Request externo (a través del Gateway)
GET http://localhost:8090/api/v1/students/all

# Se traduce internamente a:
GET http://microservice-students:8081/api/v1/students/all
```

---

## 📂 Estructura del Proyecto

```
java-microservices-spring-cloud-main/
├── pom.xml                          # Parent Maven (define versiones, módulos)
├── docker-compose.yml               # Infraestructura: MySQL (×2), PostgreSQL
├── README.md                        # Este archivo
│
├── configurations/                  # YAML centralizados (gestiona Config Server)
│   ├── microservice-courses.yml
│   ├── microservice-eureka.yml
│   ├── microservice-gateway.yml
│   ├── microservice-students.yml
│   ├── microservice-roles-y-permisos.yml
│   └── microservices-auth.yml
│
├── microservice-config/             # Config Server (puerto 8888)
│   ├── src/main/java/.../MicroserviceConfigApplication.java
│   └── src/main/resources/application.yml
│
├── microservice-eureka/             # Service Registry (puerto 8761)
│   ├── src/main/java/.../MicroserviceEurekaApplication.java
│   └── src/main/resources/application.yml
│
├── microservice-gateway/            # API Gateway (puerto 8090)
│   ├── src/main/java/com/microservice/gateway/
│   │   ├── MicroserviceGatewayApplication.java
│   │   ├── config/                  # Configuración de rutas
│   │   ├── filter/                  # Filtros custom (AuthenticationFilter)
│   │   └── exception/               # Manejo de errores
│   └── src/main/resources/application.yml
│
├── microservices-auth/              # Auth Service (PostgreSQL)
│   ├── src/main/java/com/microservice/auth/
│   │   ├── MicroservicesAuthApplication.java
│   │   ├── controller/              # REST endpoints
│   │   ├── service/                 # Lógica de negocio
│   │   ├── repository/              # Spring Data JPA
│   │   ├── entity/                  # Modelos JPA
│   │   ├── dto/                     # Data Transfer Objects
│   │   └── exceptions/              # Excepciones custom
│   └── src/main/resources/application.yml
│
├── microservice.students/           # Students Service (MySQL 3306)
│   └── [Estructura idéntica a auth]
│
├── microservice-courses/            # Courses Service (MySQL 3306)
│   ├── client/                      # Clientes HTTP para llamadas inter-servicio
│   └── [Estructura idéntica a auth]
│
└── microservice-roles-y-permisos/   # Roles Service (MySQL 3307)
    └── [Estructura idéntica a auth]
```

---

## 🔧 URLs y Dashboards

**Desarrollo Local:**
| Componente | URL | Descripción |
|-----------|-----|-------------|
| API Gateway | http://localhost:8090 | Punto de entrada (requests aquí) |
| Eureka Dashboard | http://localhost:8761/ | Ver servicios registrados |
| Config Server | http://localhost:8888 | Servidor de configuración |
| Auth Service | http://localhost:8081 | (Direct) |
| Students Service | http://localhost:8082 | (Direct) |
| Courses Service | http://localhost:8083 | (Direct) |

---

## 🐛 Troubleshooting Común

### ❌ "Config Server not reachable"
```
Solución: Asegúrate que Config Server está levantado ANTES que otros servicios.
Verifica: mvn spring-boot:run en microservice-config (debería ver puerto 8888)
```

### ❌ "Eureka says instance is DOWN"
```
Solución: Espera 30 segundos a que Eureka reconozca la instancia como UP.
Si persiste: Revisa logs para errores de conexión.
```

### ❌ "No hay conexión a la BD"
```
Solución: 
1. Verifica docker-compose: docker-compose ps
2. Revisa puertos: docker port mysql-student-db
3. Test conexión: mysql -h 127.0.0.1 -P 3306 -u root -p1234
```

### ❌ "Puerto ya en uso"
```
Solución: Mata el proceso:
  macOS/Linux: lsof -i :8090 | grep LISTEN | awk '{print $2}' | xargs kill -9
  Windows: netstat -ano | findstr :8090
```

---

## 📚 Diferenciación Naming

⚠️ **Importante:** Nota los naming inconsistentes (herencia del proyecto):
- `microservice-gateway` (guion) ✅ Preferido
- `microservice.students` (punto) ⚠️ Evitar en futuros módulos
- `microservice-roles-y-permisos` (guion + palabras) ✅

Para **nuevos módulos**, usar: `microservice-{nombre}` (kebab-case)

---

## 🔍 Objetivos del Proyecto
* Practicar **Spring Cloud** y sus componentes principales
* Aplicar buenas prácticas de **Clean Code y organización de proyectos**
* Entender la comunicación entre servicios
* Simular escenarios comunes de backend empresarial

---

## 📌 Buenas Prácticas Aplicadas

* Separación de responsabilidades por microservicio
* Configuración centralizada (Config Server)
* Registro y descubrimiento dinámico de servicios (Eureka)
* Uso de API Gateway como punto de entrada único
* Organización clara del código (controller → service → repository)
* Uso de Lombok para reducir boilerplate
* Spring Data JPA para persistencia
* Docker Compose para infraestructura reproducible

---

## 🚀 Próximos Pasos

### Para Entender Mejor el Proyecto
1. Lee [`.github/copilot-instructions.md`](./.github/copilot-instructions.md) para patrones internos
2. Explora `microservice-gateway/src/main/java/com/microservice/gateway/filter/` para ver filtros custom
3. Revisa `microservice-courses/src/main/java/com/microservice/courses/client/` para ejemplos de inter-servicio communication

### Mejoras Futuras
* Implementar **pruebas unitarias y de integración** (JUnit, Spring Test)
* Agregar **CI/CD básico con GitHub Actions**
* Incorporar **seguridad avanzada con OAuth2 Client**
* Implementar **Keycloak** para SSO
* Documentar endpoints con **Swagger/OpenAPI**
* Implementar **circuit breakers** (Resilience4j)
* Desplegar en entorno cloud (AWS/Azure)

---

## 👤 Autor

**Carlos Daniel Saavedra Chu**
Backend Java Developer
📍 Perú
🔗 LinkedIn: [https://linkedin.com/in/danitherev](https://linkedin.com/in/danitherev)
🐙 GitHub: [https://github.com/danitherev-coder](https://github.com/danitherev-coder)

---

Este proyecto forma parte de mi proceso de formación continua como desarrollador backend Java y está orientado a oportunidades Junior / Bootcamp / Trainee en empresas de tecnología.
