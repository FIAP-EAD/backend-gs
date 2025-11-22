-- ============================================
-- Script Completo de Inicialização do Banco
-- ViewIn - Sistema de Entrevistas com IA
-- ============================================
-- Este script dropa e recria todas as tabelas do zero
-- ⚠️  ATENÇÃO: Todos os dados serão perdidos!

-- ============================================
-- PARTE 1: DROP TABLES
-- ============================================

-- Dropar tabelas na ordem correta (devido às foreign keys)
BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE AUDIO_FILES CASCADE CONSTRAINTS';
   DBMS_OUTPUT.PUT_LINE('✅ Tabela AUDIO_FILES dropada');
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE = -942 THEN
         DBMS_OUTPUT.PUT_LINE('⚠️  Tabela AUDIO_FILES não existe');
      ELSE
         RAISE;
      END IF;
END;
/

BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE JOB_REPORT CASCADE CONSTRAINTS';
   DBMS_OUTPUT.PUT_LINE('✅ Tabela JOB_REPORT dropada');
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE = -942 THEN
         DBMS_OUTPUT.PUT_LINE('⚠️  Tabela JOB_REPORT não existe');
      ELSE
         RAISE;
      END IF;
END;
/

BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE USERS CASCADE CONSTRAINTS';
   DBMS_OUTPUT.PUT_LINE('✅ Tabela USERS dropada');
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE = -942 THEN
         DBMS_OUTPUT.PUT_LINE('⚠️  Tabela USERS não existe');
      ELSE
         RAISE;
      END IF;
END;
/

-- ============================================
-- PARTE 2: CREATE TABLES
-- ============================================

-- 1. Criar tabela USERS
CREATE TABLE USERS (
   ID NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
   USERNAME VARCHAR2(50) NOT NULL UNIQUE,
   EMAIL VARCHAR2(100) NOT NULL UNIQUE,
   PASSWORD VARCHAR2(255) NOT NULL,
   CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

DBMS_OUTPUT.PUT_LINE('✅ Tabela USERS criada');

-- 2. Criar tabela JOB_REPORT
CREATE TABLE JOB_REPORT (
   ID_JOB_REPORT NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
   ID_USER NUMBER NOT NULL,
   COMPANY VARCHAR2(100) NOT NULL,
   TITLE VARCHAR2(150) NOT NULL,
   DESCRIPTION CLOB NOT NULL,
   SESSION_ID VARCHAR2(100),
   REPORT_URL VARCHAR2(2000),
   CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
   CONSTRAINT FK_JOB_REPORT_USER 
      FOREIGN KEY (ID_USER) 
      REFERENCES USERS(ID) 
      ON DELETE CASCADE
);

DBMS_OUTPUT.PUT_LINE('✅ Tabela JOB_REPORT criada');

-- 3. Criar tabela AUDIO_FILES
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

DBMS_OUTPUT.PUT_LINE('✅ Tabela AUDIO_FILES criada');

-- ============================================
-- PARTE 3: CREATE INDEXES
-- ============================================

-- Índices para USERS
CREATE INDEX IDX_USERS_USERNAME ON USERS(USERNAME);
CREATE INDEX IDX_USERS_EMAIL ON USERS(EMAIL);

-- Índices para JOB_REPORT
CREATE INDEX IDX_JOB_REPORT_USER ON JOB_REPORT(ID_USER);
CREATE INDEX IDX_JOB_REPORT_SESSION_ID ON JOB_REPORT(SESSION_ID);

-- Índices para AUDIO_FILES
CREATE INDEX IDX_AUDIO_FILES_JOB_REPORT ON AUDIO_FILES(ID_JOB_REPORT);

DBMS_OUTPUT.PUT_LINE('✅ Índices criados');

COMMIT;

-- ============================================
-- VERIFICAÇÃO
-- ============================================

DBMS_OUTPUT.PUT_LINE('');
DBMS_OUTPUT.PUT_LINE('========================================');
DBMS_OUTPUT.PUT_LINE('✅ Banco de dados inicializado!');
DBMS_OUTPUT.PUT_LINE('========================================');
DBMS_OUTPUT.PUT_LINE('');
DBMS_OUTPUT.PUT_LINE('Tabelas criadas:');
DBMS_OUTPUT.PUT_LINE('  1. USERS (usuários do sistema)');
DBMS_OUTPUT.PUT_LINE('  2. JOB_REPORT (relatórios de vagas)');
DBMS_OUTPUT.PUT_LINE('  3. AUDIO_FILES (arquivos de áudio)');
DBMS_OUTPUT.PUT_LINE('');
DBMS_OUTPUT.PUT_LINE('Relacionamentos:');
DBMS_OUTPUT.PUT_LINE('  USERS 1:N JOB_REPORT');
DBMS_OUTPUT.PUT_LINE('  JOB_REPORT 1:N AUDIO_FILES');

