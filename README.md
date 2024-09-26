# Infinite Podcast(-Kt 🎉)

---

Service for generating podcasts, similar to the [python](https://github.com/tony56a/infinite-podcast/tree/main)
version, but using a Kotlin "canonical service stack", with:

* Micronaut as the DI/service framework
* Postgres for DB
* RabbitMQ as the internal job queue
* gRPC as the main RPC protocol
    * proto schemas are defined in the separate protos repository

For simplicity, sidecar services are deployed via Docker

## System Architecture

TODO

## How to Deploy

### Docker

* Get Docker
* Run `docker-compose up -d` to bring up sidecar services
* Run `./gradlew run`

## TODO

* per-model logging tags
* DB for versioned LLM prompts
* multiple LLM clients
* dynamic configuration files
* object store for audio clips
* Docker image for main application
* CI/CD to build/test image
* Unit tests
* gRPC error handling interceptor
* Rate limiting
    * Application layer, not in gRPC filter/interceptor though
* metrics
* authZ
* consistent logging