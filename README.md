# 🛍️ Accenture Store Microservice

Um sistema de e-commerce moderno construído com arquitetura de microsserviços utilizando Spring Boot, implementando os princípios da Clean Architecture dentro de um framework MVC.

## 🚀 Visão Geral da Arquitetura

O sistema consiste nos seguintes microsserviços:
- 🔹 **Eureka Server**: Descoberta e registro de serviços
- 🛒 **Order Service**: Gerenciamento e processamento de pedidos
- 📦 **Stock Service**: Gerenciamento de estoque de produtos
- 💳 **Payment Service**: Processamento de pagamentos
- 📢 **Notification Service**: Gerenciamento de notificações do sistema

## 🛠️ Requisitos

- ☕ **Java JDK 17** ou superior
- 🐘 **Maven 3.8.x** ou superior
- 🐬 **MySQL 8** ou superior
- 🐰 **RabbitMQ** para filas de mensagens
- 🔄 **Docker** (opcional, para facilitar a execução dos serviços)

## 📥 Instalação e Configuração

1. Clone o repositório:
```bash
git clone https://github.com/KarolDiniz/accenture-summer-academy-backend.git
```
Ou faça o download do arquivo ZIP e extraia-o em seu computador.

## 🛠️ Configuração de Ambiente

Edite o arquivo `application.properties` e insira suas credenciais do banco de dados MySQL:

```yml
datasource:
  url: jdbc:mysql://localhost:3306/seudatabase
  driver-class-name: com.mysql.cj.jdbc.Driver
  username: seunome
  password: suasenha
```

## ▶️ Inicialização dos Serviços

Inicie os serviços na seguinte ordem:

1️⃣ Inicie o **Eureka Server**:
```bash
cd eureka-server
mvn spring-boot:run
```

2️⃣ Em seguida, inicie os demais serviços (abra um novo terminal para cada um):
```bash
# 🛒 Order Service
cd order-service
mvn spring-boot:run

# 📦 Stock Service
cd stock-service
mvn spring-boot:run

# 💳 Payment Service
cd payment-service
mvn spring-boot:run

# 📢 Notification Service
cd notification-service
mvn spring-boot:run
```
Ou execute-os diretamente na sua IDE.

## 🔌 Configuração de Portas Padrão

- **Eureka Server**: `8761` ➡️ [http://localhost:8761](http://localhost:8761)
- **Order Service**: `8081`
- **Stock Service**: `8083`
- **Payment Service**: `8082`
- **Notification Service**: `8085`

## 🐰 Acessando o RabbitMQ

Para acessar o painel do RabbitMQ:

🔗 [https://rabbitmq.tericcabrel.com/](https://rabbitmq.tericcabrel.com/)

🔑 Credenciais:
   - **Usuário**: `admin`
   - **Senha**: `MyStrong-P4ssw0rd$`

## 📌 Endpoints Disponíveis

### 🛒 Order Service
```
GET    /api/orders           - Obter todos os pedidos
POST   /api/orders           - Criar um novo pedido
GET    /api/orders/{id}      - Obter um pedido por ID
DELETE /api/orders/{id}      - Excluir um pedido por ID
PATCH  /api/orders/{id}      - Atualizar o status de um pedido
GET    /api/orders/{id}/history - Obter histórico de status do pedido
```
Exemplo de requisição para **POST /api/orders**:
```json
{
  "customerEmail": "adicioneseuemail",
  "totalAmount": 249.98,
  "paymentMethod": "BOLETO",
  "items": [
    {
      "productId": 1,
      "sku": "IPH15PRO-256-BLK",
      "quantity": 1,
      "price": 99.99
    }
  ]
}
```

### 📦 Product Service
```
GET    /api/products         - Obter todos os produtos
POST   /api/products         - Criar um novo produto
GET    /api/products/{id}    - Obter um produto por ID
PUT    /api/products/{id}    - Atualizar um produto
DELETE /api/products/{id}    - Excluir um produto
GET    /api/products/sku/{sku} - Obter um produto pelo SKU
```
Exemplo de requisição para **POST /api/products**:
```json
{
    "name": "iPhone 15 Pro",
    "sku": "IPH15PRO-256-BLK",
    "description": "iPhone 15 Pro 256GB Black",
    "price": 999.99,
    "stockQuantity": 20
}
```

## 📄 Documentação da API

Após iniciar cada serviço, acesse a documentação Swagger:
- **Order Service**: [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)
- **Stock Service**: [http://localhost:8083/swagger-ui.html](http://localhost:8083/swagger-ui.html)
- **Payment Service**: [http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html)
- **Notification Service**: [http://localhost:8085/swagger-ui.html](http://localhost:8085/swagger-ui.html)

## 🧪 Testes

Para executar os testes de todos os serviços:
```bash
mvn test
```

Para testar um serviço específico:
```bash
cd nome-do-servico
mvn test
```

## 🛠️ Solução de Problemas

1. **Problemas de Registro de Serviços** ⚠️
   - Certifique-se de que o **Eureka Server** está em execução antes de iniciar outros serviços.
   - Verifique se o serviço está registrado no painel do **Eureka**.
   - Confirme a configuração do cliente **Eureka** no `application.properties`.

2. **Conexão Recusada** 🚫
   - Verifique se o serviço de destino está em execução.
   - Confirme se a porta não está ocupada.
   - Certifique-se de que as configurações do **firewall** permitem a conexão.

🚀 **Agora você está pronto para rodar o Accenture Store Microservice!**

