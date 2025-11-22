-- ============================================
-- Script para Dropar Todas as Tabelas
-- ============================================
-- ⚠️  ATENÇÃO: Este script apaga TODOS os dados!
-- Execute apenas se quiser resetar o banco completamente

-- Dropar na ordem correta (devido às foreign keys)

-- 1. Dropar AUDIO_FILES (tem FK para JOB_REPORT)
BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE AUDIO_FILES CASCADE CONSTRAINTS';
   DBMS_OUTPUT.PUT_LINE('✅ Tabela AUDIO_FILES dropada');
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE = -942 THEN  -- ORA-00942: table or view does not exist
         DBMS_OUTPUT.PUT_LINE('⚠️  Tabela AUDIO_FILES não existe');
      ELSE
         RAISE;
      END IF;
END;
/

-- 2. Dropar JOB_REPORT (tem FK para USERS)
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

-- 3. Dropar USERS
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

COMMIT;

DBMS_OUTPUT.PUT_LINE('');
DBMS_OUTPUT.PUT_LINE('========================================');
DBMS_OUTPUT.PUT_LINE('✅ Todas as tabelas foram dropadas!');
DBMS_OUTPUT.PUT_LINE('========================================');

