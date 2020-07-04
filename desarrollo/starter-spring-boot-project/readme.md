# Starter Spring Boot Project

Starter Spring Boot project without IDE dependency.

Its only a starter project, is not a stable final project.


## Requirements

- GNU/Linux (for bash helper).
- Text editor (Sublime Text 3 is recomended).


## Database initialize

Check if image exist

```bash
sudo docker images -q postgres:10;
```

Install postgres image

```bash
sudo docker pull postgres:10;
```

Make and run for first time with credentials: username `postgres` and password `dev`:

```bash
sudo docker run \
    -p 5432:5432 \
    --name postgres10 \
    -e POSTGRES_PASSWORD=dev \
    -e POSTGRES_DB=dev \
    -d postgres:10;
```

Stop and start the database container

```bash
sudo docker stop postgres10;
sudo docker start postgres10;
```

Login into database

```bash
psql -h 127.0.0.1 -p 5432 -U postgres -W
```

Drop the container

```bash
sudo docker rm postgres10;
```

Drop the image

```bash
sudo docker rmi postgres:10;
```