# Configuração do Ngrok para o Backend

Este guia explica como usar o ngrok para expor o backend localmente através de uma URL pública.

## 📋 Pré-requisitos

- Ngrok instalado (já instalado via Homebrew)
- Backend rodando na porta 8080

## 🚀 Como Usar

### Iniciar o Ngrok

Execute o comando diretamente no terminal:

```bash
ngrok http 8080
```

Isso abrirá o ngrok e exibirá a URL pública no terminal. Você verá algo como:

```
Forwarding  https://abc123.ngrok-free.app -> http://localhost:8080
```

**Copie a URL HTTPS** e use-a no frontend ou para testes externos.

### Executar em Background (Opcional)

Se quiser executar o ngrok em background:

```bash
ngrok http 8080 --log=stdout > logs/ngrok.log 2>&1 &
```

Para parar, encontre o processo e encerre:

```bash
pkill ngrok
```

## 🔧 Configuração no Frontend

Após iniciar o ngrok, você terá uma URL pública como:
```
https://abc123.ngrok-free.app
```

Configure no frontend (arquivo `.env` ou variável de ambiente):

```env
VITE_API_GATEWAY_URL=https://abc123.ngrok-free.app/api
```

**⚠️ Importante:** A URL do ngrok muda a cada vez que você reinicia (a menos que tenha uma conta paga). Atualize a configuração do frontend sempre que reiniciar o ngrok.

## 🌐 Interface Web do Ngrok

Quando o ngrok estiver rodando, você pode acessar a interface web em:
```
http://localhost:4040
```

Lá você pode ver:
- URL pública atual
- Requisições em tempo real
- Estatísticas de uso

## 🔐 Autenticação do Ngrok (Conta Gratuita)

Se você tiver uma conta gratuita do ngrok, pode autenticar:

```bash
ngrok config add-authtoken SEU_TOKEN_AQUI
```

Isso permite:
- URLs mais estáveis
- Mais requisições por minuto
- Melhor performance

Para obter o token, acesse: https://dashboard.ngrok.com/get-started/your-authtoken

## 📝 Notas Importantes

1. **CORS já configurado**: O backend já está configurado para aceitar requisições de qualquer origem (incluindo ngrok)

2. **URLs temporárias**: URLs gratuitas do ngrok mudam a cada reinicialização

3. **Limites**: A versão gratuita tem limites de requisições por minuto

4. **Segurança**: URLs do ngrok são públicas. Não use em produção com dados sensíveis sem autenticação adequada

## 🐛 Solução de Problemas

### Erro: "ngrok not found"
```bash
brew install ngrok/ngrok/ngrok
```

### Erro: "port 8080 is already in use"
Certifique-se de que o backend está rodando:
```bash
mvn spring-boot:run
```

### CORS errors no frontend
Verifique se o CORS está configurado corretamente no `SecurityConfig.java` (já configurado)

### URL não funciona
1. Verifique se o backend está rodando na porta 8080
2. Verifique se o ngrok está ativo: `http://localhost:4040`
3. Teste a URL local primeiro: `http://localhost:8080/api/auth/register`

## 📚 Recursos

- [Documentação do Ngrok](https://ngrok.com/docs)
- [Dashboard do Ngrok](https://dashboard.ngrok.com)

