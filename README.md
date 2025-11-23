# 🚀 ViewIn API Gateway - Backend

API Gateway do **ViewIn** - Sistema de Entrevistas com IA. Backend desenvolvido com Spring Boot que atua como ponto central de comunicação entre o frontend, banco de dados Oracle e serviços AWS (Lambda, S3).

---

## 📋 Sobre o Projeto

Este backend funciona como **API Gateway** do sistema ViewIn, gerenciando:

- 🔐 **Autenticação e Autorização** - Sistema JWT para gerenciamento de usuários
- 📊 **Gerenciamento de Job Reports** - Criação e acompanhamento de relatórios de entrevistas
- 🔄 **Orquestração de Serviços AWS** - Integração com Lambda, S3 e outros serviços
- 📡 **Callbacks** - Recebimento de notificações das Lambdas AWS
- 🗄️ **Persistência de Dados** - Acesso direto ao Oracle Database via JDBC

---

## 🛠️ Tecnologias Utilizadas

### Core
- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Security** - Segurança e autenticação
- **Maven** - Gerenciamento de dependências

### Banco de Dados
- **Oracle Database 12c+** - Banco de dados relacional
- **JDBC** - Acesso direto ao banco (sem JPA/Hibernate)

### Segurança
- **JWT (JSON Web Tokens)** - Autenticação stateless
- **BCrypt** - Criptografia de senhas

### Integrações
- **AWS SDK** - Integração com serviços AWS
  - **S3** - Armazenamento de áudios e relatórios
  - **Lambda** - Invocação de funções serverless

---

## 📦 Pré-requisitos

Antes de começar, certifique-se de ter instalado:

1. **Java 17+**
   - Verifique: `java -version`
   - Download: [Oracle JDK](https://www.oracle.com/java/technologies/downloads/#java17) ou [OpenJDK](https://adoptium.net/)

2. **Maven 3.6+**
   - Verifique: `mvn -version`
   - Download: [Apache Maven](https://maven.apache.org/download.cgi)

3. **Oracle Database 12c+**
   - Acesso ao banco Oracle da FIAP ou instalação local
   - Host: `oracle.fiap.com.br` (FIAP) ou `localhost`
   - Port: `1521`
   - SID: `ORCL`

4. **ngrok** (ESSENCIAL, para expor backend localmente)
   - Download: [ngrok](https://ngrok.com/download)

---

## 🗄️ Configuração do Banco de Dados

### Passo 1: Executar Scripts SQL

Execute os scripts SQL na ordem abaixo para criar as tabelas:

```bash
cd src/main/resources/sql
```

**Opção A: Criar banco do zero (recomendado)**
```sql
sqlplus RM554589/020106@oracle.fiap.com.br:1521/ORCL @init_database.sql
```

**Opção B: Criar apenas as tabelas**
```sql
sqlplus RM554589/020106@oracle.fiap.com.br:1521/ORCL @create_tables.sql
```

### Passo 2: Verificar Estrutura

```sql
-- Verificar tabelas criadas
SELECT table_name FROM user_tables ORDER BY table_name;

-- Verificar estrutura
DESC USERS;
DESC JOB_REPORT;
DESC AUDIO_FILES;
```

### Tabelas do Sistema

1. **USERS** - Usuários do sistema
2. **JOB_REPORT** - Relatórios de entrevistas
3. **AUDIO_FILES** - Arquivos de áudio das entrevistas

---

## ⚙️ Configuração

### 1. Configurar `application.properties`

Edite o arquivo `src/main/resources/application.properties`:

```properties
# Configuração do servidor
server.port=8080

# Configuração JWT
jwt.secret=suaChaveSecretaSuperSegura123
jwt.expiration=86400000  # 24 horas em milissegundos

# Oracle Database Configuration
oracle.host=oracle.fiap.com.br
oracle.port=1521
oracle.sid=ORCL
oracle.username=SEU_RM
oracle.password=SUA_SENHA

# AWS Configuration
aws.region=us-east-1
s3.bucket.name=interview-ai-assets

# Lambda URLs (configure com suas URLs)
lambda.url=https://sua-lambda-url.lambda-url.us-east-1.on.aws/
lambda.presigned.url=https://sua-lambda-url.lambda-url.us-east-1.on.aws/
lambda.upload.urls=https://sua-lambda-url.lambda-url.us-east-1.on.aws/
lambda.check.report.url=https://sua-lambda-url.lambda-url.us-east-1.on.aws/

# Backend Public URL (ngrok - será atualizado automaticamente)
backend.public.url=https://sua-url-ngrok.ngrok-free.dev
```

**⚠️ Importante:**
- Substitua `SEU_RM` e `SUA_SENHA` pelas suas credenciais do Oracle
- Em produção, use variáveis de ambiente para credenciais sensíveis
- Configure as URLs das Lambdas AWS conforme sua infraestrutura

### 2. Configurar AWS Credentials (Opcional)

Para integração completa com AWS, configure as credenciais:

**Opção A: Variáveis de Ambiente**
```bash
export AWS_ACCESS_KEY_ID=sua_access_key
export AWS_SECRET_ACCESS_KEY=sua_secret_key
export AWS_REGION=us-east-1
```

**Opção B: Arquivo de Credenciais**
```bash
~/.aws/credentials
```

---

## 🚀 Instalação e Execução

### Passo 1: Compilar o Projeto

No diretório raiz do projeto (`backend-gs`), execute:

```bash
mvn clean install
```

Este comando irá:
- Baixar todas as dependências
- Compilar o código
- Executar os testes (se houver)

### Passo 2: Executar a Aplicação

```bash
mvn spring-boot:run
```

Ou se preferir, execute o JAR compilado:

```bash
java -jar target/backend-gs-1.0.0.jar
```

### Passo 3: Verificar se Está Rodando

A aplicação estará disponível em: `http://localhost:8080`

Teste se está funcionando:

```bash
curl http://localhost:8080/api/auth/register
```

Se retornar um erro de validação (esperado), significa que a API está funcionando!

---

## 📡 Endpoints da API

### 🔐 Autenticação

#### 1. Registrar Novo Usuário

**Endpoint:** `POST /api/auth/register`

**Headers:**
```
Content-Type: application/json
```

**Body:**
```json
{
  "username": "usuario",
  "email": "usuario@email.com",
  "password": "senha123"
}
```

**Resposta de Sucesso (201):**
```json
{
  "message": "Usuário registrado com sucesso",
  "success": true,
  "userId": 1,
  "username": "usuario",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### 2. Fazer Login

**Endpoint:** `POST /api/auth/login`

**Headers:**
```
Content-Type: application/json
```

**Body:**
```json
{
  "username": "usuario",
  "password": "senha123"
}
```

**Resposta de Sucesso (200):**
```json
{
  "message": "Login realizado com sucesso",
  "success": true,
  "userId": 1,
  "username": "usuario",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### 3. Acessar Perfil (Protegido)

**Endpoint:** `GET /api/user/profile`

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Resposta de Sucesso (200):**
```json
{
  "message": "Perfil carregado com sucesso",
  "success": true,
  "userId": 1,
  "username": "usuario"
}
```

### 📊 Job Reports

#### 1. Criar Job Report

**Endpoint:** `POST /api/jobReport/create`

**Headers:**
```
Content-Type: application/json
Authorization: Bearer <token>
```

**Body:**
```json
{
  "company": "FIAP",
  "title": "Desenvolvedor Java",
  "description": "Vaga para desenvolvedor Java sênior com experiência em Spring Boot",
  "callbackUrl": "http://localhost:8080/api/jobReport/callback/audios-ready"
}
```

**Resposta de Sucesso (201):**
```json
{
  "id": 1,
  "company": "FIAP",
  "title": "Desenvolvedor Java",
  "status": "PENDING",
  "createdAt": "2024-01-15T10:30:00"
}
```

#### 2. Verificar Status do Job Report

**Endpoint:** `GET /api/jobReport/status/{id}`

**Headers:**
```
Authorization: Bearer <token> (opcional)
```

**Resposta de Sucesso (200):**
```json
{
  "id": 1,
  "status": "COMPLETED",
  "sessionId": "session-123",
  "reportUrl": "https://s3.amazonaws.com/bucket/report.pdf",
  "audioUrls": [
    {
      "url": "https://s3.amazonaws.com/bucket/audio1.mp3",
      "presignedUrl": "https://s3.amazonaws.com/bucket/audio1.mp3?signature=..."
    }
  ]
}
```

#### 3. Gerar URLs de Upload

**Endpoint:** `POST /api/jobReport/generate-upload-urls`

**Headers:**
```
Content-Type: application/json
```

**Body:**
```json
{
  "jobReportId": 1,
  "numQuestions": 5,
  "callbackUrl": "http://localhost:8080/api/jobReport/callback/audios-ready"
}
```

**Resposta de Sucesso (200):**
```json
{
  "sessionId": "session-123",
  "uploadUrls": [
    {
      "url": "https://s3.amazonaws.com/bucket/upload1.mp3",
      "presignedUrl": "https://s3.amazonaws.com/bucket/upload1.mp3?signature=...",
      "questionNumber": 1
    }
  ]
}
```

#### 4. Obter Presigned URL para Upload

**Endpoint:** `POST /api/jobReport/presigned-upload-url`

**Headers:**
```
Content-Type: application/json
```

**Body:**
```json
{
  "sessionId": "session-123",
  "filename": "resposta_1.mp3"
}
```

**Resposta de Sucesso (200):**
```json
{
  "url": "https://s3.amazonaws.com/bucket/resposta_1.mp3",
  "presignedUrl": "https://s3.amazonaws.com/bucket/resposta_1.mp3?signature=..."
}
```

#### 5. Obter Presigned URLs dos Áudios

**Endpoint:** `GET /api/jobReport/audios/{id}/presigned-urls`

**Resposta de Sucesso (200):**
```json
[
  {
    "url": "https://s3.amazonaws.com/bucket/audio1.mp3",
    "presignedUrl": "https://s3.amazonaws.com/bucket/audio1.mp3?signature=..."
  }
]
```

### 🔔 Callbacks (Chamados pelas Lambdas AWS)

#### 1. Callback: Áudios Prontos

**Endpoint:** `POST /api/jobReport/callback/audios-ready`

**Body:**
```json
{
  "jobReportId": 1,
  "sessionId": "session-123",
  "audioFiles": [
    {
      "path": "s3://bucket/audio1.mp3",
      "questionNumber": 1
    }
  ]
}
```

#### 2. Callback: Relatório Pronto

**Endpoint:** `POST /api/jobReport/callback/report-ready`

**Body:**
```json
{
  "jobReportId": 1,
  "sessionId": "session-123",
  "reportUrl": "https://s3.amazonaws.com/bucket/report.pdf"
}
```

---

## 🧪 Testando a API

### Usando cURL

#### 1. Registrar um usuário:
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "teste",
    "email": "teste@email.com",
    "password": "senha123"
  }'
```

#### 2. Fazer login:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "teste",
    "password": "senha123"
  }'
```

**Copie o token retornado** e use no próximo comando.

#### 3. Acessar perfil (substitua TOKEN pelo token recebido):
```bash
curl -X GET http://localhost:8080/api/user/profile \
  -H "Authorization: Bearer TOKEN"
```

#### 4. Criar Job Report:
```bash
curl -X POST http://localhost:8080/api/jobReport/create \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN" \
  -d '{
    "company": "FIAP",
    "title": "Desenvolvedor Java",
    "description": "Vaga para desenvolvedor Java sênior",
    "callbackUrl": "http://localhost:8080/api/jobReport/callback/audios-ready"
  }'
```

#### 5. Verificar status:
```bash
curl -X GET http://localhost:8080/api/jobReport/status/1
```

### Usando Postman

1. **Criar uma nova requisição POST** para `http://localhost:8080/api/auth/register`
2. Na aba **Body**, selecione **raw** e **JSON**
3. Cole o JSON de exemplo acima
4. Clique em **Send**
5. **Copie o token** da resposta
6. Para testar endpoints protegidos:
   - Crie uma nova requisição
   - Na aba **Authorization**, selecione **Bearer Token**
   - Cole o token copiado
   - Clique em **Send**

---

## 📁 Estrutura do Projeto

```
src/main/java/com/backend/gs/
├── BackendGsApplication.java          # Classe principal
│
├── config/                            # Configurações
│   ├── SecurityConfig.java            # Configuração de segurança e JWT
│   ├── JwtProperties.java             # Propriedades do JWT
│   └── PublicEndpointAuthenticationEntryPoint.java
│
├── controller/                        # Controllers REST
│   ├── AuthController.java            # Endpoints de autenticação
│   ├── UserController.java            # Endpoints de usuário
│   └── JobReportController.java       # Endpoints de job reports
│
├── dao/                               # Data Access Objects (JDBC)
│   ├── UserDao.java                   # Acesso a dados de usuários
│   ├── JobReportDao.java              # Acesso a dados de job reports
│   └── AudioFileDao.java              # Acesso a dados de áudios
│
├── database/                          # Conexão com banco
│   └── OracleConnection.java           # Gerenciamento de conexão Oracle
│
├── dto/                               # Data Transfer Objects
│   ├── AuthResponse.java              # Resposta de autenticação
│   ├── LoginRequest.java              # DTO de login
│   ├── RegisterRequest.java           # DTO de registro
│   ├── JobReportRequest.java          # DTO de criação de job report
│   ├── JobReportResponse.java         # DTO de resposta de job report
│   ├── JobReportStatusResponse.java   # DTO de status
│   ├── AudiosReadyCallback.java       # DTO de callback de áudios
│   ├── ReportReadyCallback.java       # DTO de callback de relatório
│   └── ...
│
├── filter/                            # Filtros
│   └── JwtAuthenticationFilter.java    # Filtro JWT
│
├── model/                             # Entidades
│   ├── User.java                      # Entidade Usuário
│   ├── JobReport.java                 # Entidade Job Report
│   └── AudioFile.java                 # Entidade Audio File
│
├── service/                           # Lógica de negócio
│   ├── AuthService.java               # Serviço de autenticação
│   ├── JwtService.java                # Serviço JWT
│   ├── JobReportService.java          # Serviço de job reports
│   └── S3Service.java                 # Serviço de integração S3
│
└── utils/                             # Utilitários
    └── JobInfoUtil.java               # Utilitários de job info

src/main/resources/
├── application.properties             # Configurações da aplicação
└── sql/                               # Scripts SQL
    ├── init_database.sql              # Inicialização completa
    ├── create_tables.sql              # Criação de tabelas
    ├── drop_tables.sql                # Remoção de tabelas
    └── ...
```

---

## 🔐 Segurança

- **Senhas** são criptografadas usando BCrypt
- **Tokens JWT** são assinados com HMAC SHA-256
- **Validação** de dados de entrada com Bean Validation
- **Autenticação stateless** (sem sessões no servidor)
- **CORS** configurado para permitir requisições do frontend
- **Endpoints protegidos** requerem token JWT válido

---

## 🔄 Integração com AWS

### Fluxo de Trabalho

1. **Frontend** → Cria Job Report via API Gateway
2. **API Gateway** → Invoca Lambda para gerar perguntas
3. **Lambda** → Gera áudios e armazena no S3
4. **Lambda** → Chama callback `/api/jobReport/callback/audios-ready`
5. **API Gateway** → Atualiza status no banco Oracle
6. **Lambda** → Processa respostas e gera relatório
7. **Lambda** → Chama callback `/api/jobReport/callback/report-ready`
8. **API Gateway** → Atualiza URL do relatório no banco

### Serviços AWS Utilizados

- **AWS Lambda** - Processamento serverless
- **AWS S3** - Armazenamento de áudios e relatórios
- **AWS SDK** - Integração via Java SDK

---

## 🐛 Solução de Problemas

### Erro: "Connection refused" ao conectar ao Oracle

**Solução:** Verifique se o Oracle está acessível:

```bash
# Teste a conexão manualmente
sqlplus RM554589/020106@oracle.fiap.com.br:1521/ORCL
```

### Erro: "password authentication failed"

**Solução:** Verifique as credenciais no `application.properties` e certifique-se de que a senha está correta.

### Erro: "database does not exist" ou "table does not exist"

**Solução:** Execute os scripts SQL conforme instruções acima:

```sql
sqlplus RM554589/020106@oracle.fiap.com.br:1521/ORCL @init_database.sql
```

### Erro ao compilar: "Could not resolve dependencies"

**Solução:** Limpe o cache do Maven e baixe novamente:

```bash
mvn clean install -U
```

### Erro: "CORS policy" no frontend

**Solução:** Verifique se o CORS está configurado corretamente no `SecurityConfig.java`. O backend já está configurado para aceitar requisições de qualquer origem (`@CrossOrigin(origins = "*")`).

### Erro ao conectar com AWS

**Solução:** 
1. Verifique se as credenciais AWS estão configuradas
2. Verifique se a região está correta no `application.properties`
3. Verifique se as permissões IAM estão corretas

---

## 📝 Notas Importantes

- ⚠️ **Nunca commite** o arquivo `application.properties` com credenciais reais
- 🔒 Use variáveis de ambiente ou arquivos `.env` para produção
- 🗄️ Sempre faça backup antes de executar scripts de migração
- 📦 O projeto usa **JDBC puro** (não JPA/Hibernate) conforme requisitos acadêmicos
- 🔄 Os callbacks das Lambdas devem ser acessíveis publicamente (use ngrok em desenvolvimento)

---

## 🎓 Critérios Acadêmicos Atendidos

✅ **Backend Java com POO** - Classes com encapsulamento, herança e polimorfismo  
✅ **Uso de JDBC** - Acesso direto ao banco com PreparedStatement  
✅ **Banco relacional com CRUD completo** - Todas as entidades têm operações CRUD  
✅ **3 entidades com relacionamentos** - User, JobReport, AudioFile com relacionamentos 1:N  

---

## 📚 Documentação Adicional

- **README Principal**: Ver `../README.md` na raiz do projeto
- **Frontend README**: Ver `../viewin-frontend/README.md`
- **Diagrama de Classes UML**: Ver `../UML.png` na raiz do projeto
- **Diagrama ERD**: Ver `../ERD.png` na raiz do projeto
- **Scripts SQL**: Ver `src/main/resources/sql/`

---

## 📞 Suporte

Em caso de dúvidas ou problemas:

- **Lucas Cabral** - RM554589
- **Thiago Barros** - RM555485
- **Yuri Lopes** - RM555522

---

## 📄 Licença

Este projeto foi desenvolvido para fins acadêmicos.

---

**Se prepare para o futuro do trabalho. View:In** 🚀
