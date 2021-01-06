# Example project

# Run
To run please use command from below
- `./gradlew bootRun`

# Swagger
To open swagger with scanned api open web browser and open `http://localhost:8080/swagger-ui/`

# Application use case
1. Adding new resource to queue
- `curl -X POST http://localhost:8080/web-resource -H 'content-type: application/json' -d '{"url":"http://example.com"}'`

1. Getting all saved resource
- `curl -X GET http://localhost:8080/web-resource`

1. Getting content of downloaded resource
- `curl -X GET http://localhost:8080/web-resource/1/content`

1. Searching for given text in downloaded resources
- `curl -X GET http://localhost:8080/web-resource?content=example`
