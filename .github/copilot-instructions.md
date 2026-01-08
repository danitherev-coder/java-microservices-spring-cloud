# Guía de Copilot - Java Microservicios Spring Cloud

## Arquitectura General

Esta es una arquitectura de **microservicios multi-módulo Maven** basada en Spring Boot 3.1+ y Spring Cloud 2023.0.0, orientada al aprendizaje de escalabilidad y desacoplamiento. Sigue el patrón de **Service Discovery + Central Configuration + API Gateway**.

### Stack Tecnológico
- **Java 17+** | **Spring Boot 3.1.12** | **Spring Cloud 2023.0.0**
- **Maven 3.8+** (multi-módulo con parent pom.xml)
- **Lombok** para reducción de boilerplate
- **Spring Data JPA** para persistencia
- **Docker Compose** para infraestructura local

### Componentes Infraestructura
1. **Config Server** (puerto 8888): Centraliza configuración YAML para todos los servicios mediante Spring Cloud Config
2. **Eureka Server** (puerto 8761): Registry dinámico de servicios; cada microservicio se auto-registra en startup
3. **API Gateway** (puerto 8090): Enrutador basado en Spring Cloud Gateway con predicados de path y filtros custom (ej: `AuthenticationFilter`)

### Microservicios de Negocio
- **auth-service** (PostgreSQL): Autenticación y gestión de usuarios
- **students-service** (MySQL 3306): Gestión de estudiantes
- **courses-service** (MySQL + cliente HTTP): Gestión de cursos
- **roles-permissions-service** (MySQL 3307): Control de acceso

**Convención**: Cada servicio tiene estructura idéntica: `controller/` → `service/` → `repository/` (Spring Data JPA) + `entity/`, `dto/`, `exceptions/`.

---

## Flujos de Trabajo Críticos

### Orden de Arranque Local
```bash
cd microservice-config && mvn spring-boot:run   # Puerto 8888 (requiere git.properties si usa profile git)
cd microservice-eureka && mvn spring-boot:run   # Puerto 8761
cd microservice-gateway && mvn spring-boot:run  # Puerto 8090
cd microservices-auth && mvn spring-boot:run    # Registra en Eureka automáticamente
cd microservice.students && mvn spring-boot:run
cd microservice-courses && mvn spring-boot:run
```

Todos los servicios requieren que **Config Server + Eureka** estén activos primero (ver `spring.config.import: configserver:http://localhost:8888` en cada `application.yml`).

### Docker Compose
```bash
docker-compose up -d  # Levanta MySQL (×2), PostgreSQL, y sus volúmenes
```
Bases de datos predefinidas:
- MySQL student (3306): `microservice-student-db`
- MySQL roles (3307): `microservice-roles-db`
- PostgreSQL (5432): `microservice-auth-db`

### Build & Packaging
```bash
# Parent build (compila todos los módulos)
mvn clean package

# Build específico
cd microservice-students && mvn clean install
```

---

## Patrones y Convenciones Proyecto

### 1. Configuración Descentralizada
- **Config Server Profile**: `native` (local) u otros perfiles en `/configurations/` (Eureka, Gateway, Auth configs)
- Cada microservicio importa configuración desde Config Server vía `spring.config.import`
- Las YAML locales (`application.yml`) solo declaran `spring.application.name` y puerto

### 2. Enrutamiento & Filtros (Gateway)
```yaml
# En microservice-gateway/application.yml
spring.cloud.gateway.routes:
  - id: microservice-students
    uri: lb://microservice-students  # Load-balanced por Eureka
    predicates:
      - Path=/api/v1/students/**
    filters:
      - AuthenticationFilter  # Custom filter
```

Ver `microservice-gateway/src/.../filter/` para implementación del filtro. Los predicados actúan como reglas de routing.

### 3. Estructura Interna de Servicios
```
microservice-students/
├── controller/         # REST endpoints (@RestController)
├── service/            # Lógica de negocio (interfaz + impl)
├── repository/         # Spring Data JPA (extends JpaRepository)
├── entity/             # @Entity JPA
├── dto/                # Transfer objects (si existen)
└── exceptions/         # Excepciones custom
```

### 4. Base de Datos
- **JPA**: `spring.jpa.hibernate.ddl-auto` se configura en Config Server (usar `update` o `create-drop` en dev)
- **Lombok**: Decoradores `@Entity` + `@Data` + `@NoArgsConstructor` + `@AllArgsConstructor` redacen entity boilerplate
- **Naming**: Convención JPA estándar (tabla = nombre entidad en snake_case)

---

## Puntos de Integración & Communication

### Inter-Microservices (Síncrono)
- **RestTemplate / WebClient**: Usar nombres de servicio Eureka (`http://microservice-name/...`)
- Ver `microservice-courses/src/.../client/` para ejemplos de clientes HTTP inter-servicio
- El Gateway los traduce: cliente externo → `/api/v1/courses/**` → `lb://microservice-courses`

### Configuración Centralizada
- Config Server se conecta a Git repo remoto o usa `native` (local) profile
- Cambios en `/configurations/**/*.yml` requieren **reinicio del Config Server** (no hot-reload)
- Los servicios caché las propiedades al startup; para actualizar dinámicamente usar `@RefreshScope` (requiere actuator endpoint)

### Autenticación (Gateway Filter)
- `AuthenticationFilter` intercepta requests en el Gateway
- Ver estructura en `microservice-gateway/.../filter/` para agregar lógica de validación de tokens

---

## Convenciones de Código Específicas

### Nombrado de Módulos
- Directorios: `microservice-{nombre}` (kebab-case)
- Exception: `microservice.students` (punto en lugar de guion) — **evitar en nuevos módulos**, usar guion

### Decoradores Comunes
```java
@SpringBootApplication          // Aplicación raíz
@EnableConfigServer            // Config Server
@EnableEurekaServer            // Eureka registry
@EnableEurekaClient / @SpringBootApplication  // Clientes Eureka (auto-activado)
@RestController @RequestMapping("/api/v1/...")  // Endpoints
@Service                        // Lógica de negocio
@Repository                     // Spring Data repos (heredan automáticamente @Component)
@Entity @Table(name="...")      // JPA entities
@Transactional                  // Métodos service (gestiona transacciones DB)
```

### Lombok Usage
```java
@Data @NoArgsConstructor @AllArgsConstructor @Entity  // Entidades
@Data @Builder                  // DTOs
```

---

## Estructura de Archivos Clave

| Ruta | Propósito |
|------|-----------|
| `pom.xml` | Parent Maven; define versiones Spring Cloud, módulos |
| `docker-compose.yml` | Infraestructura: MySQL (×2), PostgreSQL |
| `microservice-config/src/main/resources/` | Perfil nativo de Config Server |
| `configurations/*.yml` | YAMLs de servicios individuales (gestionados por Config Server) |
| `microservice-gateway/application.yml` | Rutas y predicados del Gateway |
| `microservice-*/pom.xml` | Dependencias específicas de cada servicio |

---

## Tareas Comunes

### Agregar nuevo endpoint en microservicio
1. Crear método en `{Service}Controller` con `@PostMapping/@GetMapping`
2. Implementar lógica en `{Service}Service`
3. Si necesita DB: agregar método a `{Entity}Repository`
4. Testear vía Gateway: `POST http://localhost:8090/api/v1/{ruta}/**`

### Crear nuevo microservicio
1. Copiar estructura de `microservice-students/` (más simple: sin cliente HTTP)
2. Agregar módulo a `pom.xml` raíz bajo `<modules>`
3. Configurar `spring.application.name` en `application.yml`
4. Crear YAML en `/configurations/microservice-{nombre}.yml`
5. Ejecutar `mvn clean package` desde raíz
6. Agregar ruta en Gateway si es cliente-facing

### Debuggear comunicación Eureka/Gateway
- Verificar Eureka dashboard: `http://localhost:8761/`
- Logs: `LOG LEVEL: com.netflix.eureka=DEBUG` en Config Server YAML
- Gateway logs: ver ruta predicates que no matchean en requests

---

## Notas Técnicas

- **Spring Cloud versión fija**: 2023.0.0 en parent pom (coincide con Spring Boot 3.1.12)
- **Java 17**: Requerido; usa `<java.version>17</java.version>` en todos los pom.xml
- **Perfiles Activos**: Cada servicio soporta `application-{profile}.yml` (ej: `application-local.yml`, `application-git.yml`)
- **Docker Networking**: Servicios en contenedores deben usar hostnames internos (ej: `http://mysql-student-db:3306`), no `localhost`
- **Port Bindings**: Reservados por convención:
  - 8888: Config
  - 8761: Eureka
  - 8090: Gateway
  - 8081+: Servicios individuales

---

## Referencias Internas

- README.md: Arquitectura general y tecnologías
- microservice-gateway: Point de entrada; personalizar rutas y filtros aquí
- microservice-config: Nodo de configuración; gestiona YAML de todos los servicios
