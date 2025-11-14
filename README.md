# Sistema de Login com JWT - Spring Boot

Sistema completo de autenticação e registro de usuários desenvolvido com Spring Boot, utilizando PostgreSQL como banco de dados e JWT (JSON Web Tokens) para autenticação stateless.

## 🚀 Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **Spring Security**
- **PostgreSQL** (banco de dados)
- **JWT** (JSON Web Tokens)
- **Maven**

## ✨ Funcionalidades

- ✅ Registro de novos usuários
- ✅ Login de usuários com autenticação JWT
- ✅ Validação de dados
- ✅ Criptografia de senhas (BCrypt)
- ✅ Persistência no banco de dados PostgreSQL
- ✅ Autenticação stateless com JWT
- ✅ Endpoint protegido para perfil do usuário

## 📋 Pré-requisitos

Antes de começar, certifique-se de ter instalado:

1. **Java 17** ou superior
   - Verifique: `java -version`
   - Download: [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) ou [OpenJDK](https://adoptium.net/)

2. **Maven 3.6+**
   - Verifique: `mvn -version`
   - Download: [Apache Maven](https://maven.apache.org/download.cgi)

3. **PostgreSQL 12+**
   - Download: [PostgreSQL](https://www.postgresql.org/download/)
   - Certifique-se de que o PostgreSQL está rodando

4. **Postman** ou **cURL** (para testar a API)
   - Download Postman: [Postman](https://www.postman.com/downloads/)

## 🗄️ Configuração do Banco de Dados

### Passo 1: Instalar PostgreSQL

Se ainda não tiver o PostgreSQL instalado, baixe e instale a partir do site oficial.

### Passo 2: Criar o Banco de Dados

Abra o terminal/linha de comando e execute:

```bash
# Conectar ao PostgreSQL (use a senha que você configurou na instalação)
psql -U postgres

# Criar o banco de dados
CREATE DATABASE backend_gs;

# Sair do psql
\q
```

### Passo 3: Configurar Credenciais

Edite o arquivo `src/main/resources/application.properties` e ajuste as credenciais do PostgreSQL se necessário:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/backend_gs
spring.datasource.username=postgres
spring.datasource.password=sua_senha_aqui
```

**⚠️ Importante:** Substitua `sua_senha_aqui` pela senha do seu PostgreSQL.

## 🛠️ Instalação e Execução

### Passo 1: Clonar/Baixar o Projeto

Se você já tem o projeto, pule para o próximo passo.

### Passo 2: Compilar o Projeto

No diretório raiz do projeto, execute:

```bash
mvn clean install
```

Este comando irá:
- Baixar todas as dependências
- Compilar o código
- Executar os testes (se houver)

### Passo 3: Executar a Aplicação

Execute o projeto com Maven:

```bash
mvn spring-boot:run
```

Ou se preferir, execute o JAR compilado:

```bash
java -jar target/backend-gs-1.0.0.jar
```

### Passo 4: Verificar se Está Rodando

A aplicação estará disponível em: `http://localhost:8080`

Você pode testar se está funcionando acessando:
```bash
curl http://localhost:8080/api/auth/register
```

Se retornar um erro de validação (esperado), significa que a API está funcionando!

## 📡 Endpoints da API

### 1. Registrar Novo Usuário

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

**Resposta de Erro (400):**
```json
{
  "message": "Username já está em uso",
  "success": false
}
```

### 2. Fazer Login

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

**Resposta de Erro (401):**
```json
{
  "message": "Username ou senha inválidos",
  "success": false
}
```

### 3. Acessar Perfil (Protegido)

**Endpoint:** `GET /api/user/profile`

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Resposta de Sucesso (200):**
```json
{
  "message": "Perfil do usuário",
  "success": true,
  "username": "usuario"
}
```

**Resposta de Erro (401):**
```
Sem token ou token inválido
```

## 🧪 Testando a API

### Usando cURL

#### 1. Registrar um usuário:
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"teste\",\"email\":\"teste@email.com\",\"password\":\"senha123\"}"
```

#### 2. Fazer login:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"teste\",\"password\":\"senha123\"}"
```

**Copie o token retornado** e use no próximo comando.

#### 3. Acessar perfil (substitua TOKEN pelo token recebido):
```bash
curl -X GET http://localhost:8080/api/user/profile \
  -H "Authorization: Bearer TOKEN"
```

### Usando Postman

1. **Criar uma nova requisição POST** para `http://localhost:8080/api/auth/register`
2. Na aba **Body**, selecione **raw** e **JSON**
3. Cole o JSON de exemplo acima
4. Clique em **Send**
5. **Copie o token** da resposta
6. Para testar o endpoint protegido:
   - Crie uma nova requisição GET para `http://localhost:8080/api/user/profile`
   - Na aba **Authorization**, selecione **Bearer Token**
   - Cole o token copiado
   - Clique em **Send**

## 📁 Estrutura do Projeto

```
src/main/java/com/backend/gs/
├── BackendGsApplication.java    # Classe principal
├── config/
│   ├── SecurityConfig.java      # Configuração de segurança e JWT
│   └── JwtProperties.java       # Propriedades do JWT
├── controller/
│   ├── AuthController.java      # Endpoints de autenticação
│   └── UserController.java      # Endpoints protegidos
├── dto/
│   ├── AuthResponse.java        # Resposta de autenticação
│   ├── LoginRequest.java        # DTO de login
│   └── RegisterRequest.java     # DTO de registro
├── filter/
│   └── JwtAuthenticationFilter.java  # Filtro JWT
├── model/
│   └── User.java                # Entidade Usuário
├── repository/
│   └── UserRepository.java      # Repositório JPA
└── service/
    ├── AuthService.java         # Lógica de autenticação
    └── JwtService.java          # Serviço JWT
```

## 🔐 Segurança

- **Senhas** são criptografadas usando BCrypt
- **Tokens JWT** são assinados com HMAC SHA-256
- **Validação** de dados de entrada
- **Autenticação stateless** (sem sessões no servidor)

## ⚙️ Configurações

### application.properties

Principais configurações que você pode ajustar:

```properties
# Porta do servidor
server.port=8080

# Configurações do PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/backend_gs
spring.datasource.username=postgres
spring.datasource.password=postgres

# Configurações JWT
jwt.secret=sua_chave_secreta_aqui
jwt.expiration=86400000  # 24 horas em milissegundos
```

**⚠️ Importante:** Em produção, altere a `jwt.secret` para uma chave segura e longa!

## 🐛 Solução de Problemas

### Erro: "Connection refused" ao conectar ao PostgreSQL

**Solução:** Certifique-se de que o PostgreSQL está rodando:
```bash
# Windows
net start postgresql-x64-14

# Linux/Mac
sudo systemctl start postgresql
```

### Erro: "password authentication failed"

**Solução:** Verifique as credenciais no `application.properties` e certifique-se de que a senha está correta.

### Erro: "database does not exist"

**Solução:** Crie o banco de dados conforme instruções acima:
```sql
CREATE DATABASE backend_gs;
```

### Erro ao compilar: "Could not resolve dependencies"

**Solução:** Limpe o cache do Maven e baixe novamente:
```bash
mvn clean install -U
```

## 📝 Próximos Passos

Para melhorar ainda mais o sistema, considere:

- [ ] Implementar refresh tokens
- [ ] Adicionar rate limiting
- [ ] Implementar logout (blacklist de tokens)
- [ ] Adicionar recuperação de senha
- [ ] Implementar verificação de email
- [ ] Adicionar testes unitários e de integração
- [ ] Implementar roles/permissões (ADMIN, USER, etc.)
- [ ] Adicionar logs de auditoria
- [ ] Configurar CORS adequadamente para produção

## 📄 Licença

Este projeto é de código aberto e está disponível para uso livre.

## 👨‍💻 Autor

Desenvolvido com Spring Boot e JWT.

---

**Dúvidas?** Abra uma issue ou entre em contato!
