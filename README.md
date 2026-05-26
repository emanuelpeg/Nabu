# Nabu 📜

[![License](https://img.shields.io/badge/License-EPL%202.0-red.svg)](https://opensource.org/licenses/EPL-2.0)
[![Database: Agnostic](https://img.shields.io/badge/Database-Agnostic-blue.svg)](#)
[![Inspired By: PostgREST](https://img.shields.io/badge/Inspired%20By-PostgREST-orange.svg)](https://postgrest.org/)

**Nabu** is a lightweight, database-agnostic REST API engine that instantly transforms any database schema into dynamic CRUD and DDL endpoints. Inspired by PostgREST, Nabu goes a step further by providing multi-database support and schema management capabilities directly via HTTP methods.

> **Why Nabu?** In Babylonian mythology, **Nabu** is the god of writing, literacy, and wisdom, famed for recording the fates of gods and men upon clay **tablets**. Just as ancient Nabu managed the celestial tablets, this project provides a seamless API layer to manage your modern database tables.

---

## ✨ Features

- 🌍 **Database Agnostic:** Designed to abstract database connections (PostgreSQL, MySQL, SQLite, etc.) into a unified internal query builder.
- 🏗️ **Dynamic Schema Management (DDL via REST):** Create, inspect, alter, or drop tables using standard HTTP methods.
- ⚡ **Instant CRUD (DML via REST):** Automatically maps tables to queryable endpoints with advanced filtering, pagination, and sorting.
- 🔒 **Safe & Isolated:** Built-in SQL injection prevention using prepared statements and parameterized queries.
- 🛠️ **Educational & Extensible:** Clean architecture designed to deep-dive into database abstraction patterns.

---

## 🚀 How It Works (API Architecture)

Nabu maps HTTP methods to both Schema operations (DDL) and Data operations (DML).

### 1. Schema Management (`/tables`)

| Method | Endpoint | Description | Payload Example |
| :--- | :--- | :--- | :--- |
| **POST** | `/tables` | Create a new table in the database | `{"name": "users", "columns": [{"name": "id", "type": "serial"}, {"name": "name", "type": "varchar"}]}` |
| **GET** | `/tables` | List all tables and their metadata | *None* |
| **DELETE** | `/tables/:name` | Drop a specific table | *None* |

### 2. Data CRUD (`/tables/:name`)

| Method | Endpoint | Description | Query / Payload Example |
| :--- | :--- | :--- | :--- |
| **GET** | `/tables/:name` | Query rows from a table (supports filters) | `/tables/users?age=gte.18&sort=name.desc` |
| **POST** | `/tables/:name` | Insert a new row (or multiple rows) | `{"name": "Alice", "age": 25}` |
| **PATCH** | `/tables/:name` | Update rows matching a condition | `payload: {"status": "active"}` *with query filters* |
| **DELETE** | `/tables/:name` | Delete rows matching a condition | `/tables/users?id=eq.5` |

---

## 🛠️ Tech Stack & Structure

- **Core Engine:** Written with adaptability in mind (e.g., Node.js/TypeScript, Go, or Python).
- **Database Driver Interface:** An abstract adapter layer to easily swap between SQL engines.

```text
nabu/
├── src/
│   ├── config/          # Environment and DB connection settings
│   ├── adapters/        # Database drivers (PostgresAdapter, MySQLAdapter)
│   ├── parser/          # Converts URL query strings into structured SQL AST
│   ├── routes/          # Dynamic HTTP route handlers (/tables, /tables/:name)
│   └── server.js        # Application entrypoint
├── tests/               # Integration tests for different DB engines
├── .env.example         # Template for database configuration
└── README.md
```

---

## ⚙️ Getting Started

### 1. Clone the repository
```bash
git clone https://github.com/yourusername/nabu.git
cd nabu
```

### 2. Configure Environment
Create a `.env` file based on `.env.example`:
```env
PORT=3000
DB_TYPE=postgres # postgres, mysql, sqlite
DB_HOST=localhost
DB_PORT=5432
DB_USER=nabu_user
DB_PASSWORD=secret
DB_NAME=nabu_db
```

### 3. Install & Run (Example for Node/TS)
```bash
npm install
npm run dev
```

---

## 🗺️ Roadmap

- [ ] Abstract Layer interface definition
- [ ] PostgreSQL dynamic CRUD adapter
- [ ] SQLite memory adapter for rapid local testing
- [ ] Dynamic DDL operations (`POST /tables`)
- [ ] Advanced URL querying filters (`eq`, `gte`, `lte`, `like`, `in`)
- [ ] JWT Authentication & Row-level Security (RLS) simulation

---

## 📄 License

This project is open-source and available under the [MIT License](LICENSE).
