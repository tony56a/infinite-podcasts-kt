FROM amazoncorretto:17

WORKDIR /home/app
COPY build/docker/main/layers/ /home/app/
COPY build/libs/ /home/app/app

ENTRYPOINT ["java", "-jar", "/home/app/app/infinite-podcast-kt-fat.jar"]
