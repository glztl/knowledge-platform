# 构建阶段
FROM maven:3.8.6-openjdk-17 AS builder

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests -B

# 运行阶段
FROM openjdk:17-jdk-slim

WORKDIR /app

# 复制 jar 包
COPY --from=builder /app/target/*.jar app.jar

# 复制配置文件
COPY application-prod.yml /app/

# 创建日志目录
RUN mkdir -p /app/logs

# 暴露端口
EXPOSE 10000

# 启动命令
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]