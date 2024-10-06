# Infinite Podcast(-Kt 🎉)

---

Service for generating podcasts, similar to the [python](https://github.com/tony56a/infinite-podcast/tree/main)
version, but using a Kotlin "canonical service stack", with:

* Micronaut as the DI/service framework
* Postgres for DB
* RabbitMQ as the internal job queue
* Redis for ephemeral storage
    * Rate limiting storage
    * Output Queue to front-end clients
* gRPC as the main RPC protocol
    * proto schemas are defined in the separate [protos](https://github.com/tony56a/protos) repository

For simplicity, sidecar services are deployed via Docker

## System Architecture

TODO

## How to Deploy

Perquisites:

* Get Container Engine (like Docker)
* Fill out .env with the relevant attributes(see [example.env](example.env))

### Docker

* Run `./gradlew clean build` to build the JAR
* Run ` docker build . -t infinite-podcast-kt:latest` to build + tag the image
* Run `docker-compose --profile service up -d` to bring up the sidecar services and service

### Local

* Run `./gradlew clean build` to build the JAR
* Run `docker-compose up -d` to bring up the sidecar services
* Run `env $(cat .env | xargs) ./gradlew run` to pipe in environment arguments + start the service

## TODO

* ~~per-model logging tags~~
* ~~DB for versioned LLM prompts~~
* multiple LLM clients
* ~~dynamic configuration files~~
* object store for audio clips
* ~~Docker image for main application~~
    * Fix gradle rule
* CI/CD to build/test image
* Unit tests
* ~~gRPC error handling interceptor~~
* ~~Rate limiting~~
    * Application layer, not in gRPC filter/interceptor though
* metrics
* authZ
* consistent logging