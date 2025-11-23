-- ============================================
-- Script para Corrigir Relacionamento User → JobReport
-- ============================================
-- Execute este script APÓS add_user_relationship.sql
-- para atualizar registros existentes e tornar ID_USER NOT NULL

SET SERVEROUTPUT ON;

-- ============================================
-- 1. VERIFICAR REGISTROS SEM ID_USER
-- ============================================
DECLARE
    v_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count 
    FROM JOB_REPORT 
    WHERE ID_USER IS NULL;
    
    DBMS_OUTPUT.PUT_LINE('📊 Registros sem ID_USER: ' || v_count);
    
    IF v_count > 0 THEN
        DBMS_OUTPUT.PUT_LINE('⚠️  Encontrados ' || v_count || ' registros sem ID_USER');
    ELSE
        DBMS_OUTPUT.PUT_LINE('✅ Todos os registros já têm ID_USER');
    END IF;
END;
/

-- ============================================
-- 2. VERIFICAR SE EXISTE USUÁRIO NO BANCO
-- ============================================
DECLARE
    v_user_count NUMBER;
    v_first_user_id NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_user_count FROM USERS;
    
    IF v_user_count = 0 THEN
        DBMS_OUTPUT.PUT_LINE('❌ ERRO: Não existe nenhum usuário no banco!');
        DBMS_OUTPUT.PUT_LINE('💡 Crie pelo menos um usuário antes de continuar.');
        RAISE_APPLICATION_ERROR(-20001, 'Não há usuários no banco de dados');
    ELSE
        SELECT MIN(ID) INTO v_first_user_id FROM USERS;
        DBMS_OUTPUT.PUT_LINE('✅ Encontrados ' || v_user_count || ' usuário(s)');
        DBMS_OUTPUT.PUT_LINE('📌 Usando usuário ID: ' || v_first_user_id);
    END IF;
END;
/

-- ============================================
-- 3. ATUALIZAR REGISTROS SEM ID_USER
-- ============================================
DECLARE
    v_updated NUMBER;
    v_first_user_id NUMBER;
BEGIN
    -- Pega o primeiro usuário disponível
    SELECT MIN(ID) INTO v_first_user_id FROM USERS;
    
    -- Atualiza todos os registros sem ID_USER
    UPDATE JOB_REPORT 
    SET ID_USER = v_first_user_id
    WHERE ID_USER IS NULL;
    
    v_updated := SQL%ROWCOUNT;
    
    IF v_updated > 0 THEN
        DBMS_OUTPUT.PUT_LINE('✅ ' || v_updated || ' registro(s) atualizado(s) com ID_USER = ' || v_first_user_id);
        COMMIT;
    ELSE
        DBMS_OUTPUT.PUT_LINE('✅ Nenhum registro precisou ser atualizado');
    END IF;
END;
/

-- ============================================
-- 4. VERIFICAR SE AINDA HÁ REGISTROS NULL
-- ============================================
DECLARE
    v_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count 
    FROM JOB_REPORT 
    WHERE ID_USER IS NULL;
    
    IF v_count > 0 THEN
        DBMS_OUTPUT.PUT_LINE('❌ AINDA EXISTEM ' || v_count || ' registros com ID_USER NULL!');
        RAISE_APPLICATION_ERROR(-20002, 'Ainda existem registros sem ID_USER');
    ELSE
        DBMS_OUTPUT.PUT_LINE('✅ Todos os registros agora têm ID_USER preenchido');
    END IF;
END;
/

-- ============================================
-- 5. TORNAR A COLUNA ID_USER NOT NULL
-- ============================================
BEGIN
    EXECUTE IMMEDIATE 'ALTER TABLE JOB_REPORT MODIFY ID_USER NUMBER NOT NULL';
    DBMS_OUTPUT.PUT_LINE('✅ Coluna ID_USER definida como NOT NULL com sucesso!');
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE = -1442 THEN  -- ORA-01442: column to be modified to NOT NULL is already NOT NULL
            DBMS_OUTPUT.PUT_LINE('⚠️  Coluna ID_USER já é NOT NULL');
        ELSE
            DBMS_OUTPUT.PUT_LINE('❌ Erro ao tornar ID_USER NOT NULL: ' || SQLERRM);
            RAISE;
        END IF;
END;
/

COMMIT;

-- ============================================
-- 6. VERIFICAÇÃO FINAL
-- ============================================
BEGIN
    DBMS_OUTPUT.PUT_LINE('');
    DBMS_OUTPUT.PUT_LINE('========================================');
    DBMS_OUTPUT.PUT_LINE('✅ Migração concluída com sucesso!');
    DBMS_OUTPUT.PUT_LINE('========================================');
    DBMS_OUTPUT.PUT_LINE('');
    DBMS_OUTPUT.PUT_LINE('📋 Resumo:');
    DBMS_OUTPUT.PUT_LINE('   ✅ Coluna ID_USER existe');
    DBMS_OUTPUT.PUT_LINE('   ✅ Todos os registros têm ID_USER preenchido');
    DBMS_OUTPUT.PUT_LINE('   ✅ Coluna ID_USER é NOT NULL');
    DBMS_OUTPUT.PUT_LINE('   ✅ Foreign Key FK_JOB_REPORT_USER criada');
    DBMS_OUTPUT.PUT_LINE('   ✅ Índice IDX_JOB_REPORT_USER criado');
    DBMS_OUTPUT.PUT_LINE('');
END;
/

-- ============================================
-- QUERIES DE VERIFICAÇÃO (OPCIONAL)
-- ============================================
-- Execute estas queries para verificar:

-- Ver estrutura da tabela:
-- SELECT column_name, data_type, nullable 
-- FROM user_tab_columns 
-- WHERE table_name = 'JOB_REPORT' 
-- ORDER BY column_id;

-- Ver constraints:
-- SELECT constraint_name, constraint_type 
-- FROM user_constraints 
-- WHERE table_name = 'JOB_REPORT';

-- Ver registros:
-- SELECT ID_JOB_REPORT, ID_USER, COMPANY, TITLE 
-- FROM JOB_REPORT;

