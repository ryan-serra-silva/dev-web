compose-up:
	docker compose -f docker-compose.yaml up -d --build
compose-down:
	docker compose -f docker-compose.yaml down -v
compose-exec:
	docker compose -f docker-compose.yaml exec web bash
mvn-install:
	mvn clean install
web-run:
	mvn jetty:run
