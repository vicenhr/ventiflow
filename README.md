# ☕ VentiFlow

> Sistema de gestión de pedidos en tiempo real para cafeterías, con pantallas KDS, visor público y programa de lealtad.

![Java](https://img.shields.io/badge/Java-17+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
![WebSocket](https://img.shields.io/badge/WebSocket-STOMP-010101?style=for-the-badge&logo=socketdotio&logoColor=white)
![Flyway](https://img.shields.io/badge/Flyway-CC0200?style=for-the-badge&logo=flyway&logoColor=white)
![Lombok](https://img.shields.io/badge/Lombok-ED1C24?style=for-the-badge&logo=lombok&logoColor=white)
![HTML](https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=html5&logoColor=white)
![CSS](https://img.shields.io/badge/CSS3-1572B6?style=for-the-badge&logo=css3&logoColor=white)
![JavaScript](https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black)

---

## 📋 Tabla de Contenidos

- [Descripción General](#descripción-general)
- [Tecnologías](#tecnologías)
- [Arquitectura del Sistema](#arquitectura-del-sistema)
- [Requisitos Previos](#requisitos-previos)
- [Instalación y Configuración](#instalación-y-configuración)
- [API Reference](#api-reference)
- [WebSockets / Tiempo Real](#websockets--tiempo-real)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Estructura de la Base de Datos](#estructura-de-la-base-de-datos)

---

## Descripción General

VentiFlow es una plataforma backend desarrollada con **Spring Boot** que centraliza el flujo operativo de una cafetería. El sistema conecta el punto de venta (caja) con las pantallas de preparación (**KDS** — Kitchen/Bar Display System) y con la pantalla pública del local a través de **WebSockets en tiempo real**.

### Funcionalidades principales

- **Gestión de pedidos** con turno asignado automáticamente (ej. `#A-001`)
- **KDS para Cocina y Barra** con actualización instantánea via WebSocket
- **Pantalla pública (CDS)** que muestra el estado de los pedidos a los clientes
- **Programa de lealtad** con acumulación de puntos por cliente
- **Dashboard de administración** con historial y métricas de ventas
- **Migraciones de base de datos** gestionadas con Flyway

---

## Tecnologías

| Capa | Tecnología |
|---|---|
| Backend | Java 17 + Spring Boot 3.5 |
| Persistencia | Spring Data JPA + PostgreSQL |
| Tiempo Real | Spring WebSocket + STOMP |
| Migraciones DB | Flyway |
| Frontend | HTML, CSS, JavaScript |
| Validación | Spring Validation |
| Utilities | Lombok, Spring DevTools |
| Build | Maven |

---

## Arquitectura del Sistema

```
┌─────────────┐     REST API      ┌──────────────────┐
│    Caja     │ ────────────────► │                  │
│  (Cashier)  │                   │   Spring Boot    │
└─────────────┘                   │    Backend       │
                                  │                  │
┌─────────────┐    WebSocket      │  /ws-ventiflow   │
│ KDS Cocina  │ ◄──────────────── │                  │
│  KDS Barra  │                   │   PostgreSQL     │
└─────────────┘                   │                  │
                                  └──────────────────┘
┌─────────────┐    WebSocket
│  TV Pública │ ◄────────────────
│    (CDS)    │
└─────────────┘
```

---

## Requisitos Previos

- **Java 17** o superior
- **Maven 3.8+**
- **PostgreSQL 14+** (base de datos corriendo y accesible)

---

## Instalación y Configuración

### 1. Clonar el repositorio

```bash
git clone https://github.com/vicenhr/ventiflow.git
cd ventiflow
```

### 2. Configurar la base de datos

Crea una base de datos en PostgreSQL:

```sql
CREATE DATABASE ventiflow;
```

### 3. Configurar `application.properties`

Edita `src/main/resources/application.properties` con tus credenciales:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/ventiflow
spring.datasource.username=TU_USUARIO
spring.datasource.password=TU_CONTRASEÑA

spring.jpa.hibernate.ddl-auto=validate
spring.flyway.enabled=true
```

> Las migraciones de Flyway se ejecutarán automáticamente al iniciar la aplicación.

### 4. Compilar y ejecutar

```bash
mvn spring-boot:run
```

El servidor quedará disponible en `http://localhost:8080`.

---

## API Reference

Todas las rutas bajo `/api/v1/` (excepto las indicadas) requieren autenticación mediante token de sesión.

---

### 🔐 Autenticación

#### `POST /api/v1/auth/login`

Inicia sesión y obtiene un token de acceso.

**Requiere autenticación:** No

**Body:**
```json
{
  "usuario": "string",
  "clave": "string"
}
```

**Respuesta `200 OK`:**
```json
{
  "token": "...",
  "rol": "ADMIN",
  "alias": "Vicente"
}
```

---

### 👤 Clientes

#### `GET /api/v1/clientes/{telefono}`

Busca un cliente por su número de teléfono.

**Respuesta `200 OK`:**
```json
{
  "idCliente": 1,
  "nombreAlias": "Ana",
  "telefono": "5512345678",
  "puntosAcumulados": 150
}
```

#### `POST /api/v1/clientes`

Registra un nuevo cliente en el programa de lealtad.

**Body:**
```json
{
  "telefono": "5512345678",
  "nombreAlias": "Ana"
}
```

**Respuesta `201 Created`:** Objeto del cliente con `puntosAcumulados: 0`.

---

### 📦 Pedidos

#### `POST /api/v1/pedidos`

Crea un nuevo pedido desde caja.

**Requiere rol:** `CAJA`

**Body:**
```json
{
  "idUsuario": 2,
  "idCliente": 1,
  "detalles": [
    { "idProducto": 5, "cantidad": 1 },
    { "idProducto": 3, "cantidad": 2 }
  ]
}
```

**Respuesta `201 Created`:** `PedidoDTO` completo con `turnoAsignado` generado (ej. `"A-001"`).

---

#### `GET /api/v1/pedidos/activos?area={area}`

Obtiene los pedidos activos filtrados por área de preparación.

**Parámetros:**

| Query Param | Valores posibles |
|---|---|
| `area` | `Cocina`, `Barra` |

**Estados incluidos:** `PENDIENTE`, `PREPARANDO`, `LISTO`

**Respuesta `200 OK`:** Lista de `PedidoDTO`.

---

#### `PATCH /api/v1/pedidos/{idPedido}/estado`

Actualiza el estado general de un pedido.

**Requiere rol:** Verificación por roles

**Body:**
```json
{
  "estadoGeneral": "PREPARANDO"
}
```

**Estados válidos:** `PENDIENTE` → `PREPARANDO` → `LISTO` → `ENTREGADO`

**Respuesta `200 OK`:** Confirmación de actualización.

---

#### `GET /api/v1/pedidos/pantalla-publica`

Devuelve todos los pedidos activos del local para carga inicial de la pantalla pública.

**Requiere autenticación:** No

**Respuesta `200 OK`:** Lista de `PedidoDTO`.

---

#### `GET /api/v1/pedidos/historial?fechaInicio={fecha}&fechaFin={fecha}`

Obtiene el historial de pedidos en un rango de fechas para el dashboard.

**Requiere rol:** `ADMIN`

**Parámetros:**

| Query Param | Formato |
|---|---|
| `fechaInicio` | `YYYY-MM-DD` |
| `fechaFin` | `YYYY-MM-DD` |

**Respuesta `200 OK`:** Lista de pedidos históricos con datos de ventas, tickets y flujo horario.

---

## WebSockets / Tiempo Real

**Endpoint de conexión:** `http://localhost:8080/ws-ventiflow`  
**Protocolo:** STOMP sobre WebSocket

---

### Tópicos disponibles

#### `/topic/kds/cocina`

Notifica a la pantalla de **Cocina** cuando entra un pedido con alimentos.

**Datos recibidos:** `PedidoDTO` completo.

---

#### `/topic/kds/barra`

Notifica a la pantalla de **Barra/Baristas** cuando entra un pedido con bebidas.

**Datos recibidos:** `PedidoDTO` completo.

---

#### `/topic/cds/publico`

Actualiza la **TV pública del local** al cambiar el estado de un pedido. Dispara la campana de aviso.

**Datos recibidos:**
```json
{
  "turnoAsignado": "A-001",
  "estadoGeneral": "LISTO"
}
```

---

### Ejemplo de suscripción (JavaScript)

```javascript
const socket = new SockJS('http://localhost:8080/ws-ventiflow');
const stompClient = Stomp.over(socket);

stompClient.connect({}, () => {
  stompClient.subscribe('/topic/kds/cocina', (message) => {
    const pedido = JSON.parse(message.body);
    console.log('Nuevo pedido en cocina:', pedido);
  });
});
```

---

## Estructura del Proyecto

```
ventiflow/
├── src/
│   ├── main/
│   │   ├── java/blav/ventiflow/
│   │   │   ├── auth/          # Autenticación y tokens
│   │   │   ├── cliente/       # Gestión de clientes y lealtad
│   │   │   ├── pedido/        # Lógica de pedidos y KDS
│   │   │   ├── websocket/     # Configuración STOMP y eventos
│   │   │   └── config/        # Configuración general
│   │   └── resources/
│   │       ├── db/migration/  # Scripts Flyway (V1__, V2__...)
│   │       └── static/        # Frontend (HTML, CSS, JS)
│   └── test/
└── pom.xml
```

---

## Estructura de la Base de Datos

El esquema es gestionado automáticamente por **Flyway** al arrancar la aplicación. El formato estándar de un pedido en el sistema (`PedidoDTO`) es:

```json
{
  "idPedido": 13,
  "turnoAsignado": "A-001",
  "estadoGeneral": "PENDIENTE",
  "fechaHoraCreacion": "2026-06-07T19:30:38.178527",
  "detalles": [
    {
      "idProducto": 5,
      "nombreProducto": "Croissant de Mantequilla",
      "cantidad": 1
    }
  ]
}
```

---

## Autor

**Vicente Hernández Ramos** — [@vicenhr](https://github.com/vicenhr)
