# Estágio 1: Build da aplicação com Maven e Java 17
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder

WORKDIR /build

# Copia arquivos do Maven e faz download do cache de dependências
COPY pom.xml .
RUN mvn dependency:go-offline -B || true

# Copia código-fonte e compila pacote JAR
COPY src ./src
RUN mvn clean package -DskipTests -B

# Estágio 2: Imagem final leve com JRE 17 Alpine
FROM eclipse-temurin:17-jre-alpine

LABEL maintainer="Sistema PDV <suporte@examplepdv.com.br>"
LABEL description="Sistema PDV com Banco de Dados H2 e Docker"

WORKDIR /app

# Instala curl e tzdata para healthcheck e fuso horário correto
RUN apk add --no-cache curl tzdata && \
    cp /usr/share/zoneinfo/America/Sao_Paulo /etc/localtime && \
    echo "America/Sao_Paulo" > /etc/timezone

# Cria diretório para persistência de dados do H2 e uploads
RUN mkdir -p /data && chmod 777 /data
VOLUME ["/data"]

# Copia o artefato compilado do estágio de build
COPY --from=builder /build/target/*.jar app.jar

# Variáveis de ambiente padrão com H2 em arquivo persistente (/data/pdvdb)
ENV PORT=8080 \
    SPRING_DATASOURCE_URL="jdbc:h2:file:/data/pdvdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;AUTO_RECONNECT=TRUE" \
    SPRING_DATASOURCE_USERNAME="sa" \
    SPRING_DATASOURCE_PASSWORD="" \
    SPRING_H2_CONSOLE_ENABLED="true" \
    JAVA_OPTS="-Xms128m -Xmx512m -XX:+UseG1GC -XX:+ExitOnOutOfMemoryError"

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
  CMD curl -f http://localhost:${PORT:-8080}/login || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
