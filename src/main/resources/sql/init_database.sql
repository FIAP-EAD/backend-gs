-- Script completo para inicializar o banco de dados
-- Este script combina drop e create para facilitar a inicialização

-- ============================================
-- PARTE 1: DROP TABLES
-- ============================================

-- Dropar tabelas se existirem (na ordem correta devido às foreign keys)
BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE AUDIO_FILES CASCADE CONSTRAINTS';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -942 THEN
         RAISE;
      END IF;
END;
/

BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE JOB_REPORT CASCADE CONSTRAINTS';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -942 THEN
         RAISE;
      END IF;
END;
/

BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE users CASCADE CONSTRAINTS';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -942 THEN
         RAISE;
      END IF;
END;
/

-- ============================================
-- PARTE 2: CREATE/ALTER TABLES
-- ============================================

-- Criar tabela USERS (autenticação)
BEGIN
   EXECUTE IMMEDIATE '
   CREATE TABLE users (
      id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
      username VARCHAR2(50) NOT NULL UNIQUE,
      email VARCHAR2(100) NOT NULL UNIQUE,
      password VARCHAR2(255) NOT NULL
   )';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -955 THEN  -- ORA-00955: name is already used by an existing object
         RAISE;
      END IF;
END;
/

-- Criar índices para USERS
BEGIN
   EXECUTE IMMEDIATE 'CREATE INDEX IDX_USERS_USERNAME ON users(username)';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -955 THEN
         RAISE;
      END IF;
END;
/

BEGIN
   EXECUTE IMMEDIATE 'CREATE INDEX IDX_USERS_EMAIL ON users(email)';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -955 THEN
         RAISE;
      END IF;
END;
/

-- Criar tabela JOB_REPORT se não existir
BEGIN
   EXECUTE IMMEDIATE '
   CREATE TABLE JOB_REPORT (
      ID_JOB_REPORT NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
      COMPANY VARCHAR2(100) NOT NULL,
      TITLE VARCHAR2(150) NOT NULL,
      DESCRIPTION CLOB NOT NULL,
      SESSION_ID VARCHAR2(100)
   )';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -955 THEN  -- ORA-00955: name is already used by an existing object
         RAISE;
      END IF;
END;
/

-- Adicionar coluna SESSION_ID na tabela JOB_REPORT se não existir
BEGIN
   EXECUTE IMMEDIATE 'ALTER TABLE JOB_REPORT ADD SESSION_ID VARCHAR2(100)';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -1430 THEN  -- ORA-01430: column being added already exists
         RAISE;
      END IF;
END;
/

-- Criar tabela AUDIO_FILES
CREATE TABLE AUDIO_FILES (
   ID_AUDIO_FILE NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
   ID_JOB_REPORT NUMBER NOT NULL,
   S3_PATH VARCHAR2(500) NOT NULL,
   FILE_NAME VARCHAR2(200) NOT NULL,
   CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
   CONSTRAINT FK_AUDIO_FILES_JOB_REPORT 
      FOREIGN KEY (ID_JOB_REPORT) 
      REFERENCES JOB_REPORT(ID_JOB_REPORT) 
      ON DELETE CASCADE
);

-- ============================================
-- PARTE 3: CREATE INDEXES
-- ============================================

-- Criar índice para melhorar performance nas consultas por ID_JOB_REPORT
BEGIN
   EXECUTE IMMEDIATE 'CREATE INDEX IDX_AUDIO_FILES_JOB_REPORT ON AUDIO_FILES(ID_JOB_REPORT)';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -955 THEN
         RAISE;
      END IF;
END;
/

-- Criar índice para melhorar performance nas consultas por SESSION_ID
BEGIN
   EXECUTE IMMEDIATE 'CREATE INDEX IDX_JOB_REPORT_SESSION_ID ON JOB_REPORT(SESSION_ID)';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -955 THEN
         RAISE;
      END IF;
END;
/

COMMIT;

-- Mensagem de sucesso
SELECT 'Database initialized successfully!' AS STATUS FROM DUAL;

