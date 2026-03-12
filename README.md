# Invex 📋

> A high-performance, RESTful log management service built with Spring Boot — powered by a custom LRU Cache and Inverted Index for real-time log ingestion, keyword search, and paginated retrieval with zero database overhead.

---

## ✨ Features

| Feature | Description |
|---|---|
| 🧠 **LRU Cache Storage** | Custom `LinkedHashMap`-backed cache with automatic eviction of the oldest logs at capacity |
| ⚡ **Inverted Index Search** | `HashMap<String, List<LogEntry>>` enables near O(1) keyword lookups without full scans |
| 📄 **Efficient Pagination** | Paginated retrieval keeps API responses fast regardless of cache size |
| 🏷 **Automatic Indexing** | Every ingested log is tokenized and indexed immediately — searchable on arrival |
| 🪶 **Zero Infrastructure** | No database, no broker, no external dependencies — runs entirely in memory |
| 🌐 **RESTful API** | Clean endpoints for log ingestion, paginated retrieval, and keyword search |

---

## 🛠 Tech Stack

- **Language:** Java 17
- **Framework:** Spring Boot 3.x
- **Build Tool:** Maven
- **Core Data Structures:**
  - `LinkedHashMap` with custom `removeEldestEntry` → LRU eviction policy
  - `HashMap<String, List<LogEntry>>` → Inverted Index for keyword search

---

## ⚙️ Architecture & Flow

```
POST /logs
     │
     ▼
[ LogController ] ──────────────────── Receive log (level + message)
     │
     ▼
[ LogService ] ─────────────────────── Generate unique ID + timestamp
     │
     ├──► [ Inverted Index ]  ─────── Tokenize message → map keywords to log entry
     │
     └──► [ LRU Cache ] ──────────── Store log entry
               │
               └── Cache full? ──► Evict oldest entry + clear its index tokens
                                                    │
                                                    ▼
GET /logs          ──────────────────── Return paginated results from LRU Cache
GET /logs/search   ──────────────────── Keyword lookup via Inverted Index → O(1) or O(k)
```

### Why These Data Structures?

**LRU Cache** — By extending `LinkedHashMap` and overriding `removeEldestEntry`, Invex automatically drops the oldest log once the 100-entry limit is reached. No manual cleanup, no memory leaks.

**Inverted Index** — Instead of scanning every log on each search query, Invex maps individual keywords to the list of log entries that contain them. A search for `"error"` goes directly to its entry in the map — no iteration required.

---

## 🌐 API Reference

### Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/logs` | Ingest a new log entry |
| `GET` | `/logs` | Retrieve all logs (paginated) |
| `GET` | `/logs/search` | Search logs by keyword |

### Parameters

| Parameter | Endpoint | Type | Description |
|---|---|---|---|
| `level` | `POST /logs` | `string` | Log severity level (e.g. `INFO`, `WARN`, `ERROR`) |
| `message` | `POST /logs` | `string` | The log message content |
| `page` | `GET /logs` | `int` | Page number (zero-indexed) |
| `size` | `GET /logs` | `int` | Number of entries per page |
| `keyword` | `GET /logs/search` | `string` | Keyword to search across all indexed logs |

---

## 📖 Example Usage

**Ingest a log**
```bash
curl -X POST "http://localhost:8080/logs?level=INFO&message=System+Started"
```

**Ingest an error log**
```bash
curl -X POST "http://localhost:8080/logs?level=ERROR&message=Database+connection+failed"
```

**Retrieve all logs (first page)**
```bash
curl "http://localhost:8080/logs?page=0&size=10"
```

**Search by keyword**
```bash
curl "http://localhost:8080/logs/search?keyword=error"
```

---

## 🚦 Getting Started

### Prerequisites

- Java 17+
- Maven 3.6+

### Installation

```bash
# 1. Clone the repository
git clone https://github.com/sanuh1999/invex.git
cd invex

# 2. Build the project
mvn clean install

# 3. Run the application
mvn spring-boot:run

# 4. API is available at
# http://localhost:8080/logs
```

---

## 🧠 Key Design Decisions

| Decision | Rationale |
|---|---|
| LRU over unbounded map | Prevents unbounded memory growth in long-running services |
| Inverted Index over linear scan | Reduces search from O(n) to O(1)/O(k) as log volume grows |
| Token-level indexing | Enables partial keyword matches across multi-word log messages |
| In-memory only | Prioritizes raw speed; suitable for ephemeral or edge logging scenarios |

---

## 🛣 Roadmap

- [ ] Configurable cache capacity via `application.properties`
- [ ] Multi-keyword (AND/OR) search queries
- [ ] Log severity filtering on retrieval (`?level=ERROR`)
- [ ] Structured JSON log ingestion (replacing query params)
- [ ] Metrics endpoint (`/actuator/prometheus`) for observability
- [ ] Optional persistence layer with **Redis** or **PostgreSQL**

---

## 🤝 Contributing

Contributions are welcome! Please open an issue to discuss your idea before submitting a pull request.

---


---

*Built as a deep-dive into cache eviction policies, inverted indexing, and high-performance in-memory data management.*
