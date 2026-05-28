# ✉️ Email Service

Microserviço responsável pelo processamento e envio assíncrono de e-mails na plataforma. O serviço consome mensagens de uma fila do RabbitMQ, realiza o envio utilizando o Spring Mail (SMTP) e armazena o histórico de envios em um banco de dados PostgreSQL.

---

## 🛠️ Tecnologias Utilizadas

- **Java 21**
- **Spring Boot 3.3.1**
  - **Spring Web**
  - **Spring Data JPA**
  - **Spring AMQP (RabbitMQ)**
  - **Spring Mail**
  - **Spring Boot Actuator**
- **PostgreSQL**
- **Flyway Migration**
- **Lombok**
- **UUID Creator** (geração de UUIDs ordenados no tempo)
- **Docker**

---

## 🧩 Arquitetura & Funcionamento

O projeto foi construído seguindo a estrutura modular do Spring. Ele funciona de forma reativa a eventos enviados para o broker:

1. **Consumo de Mensagens**: O `EmailConsumer` monitora a fila do RabbitMQ (configurada na propriedade `broker.queue.email.name`, padrão `default.email`).
2. **Processamento**: Ao receber um payload do tipo `EmailRequest`, os dados são validados e mapeados para a entidade `Email`.
3. **Envio**: O `EmailService` realiza o disparo real da mensagem via `JavaMailSender` (configurado para SMTP, ex: Gmail).
4. **Persistência**: O status final do e-mail (`SENT` ou `ERROR`) é persistido na tabela `emails` do banco de dados junto com a data/hora de envio e identificadores relacionados.

---

## 📂 Estrutura do Projeto

O código-fonte está localizado no pacote `dev.jorge.projects.email` e possui a seguinte divisão:

```text
src/main/java/dev/jorge/projects/email
├── configs
│   └── RabbitMQConfig.java         # Configuração da fila e conversor de mensagens JSON
├── consumers
│   └── EmailConsumer.java           # Listener que consome as mensagens da fila RabbitMQ
├── dtos
│   └── requests
│       └── EmailRequest.java        # Record DTO com os campos userId, emailTo, subject e text
├── entities
│   └── Email.java                   # Entidade JPA que mapeia a tabela "emails"
├── enums
│   └── StatusEmail.java             # Enum com os estados possíveis (SENT, ERROR)
├── repositories
│   └── EmailRepository.java         # Interface JPA para persistência da entidade Email
├── services
│   └── EmailService.java            # Lógica de negócio e integração com JavaMailSender
└── EmailServiceApplication.java     # Classe principal de inicialização da aplicação
```

---

## 🗄️ Modelo de Dados

As migrações do banco de dados são gerenciadas via **Flyway**. O schema inicial (`V1__create_table_emails.sql`) cria a seguinte tabela:

```sql
CREATE TABLE emails (
    email_id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    email_from VARCHAR(50) NOT NULL,
    email_to VARCHAR(50) NOT NULL,
    subject VARCHAR(255) NOT NULL,
    text TEXT NOT NULL,
    send_date_email TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status_email VARCHAR(20) NOT NULL,
    CONSTRAINT fk_emails_users FOREIGN KEY (user_id) REFERENCES users(user_id)
);
```

> [!NOTE]
> A tabela possui uma chave estrangeira para `users(user_id)`. Isso indica que este banco de dados é compartilhado ou o schema espera a tabela de usuários criada pelo serviço correspondente (ex: `auth-service`).

---

## ⚙️ Configurações & Variáveis de Ambiente

Para executar a aplicação, é necessário definir as seguintes variáveis de ambiente:

| Variável | Descrição | Exemplo |
|----------|-----------|---------|
| `DATABASE_URL` | URL de conexão JDBC com o PostgreSQL | `jdbc:postgresql://localhost:5432/email_db` |
| `DATABASE_USERNAME` | Nome de usuário do banco de dados | `postgres` |
| `DATABASE_PASSWORD` | Senha do banco de dados | `sua_senha` |
| `RABBITMQ_URL` | Endereço do servidor RabbitMQ | `amqp://localhost:5672` |
| `EMAIL_USERNAME` | E-mail remetente (SMTP) | `seu-email@gmail.com` |
| `EMAIL_PASSWORD` | Senha de app/token de acesso do e-mail | `abcd efgh ijkl mnop` |

---

## 🚀 Como Executar

### Pré-requisitos
- JDK 21 instalado
- Maven instalado (ou utilizar o wrapper `./mvnw`)
- Instâncias do PostgreSQL e do RabbitMQ em execução

### Executando Localmente

1. Defina as variáveis de ambiente necessárias no seu terminal ou ambiente de desenvolvimento.
2. Compile o projeto:
   ```bash
   mvn clean package
   ```
3. Execute o microserviço:
   ```bash
   mvn spring-boot:run
   ```
   A aplicação será iniciada na porta `8082` (conforme definido em `application.properties`).

### Executando via Docker

O projeto possui um `Dockerfile` multi-stage configurado para build e execução.

1. Construa a imagem Docker:
   ```bash
   docker build -t email-service .
   ```
2. Inicialize o container passando as variáveis de ambiente necessárias:
   ```bash
   docker run -d -p 8082:8082 \
     -e DATABASE_URL=jdbc:postgresql://host.docker.internal:5432/email_db \
     -e DATABASE_USERNAME=postgres \
     -e DATABASE_PASSWORD=sua_senha \
     -e RABBITMQ_URL=amqp://host.docker.internal:5672 \
     -e EMAIL_USERNAME=seu-email@gmail.com \
     -e EMAIL_PASSWORD=sua_senha_app \
     email-service
   ```