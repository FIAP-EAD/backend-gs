-- ============================================
-- Script para criar as tabelas do sistema
-- ViewIn - Sistema de Entrevistas com IA
-- ============================================
-- Execute este script para criar todas as tabelas necessárias
-- com seus relacionamentos e índices

-- ============================================
-- 1. CRIAR TABELA USERS
-- ============================================
-- Tabela de usuários do sistema
CREATE TABLE USERS (
   ID NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
   USERNAME VARCHAR2(50) NOT NULL UNIQUE,
   EMAIL VARCHAR2(100) NOT NULL UNIQUE,
   PASSWORD VARCHAR2(255) NOT NULL,
   CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- Índices para melhorar performance nas consultas
CREATE INDEX IDX_USERS_USERNAME ON USERS(USERNAME);
CREATE INDEX IDX_USERS_EMAIL ON USERS(EMAIL);

-- ============================================
-- 2. CRIAR TABELA JOB_REPORT
-- ============================================
-- Tabela de relatórios de vagas/entrevistas
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

-- Índices para melhorar performance
CREATE INDEX IDX_JOB_REPORT_USER ON JOB_REPORT(ID_USER);
CREATE INDEX IDX_JOB_REPORT_SESSION_ID ON JOB_REPORT(SESSION_ID);

-- ============================================
-- 3. CRIAR TABELA AUDIO_FILES
-- ============================================
-- Tabela de arquivos de áudio das entrevistas
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

-- Índice para melhorar performance nas consultas por ID_JOB_REPORT
CREATE INDEX IDX_AUDIO_FILES_JOB_REPORT ON AUDIO_FILES(ID_JOB_REPORT);

-- ============================================
-- RELACIONAMENTOS
-- ============================================
-- USERS 1:N JOB_REPORT (Um usuário pode ter vários relatórios)
-- JOB_REPORT 1:N AUDIO_FILES (Um relatório pode ter vários áudios)

COMMIT;

