-- Script para criar tabelas em Oracle versões anteriores ao 12c
-- Use este script se GENERATED ALWAYS AS IDENTITY não funcionar

-- ============================================
-- PARTE 1: DROP SEQUENCES E TABLES
-- ============================================

-- Dropar sequências se existirem
BEGIN
   EXECUTE IMMEDIATE 'DROP SEQUENCE SEQ_JOB_REPORT';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -2289 THEN
         RAISE;
      END IF;
END;
/

BEGIN
   EXECUTE IMMEDIATE 'DROP SEQUENCE SEQ_AUDIO_FILES';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -2289 THEN
         RAISE;
      END IF;
END;
/

-- Dropar tabelas se existirem
BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE AUDIO_FILES CASCADE CONSTRAINTS';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -942 THEN
         RAISE;
      END IF;
END;
/

-- ============================================
-- PARTE 2: CREATE SEQUENCES
-- ============================================

CREATE SEQUENCE SEQ_JOB_REPORT
   START WITH 1
   INCREMENT BY 1
   NOCACHE
   NOCYCLE;

CREATE SEQUENCE SEQ_AUDIO_FILES
   START WITH 1
   INCREMENT BY 1
   NOCACHE
   NOCYCLE;

-- ============================================
-- PARTE 3: CREATE TABLES
-- ============================================

-- Criar tabela JOB_REPORT se não existir
BEGIN
   EXECUTE IMMEDIATE '
   CREATE TABLE JOB_REPORT (
      ID_JOB_REPORT NUMBER PRIMARY KEY,
      COMPANY VARCHAR2(100) NOT NULL,
      TITLE VARCHAR2(150) NOT NULL,
      DESCRIPTION CLOB NOT NULL,
      SESSION_ID VARCHAR2(100)
   )';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -955 THEN
         RAISE;
      END IF;
END;
/

-- Adicionar coluna SESSION_ID se não existir
BEGIN
   EXECUTE IMMEDIATE 'ALTER TABLE JOB_REPORT ADD SESSION_ID VARCHAR2(100)';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -1430 THEN
         RAISE;
      END IF;
END;
/

-- Criar tabela AUDIO_FILES
CREATE TABLE AUDIO_FILES (
   ID_AUDIO_FILE NUMBER PRIMARY KEY,
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
-- PARTE 4: CREATE TRIGGERS
-- ============================================

-- Trigger para auto-incrementar ID_JOB_REPORT
CREATE OR REPLACE TRIGGER TRG_JOB_REPORT_ID
   BEFORE INSERT ON JOB_REPORT
   FOR EACH ROW
BEGIN
   IF :NEW.ID_JOB_REPORT IS NULL THEN
      SELECT SEQ_JOB_REPORT.NEXTVAL INTO :NEW.ID_JOB_REPORT FROM DUAL;
   END IF;
END;
/

-- Trigger para auto-incrementar ID_AUDIO_FILE
CREATE OR REPLACE TRIGGER TRG_AUDIO_FILES_ID
   BEFORE INSERT ON AUDIO_FILES
   FOR EACH ROW
BEGIN
   IF :NEW.ID_AUDIO_FILE IS NULL THEN
      SELECT SEQ_AUDIO_FILES.NEXTVAL INTO :NEW.ID_AUDIO_FILE FROM DUAL;
   END IF;
END;
/

-- ============================================
-- PARTE 5: CREATE INDEXES
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
SELECT 'Database initialized successfully (Legacy Oracle version)!' AS STATUS FROM DUAL;

