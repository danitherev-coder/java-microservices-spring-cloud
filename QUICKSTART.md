# Guía Rápida de Setup para Desarrollo Local

## 1️⃣ Clone & Setup (5 minutos)

```bash
# Clonar proyecto
git clone https://github.com/danitherev-coder/java-microservices-spring-cloud.git
cd java-microservices-spring-cloud-main

# Copiar variables de entorno
cp .env.example .env.local
cp .env.docker.example .env.docker

# Editar .env.local con TUS credenciales
nano .env.local  # O usa tu editor favorito
```

## 2️⃣ Levantar Bases de Datos (2 minutos)

```bash
docker-compose --env-file .env.docker up -d

# Verificar que están corriendo
docker-compose ps
```

## 3️⃣ Arrancar Microservicios (En 4 Terminales)

```bash
# Terminal 1
cd microservice-config && mvn spring-boot:run

# Terminal 2 (espera 10s)
cd microservice-eureka && mvn spring-boot:run

# Terminal 3 (espera 10s)
cd microservice-gateway && mvn spring-boot:run

# Terminal 4+ (cualquier orden)
cd microservices-auth && mvn spring-boot:run
cd microservice.students && mvn spring-boot:run
cd microservice-courses && mvn spring-boot:run
```

## ✅ Listo

- Eureka Dashboard: http://localhost:8761/
- API Gateway: http://localhost:8090
- Test: `curl http://localhost:8090/api/v1/students/all`

## 🔑 Variables de Entorno Clave

| Variable | Para Qué | Donde Va |
|----------|----------|----------|
| `MYSQL_ROOT_PASSWORD` | Contraseña MySQL | `.env.docker` |
| `POSTGRES_PASSWORD` | Contraseña PostgreSQL | `.env.docker` |
| `STUDENTS_DB_PASSWORD` | BD Students | `.env.local` |
| `AUTH_DB_PASSWORD` | BD Auth | `.env.local` |
| `COURSES_DB_PASSWORD` | BD Courses | `.env.local` |
| `ROLES_DB_PASSWORD` | BD Roles | `.env.local` |

## ❌ Problemas Comunes

**Error: "Config Server not reachable"**
- Asegúrate que `microservice-config` está corriendo en puerto 8888

**Error: "Eureka says DOWN"**
- Espera 30 segundos, Eureka es lento en reconocer instancias

**Error: "Port already in use"**
```bash
lsof -i :8090 | awk '{print $2}' | tail -1 | xargs kill -9
```

## 🚀 IntelliJ IDEA + EnvFile Plugin

1. Instala plugin: Preferences → Plugins → "EnvFile"
2. Edit Configurations → Spring Boot:
   - ✓ Enable EnvFile
   - EnvFile: `.env.local`
3. Click Run

---

¿Preguntas? Ver [README.md](./README.md) sección "Configuración de Variables de Entorno"
