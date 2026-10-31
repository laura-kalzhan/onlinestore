# Online Store REST API

CIT4242 Assignment 1 - Laura Kalzhan

REST API for an online store product catalogue. Made with Spring Boot 3, Maven and PostgreSQL.

## How to run

You need Java 17 or newer and PostgreSQL.

1. Create the database:

```
CREATE DATABASE onlinestore;
```

2. Database settings are in `src/main/resources/application.properties`. By default the app uses:

- url: `jdbc:postgresql://localhost:5432/onlinestore`
- username: `postgres`
- password: `postgres`

If your password is different, change it in the file or set `DB_PASSWORD` before running (`DB_URL` and `DB_USERNAME` also work).

3. Build and run:

```
./mvnw clean package
java -jar target/onlinestore-1.0.0.jar
```

On Windows use `mvnw.cmd` instead of `./mvnw`.

The app starts on port 8080. The products table is created automatically by Hibernate.

The tests run with an H2 in-memory database, so `./mvnw clean package` works even if PostgreSQL is not running.

## Endpoints

| Method | URL | Description | Status |
|---|---|---|---|
| GET | /api/products | get all products | 200 |
| GET | /api/products/{id} | get product by id | 200, 404 |
| POST | /api/products | create product | 201, 400 |
| PUT | /api/products/{id} | update product | 200, 400, 404 |
| DELETE | /api/products/{id} | delete product | 204, 404 |
| GET | /api/products/category/{category} | products in a category | 200 |
| GET | /api/products/search?name=... | search by name | 200, 400 |

### Get all products

```
curl -i http://localhost:8080/api/products
```

Response 200:

```json
[
  {"id":1,"name":"Test Product","description":null,"price":29.99,"category":"Test","inStock":true,"createdAt":"2026-10-31T18:34:38","updatedAt":"2026-10-31T18:34:38"},
  {"id":2,"name":"Wireless Mouse","description":"USB mouse","price":7500.00,"category":"Electronics","inStock":false,"createdAt":"2026-10-31T18:34:38","updatedAt":"2026-10-31T18:34:38"}
]
```

### Get product by id

```
curl -i http://localhost:8080/api/products/1
```

Response 200:

```json
{"id":1,"name":"Test Product","description":null,"price":29.99,"category":"Test","inStock":true,"createdAt":"2026-10-31T18:34:38","updatedAt":"2026-10-31T18:34:38"}
```

If the id does not exist, response 404:

```json
{"status":404,"message":"Product with id 9999 not found","timestamp":"2026-10-31T18:34:38","errors":{}}
```

### Create product

```
curl -i -X POST http://localhost:8080/api/products -H "Content-Type: application/json" -d '{"name":"Test Product","price":29.99,"category":"Test"}'
```

Response 201 (with header `Location: http://localhost:8080/api/products/1`):

```json
{"id":1,"name":"Test Product","description":null,"price":29.99,"category":"Test","inStock":true,"createdAt":"2026-10-31T18:34:38","updatedAt":"2026-10-31T18:34:38"}
```

If the data is wrong:

```
curl -i -X POST http://localhost:8080/api/products -H "Content-Type: application/json" -d '{"name":"","price":-10}'
```

Response 400:

```json
{"status":400,"message":"Validation failed","timestamp":"2026-10-31T18:34:39","errors":{"name":"Name is required","price":"Price must be positive","category":"Category is required"}}
```

### Update product

```
curl -i -X PUT http://localhost:8080/api/products/1 -H "Content-Type: application/json" -d '{"name":"Updated Name","price":39.99,"category":"Test"}'
```

Response 200:

```json
{"id":1,"name":"Updated Name","description":null,"price":39.99,"category":"Test","inStock":true,"createdAt":"2026-10-31T18:34:38","updatedAt":"2026-10-31T18:34:39"}
```

If the id does not exist it returns 404, and if the body is wrong it returns 400 (same as create).

### Delete product

```
curl -i -X DELETE http://localhost:8080/api/products/1
```

Response 204, no body. If the id does not exist, 404.

### Get products by category

```
curl -i http://localhost:8080/api/products/category/Electronics
```

Response 200:

```json
[
  {"id":2,"name":"Wireless Mouse","description":"USB mouse","price":7500.00,"category":"Electronics","inStock":false,"createdAt":"2026-10-31T18:34:38","updatedAt":"2026-10-31T18:34:38"}
]
```

If there are no products in the category it returns 200 with `[]`.

### Search by name

```
curl -i "http://localhost:8080/api/products/search?name=mouse"
```

Response 200:

```json
[
  {"id":2,"name":"Wireless Mouse","description":"USB mouse","price":7500.00,"category":"Electronics","inStock":false,"createdAt":"2026-10-31T18:34:38","updatedAt":"2026-10-31T18:34:38"}
]
```

No results gives 200 with `[]`. Without the `name` parameter it returns 400.

## Errors

All errors return the same format:

```json
{"status":404,"message":"Product with id 5 not found","timestamp":"2026-10-31T18:34:38","errors":{}}
```

For validation errors, `errors` has the field name and the message. For other errors it is empty.

## Assumptions

- `inStock` is optional. If it is not sent it is `true`. PUT replaces the whole product, so if `inStock` is not sent in PUT it also becomes `true`.
- PUT needs all required fields (name, price, category).
- If the client sends `id`, `createdAt` or `updatedAt` in the body they are ignored.
- Price can have max 2 decimal places (it is money).
- Category filter and search are not case sensitive. Search finds products whose name contains the text.
- Search with empty or missing `name` returns 400.
- Products are returned sorted by id.
- If PUT sends exactly the same data, `updatedAt` does not change because nothing was updated.
