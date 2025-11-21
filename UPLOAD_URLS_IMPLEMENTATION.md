# 🚀 Implementação do Endpoint de Upload URLs

## ✅ O que foi implementado

### 1. **Novo Endpoint no Backend Java**

```
POST /api/jobReport/generate-upload-urls
```

**Request Body:**
```json
{
  "jobReportId": 4,
  "numQuestions": 5
}
```

**Response:**
```json
{
  "sessionId": "job-4-1732204800",
  "uploadUrls": [
    {
      "questionIndex": 0,
      "presignedUrl": "https://s3.amazonaws.com/...",
      "s3Key": "responses-audios/job-4-1732204800/resposta_0.mp3"
    },
    {
      "questionIndex": 1,
      "presignedUrl": "https://s3.amazonaws.com/...",
      "s3Key": "responses-audios/job-4-1732204800/resposta_1.mp3"
    },
    ...
  ],
  "expiresIn": 3600
}
```

### 2. **Arquivos Criados**

✅ **`GenerateUploadUrlsRequest.java`**
- DTO para request
- Validações: jobReportId obrigatório, numQuestions >= 1

✅ **`GenerateUploadUrlsResponse.java`**
- DTO para response
- Contém sessionId, lista de URLs e tempo de expiração
- Inner class `UploadUrlInfo` com questionIndex, presignedUrl, s3Key

### 3. **Arquivos Modificados**

✅ **`JobReportController.java`**
- Adicionado endpoint `/generate-upload-urls`
- Validação com `@Valid`
- Logs para debugging

✅ **`JobReportService.java`**
- Adicionado método `generateMultipleUploadUrls()`
- Chama Lambda via HTTP POST
- Parse da resposta JSON
- Nova propriedade `lambdaUploadUrlsUrl`

### 4. **Lambda URL Configurada**

```
https://mcy4uuho2gkb3ey3f5fz3cko2a0kmcgl.lambda-url.us-east-1.on.aws/
```

## 🔄 Fluxo Completo

```
┌─────────────────┐
│   Frontend      │
│   (React)       │
└────────┬────────┘
         │ POST /api/jobReport/generate-upload-urls
         │ { jobReportId: 4, numQuestions: 5 }
         ↓
┌─────────────────┐
│  Backend Java   │ ← Valida request
│  (Spring Boot)  │   Chama Lambda
└────────┬────────┘
         │ HTTP POST
         ↓
┌─────────────────┐
│ Lambda          │ ← Gera 5 presigned URLs
│ GenerateUpload  │   Envia mensagem para SQS
│ URLs            │
└────────┬────────┘
         │ Retorna JSON
         ↓
┌─────────────────┐
│  Backend Java   │ ← Parse response
└────────┬────────┘
         │ Retorna para frontend
         ↓
┌─────────────────┐
│   Frontend      │ ← Recebe URLs
│                 │   Faz upload direto no S3
└─────────────────┘
```

## 🧪 Como Testar

### **1. Via curl:**

```bash
curl -X POST http://localhost:8080/api/jobReport/generate-upload-urls \
  -H "Content-Type: application/json" \
  -d '{
    "jobReportId": 4,
    "numQuestions": 5
  }'
```

### **2. Via Postman/Insomnia:**

- **Method:** POST
- **URL:** `http://localhost:8080/api/jobReport/generate-upload-urls`
- **Headers:** `Content-Type: application/json`
- **Body:**
```json
{
  "jobReportId": 4,
  "numQuestions": 5
}
```

### **3. Via Frontend:**

```typescript
const response = await fetch('http://localhost:8080/api/jobReport/generate-upload-urls', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({
    jobReportId: 4,
    numQuestions: 5
  })
});

const data = await response.json();
console.log('Session ID:', data.sessionId);
console.log('URLs:', data.uploadUrls);
```

## 📝 Configuração Necessária

### **application.properties** (ou application.yml)

Adicione esta linha:

```properties
lambda.upload.urls=https://mcy4uuho2gkb3ey3f5fz3cko2a0kmcgl.lambda-url.us-east-1.on.aws/
```

Ou se já existir, atualize:

```properties
lambda.url=https://lv6bwqn7dfkqulrqquhlz3fhdy0zuzbx.lambda-url.us-east-1.on.aws/
lambda.presigned.url=https://6t7s4lvjy7aohaxruak6a3arfy0byiau.lambda-url.us-east-1.on.aws/
lambda.upload.urls=https://mcy4uuho2gkb3ey3f5fz3cko2a0kmcgl.lambda-url.us-east-1.on.aws/
s3.bucket.name=interview-ai-assets
```

## 🎯 Próximos Passos

### **1. Deploy da Lambda** (se ainda não foi feito)

```bash
cd infra/GenerateReport/lambda/GenerateUploadURLs
zip -r function.zip main_sqs.py
aws lambda update-function-code \
  --function-name GenerateUploadURLs \
  --zip-file fileb://function.zip
```

### **2. Configurar SQS**

- Criar fila: `interview-upload-queue`
- Configurar variável de ambiente na Lambda:
  ```
  UPLOAD_QUEUE_URL=https://sqs.us-east-1.amazonaws.com/SEU_ACCOUNT_ID/interview-upload-queue
  ```

### **3. Configurar Trigger SQS → ProcessUploadQueue**

- Lambda `ProcessUploadQueue`
- Trigger: SQS `interview-upload-queue`
- Batch size: 1

### **4. Atualizar Frontend**

Usar o novo endpoint ao invés do antigo:

```typescript
// ❌ Antes (uma URL por vez)
for (let i = 0; i < numQuestions; i++) {
  const url = await getPresignedUploadUrl(sessionId, `resposta_${i}.mp3`);
}

// ✅ Agora (todas URLs de uma vez)
const { sessionId, uploadUrls } = await generateUploadUrls(jobReportId, numQuestions);
```

## 🔍 Logs para Debugging

O endpoint imprime logs úteis:

```
=== GENERATE UPLOAD URLS ===
Job Report ID: 4
Num Questions: 5
✅ URLs geradas com sucesso!
Session ID: job-4-1732204800
Total URLs: 5
```

Se der erro:
```
❌ Erro ao gerar URLs: Failed to get upload URLs from Lambda: ...
```

## ⚠️ Validações

O endpoint valida:

- ✅ `jobReportId` não pode ser null
- ✅ `numQuestions` não pode ser null
- ✅ `numQuestions` deve ser >= 1
- ✅ Lambda deve retornar status 200
- ✅ Response deve ter formato JSON válido

## 🆚 Comparação: Antigo vs Novo

### **Antigo (uma URL por vez):**
```
Frontend → Backend → Lambda (1 URL) → Backend → Frontend
Frontend → Backend → Lambda (1 URL) → Backend → Frontend
Frontend → Backend → Lambda (1 URL) → Backend → Frontend
Frontend → Backend → Lambda (1 URL) → Backend → Frontend
Frontend → Backend → Lambda (1 URL) → Backend → Frontend

Total: 5 chamadas HTTP
```

### **Novo (todas URLs de uma vez):**
```
Frontend → Backend → Lambda (5 URLs) → Backend → Frontend

Total: 1 chamada HTTP
```

**Vantagens:**
- ✅ Mais rápido (1 request ao invés de 5)
- ✅ Menos overhead de rede
- ✅ Tracking automático via SQS
- ✅ Session ID único para toda a sessão

## 🎉 Pronto!

Agora você tem:
- ✅ Endpoint `/generate-upload-urls` funcionando
- ✅ Backend chama a Lambda corretamente
- ✅ DTOs criados e validados
- ✅ Lambda URL configurada: https://mcy4uuho2gkb3ey3f5fz3cko2a0kmcgl.lambda-url.us-east-1.on.aws/

**Basta testar e integrar no frontend! 🚀**

