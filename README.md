# Employee Management — Quick README

Minimal employee management app (JDBC DAO + console + Spring Boot web UI). Uses MySQL for storage. Works with Docker or local MySQL.
## Quick prerequisites
- Java 17+, Maven 3.8+
- Docker (recommended) or a local MySQL server

## Quick start (Docker)
1. Start DB:

```bash
docker compose up -d
```
2. Verify DB:

```bash
docker logs fp_mysql --tail 200
```

3. Build:
```bash
mvn -DskipTests package
```

4. Run (dev):
```bash
mvn -DskipTests spring-boot:run
```

### App serves
HTTPS on https://localhost:8080 (self-signed keystore — accept browser warning).

## Credentials (local Docker init)
root: rootpass
app user: proj_user / proj_pass
test user: test_user / secret123

### check mysql locally
``` bash
mysql -u your_username -p -h 127.0.0.1
```
