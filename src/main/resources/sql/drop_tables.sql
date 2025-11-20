-- Script para dropar tabelas se existirem
-- Execute este script antes de criar as tabelas

-- Dropar tabela AUDIO_FILES se existir
BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE AUDIO_FILES CASCADE CONSTRAINTS';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -942 THEN
         RAISE;
      END IF;
END;
/

-- Dropar coluna SESSION_ID da tabela JOB_REPORT se existir
BEGIN
   EXECUTE IMMEDIATE 'ALTER TABLE JOB_REPORT DROP COLUMN SESSION_ID';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -904 THEN
         RAISE;
      END IF;
END;
/

-- Nota: A tabela JOB_REPORT não é dropada aqui para preservar dados existentes
-- Se precisar dropar completamente, descomente as linhas abaixo:
-- BEGIN
--    EXECUTE IMMEDIATE 'DROP TABLE JOB_REPORT CASCADE CONSTRAINTS';
-- EXCEPTION
--    WHEN OTHERS THEN
--       IF SQLCODE != -942 THEN
--          RAISE;
--       END IF;
-- END;
-- /

COMMIT;

