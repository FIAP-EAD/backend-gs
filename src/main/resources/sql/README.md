# Scripts SQL para Banco de Dados Oracle

Este diretório contém os scripts SQL necessários para criar e gerenciar as tabelas do sistema.

## Arquivos Disponíveis

### 1. `init_database.sql` (Recomendado)
Script completo que combina drop e create. Use este para inicializar o banco de dados do zero.

**Uso:**
```bash
sqlplus usuario/senha@database @init_database.sql
```

### 2. `drop_tables.sql`
Script para dropar as tabelas existentes. Use com cuidado!

**Uso:**
```bash
sqlplus usuario/senha@database @drop_tables.sql
```

### 3. `create_tables.sql`
Script para criar as tabelas. Use após dropar ou se as tabelas não existirem.

**Uso:**
```bash
sqlplus usuario/senha@database @create_tables.sql
```

### 4. `create_tables_oracle_legacy.sql`
Versão alternativa para Oracle versões anteriores ao 12c (que não suportam `GENERATED ALWAYS AS IDENTITY`). 
Usa SEQUENCES + TRIGGERS para auto-incremento.

**Uso:**
```bash
sqlplus usuario/senha@database @create_tables_oracle_legacy.sql
```

## Estrutura das Tabelas

### JOB_REPORT
Armazena informações sobre as descrições de vagas.

| Coluna | Tipo | Descrição |
|--------|------|-----------|
| ID_JOB_REPORT | NUMBER | Chave primária (auto-incremento) |
| COMPANY | VARCHAR2(100) | Nome da empresa |
| TITLE | VARCHAR2(150) | Título da vaga |
| DESCRIPTION | CLOB | Descrição da vaga |
| SESSION_ID | VARCHAR2(100) | ID da sessão AWS (UUID do roteiro) |

### AUDIO_FILES
Armazena informações sobre os arquivos de áudio gerados.

| Coluna | Tipo | Descrição |
|--------|------|-----------|
| ID_AUDIO_FILE | NUMBER | Chave primária (auto-incremento) |
| ID_JOB_REPORT | NUMBER | Chave estrangeira para JOB_REPORT |
| S3_PATH | VARCHAR2(500) | Caminho completo do arquivo no S3 (s3://bucket/key) |
| FILE_NAME | VARCHAR2(200) | Nome do arquivo |
| CREATED_AT | TIMESTAMP | Data/hora de criação |

## Índices Criados

- `IDX_AUDIO_FILES_JOB_REPORT`: Índice em `AUDIO_FILES(ID_JOB_REPORT)` para melhorar consultas por job report
- `IDX_JOB_REPORT_SESSION_ID`: Índice em `JOB_REPORT(SESSION_ID)` para melhorar consultas por session ID

## Notas Importantes

1. **Oracle 12c+**: Use `init_database.sql` ou `create_tables.sql` (usa `GENERATED ALWAYS AS IDENTITY`)
2. **Oracle 11g ou anterior**: Use `create_tables_oracle_legacy.sql` (usa SEQUENCES + TRIGGERS)
3. Os scripts incluem tratamento de erros para evitar falhas se as tabelas/índices já existirem
4. A tabela `JOB_REPORT` não é dropada automaticamente para preservar dados existentes
5. A foreign key em `AUDIO_FILES` usa `ON DELETE CASCADE`, então deletar um job report também deleta seus áudios

## Verificação

Após executar os scripts, você pode verificar se as tabelas foram criadas corretamente:

```sql
-- Verificar estrutura da tabela JOB_REPORT
DESC JOB_REPORT;

-- Verificar estrutura da tabela AUDIO_FILES
DESC AUDIO_FILES;

-- Verificar índices
SELECT INDEX_NAME, TABLE_NAME, COLUMN_NAME 
FROM USER_IND_COLUMNS 
WHERE TABLE_NAME IN ('JOB_REPORT', 'AUDIO_FILES')
ORDER BY TABLE_NAME, INDEX_NAME;
```

