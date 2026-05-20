# MusicBand REST Gateway

REST-прослойка для HTTP-клиентов, которая общается с существующим сервером по UDP через `common.protocol`.

## Сборка

```bash
mvn -pl rest-gateway -am package
```

## Запуск

Сначала поднимите UDP сервер:

```bash
java -jar server/target/musicband-server-1.0-SNAPSHOT.jar musicBand.xml 5555
```

Затем запустите REST gateway:

```bash
java -jar rest-gateway/target/musicband-rest-gateway-1.0-SNAPSHOT.jar 0.0.0.0 8080 127.0.0.1 5555 5000
```

Важно:

- `0.0.0.0` — это адрес “слушать на всех интерфейсах” **для сервера**, но **клиент (Postman/браузер)** должен ходить на конкретный адрес, например `http://127.0.0.1:8080/...` или `http://localhost:8080/...`.
- Все пути начинаются с префикса **`/api/v1`**.

Параметры:

1. `httpHost`
2. `httpPort`
3. `udpHost`
4. `udpPort`
5. `timeoutMs`

Можно также задавать через переменные окружения:
`REST_HTTP_HOST`, `REST_HTTP_PORT`, `REST_UDP_HOST`, `REST_UDP_PORT`, `REST_TIMEOUT_MS`.

## Основные endpoint'ы

- Swagger / OpenAPI:
  - `GET /api/v1/swagger-ui`
  - `GET /api/v1/openapi.json`

- `GET /api/v1/bands`
- `GET /api/v1/bands/{id}`
- `POST /api/v1/bands/{key}`
- `PUT /api/v1/bands/{id}`
- `DELETE /api/v1/bands/by-key/{key}`
- `DELETE /api/v1/bands`
- `POST /api/v1/bands/replace-if-greater/{key}`
- `POST /api/v1/bands/replace-if-lower/{key}`
- `POST /api/v1/bands/remove-greater`
- `GET /api/v1/bands/filter?albumsCount=n`
- `GET /api/v1/bands/average/albums-count`
- `GET /api/v1/bands/albums-count/descending`
- `GET /api/v1/meta/info`
- `GET /api/v1/meta/help`

## Формат ответа

Все ответы возвращаются как JSON:

```json
{
  "success": true,
  "requestId": 42,
  "message": "Элемент добавлен.",
  "data": {}
}
```

## Быстрые проверки (Windows)

```powershell
curl.exe -i "http://127.0.0.1:8080/api/v1"
curl.exe -i "http://127.0.0.1:8080/api/v1/meta/info"
curl.exe -i "http://127.0.0.1:8080/api/v1/swagger-ui"
```
