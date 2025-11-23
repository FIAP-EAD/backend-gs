-- ============================================
-- Script de Migração: Adicionar Relacionamento User → JobReport
-- ============================================
-- Execute este script se você já tem tabelas criadas
-- e precisa adicionar o relacionamento com USERS

-- ============================================
-- 1. ADICIONAR COLUNA ID_USER NA TABELA JOB_REPORT
-- ============================================
BEGIN
   EXECUTE IMMEDIATE 'ALTER TABLE JOB_REPORT ADD ID_USER NUMBER';
   DBMS_OUTPUT.PUT_LINE('✅ Coluna ID_USER adicionada com sucesso');
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE = -1430 THEN  -- ORA-01430: column being added already exists
         DBMS_OUTPUT.PUT_LINE('⚠️  Coluna ID_USER já existe');
      ELSE
         RAISE;
      END IF;
END;
/

-- ============================================
-- 2. ADICIONAR COLUNA REPORT_URL SE NÃO EXISTIR
-- ============================================
BEGIN
   EXECUTE IMMEDIATE 'ALTER TABLE JOB_REPORT ADD REPORT_URL VARCHAR2(2000)';
   DBMS_OUTPUT.PUT_LINE('✅ Coluna REPORT_URL adicionada com sucesso');
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE = -1430 THEN  -- ORA-01430: column being added already exists
         DBMS_OUTPUT.PUT_LINE('⚠️  Coluna REPORT_URL já existe');
      ELSE
         RAISE;
      END IF;
END;
/

-- ============================================
-- 3. ADICIONAR COLUNA CREATED_AT NA TABELA USERS SE NÃO EXISTIR
-- ============================================
BEGIN
   EXECUTE IMMEDIATE 'ALTER TABLE USERS ADD CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL';
   DBMS_OUTPUT.PUT_LINE('✅ Coluna CREATED_AT adicionada em USERS');
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE = -1430 THEN  -- ORA-01430: column being added already exists
         DBMS_OUTPUT.PUT_LINE('⚠️  Coluna CREATED_AT já existe em USERS');
      ELSE
         RAISE;
      END IF;
END;
/

-- ============================================
-- 4. ADICIONAR COLUNA CREATED_AT NA TABELA JOB_REPORT SE NÃO EXISTIR
-- ============================================
BEGIN
   EXECUTE IMMEDIATE 'ALTER TABLE JOB_REPORT ADD CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL';
   DBMS_OUTPUT.PUT_LINE('✅ Coluna CREATED_AT adicionada em JOB_REPORT');
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE = -1430 THEN  -- ORA-01430: column being added already exists
         DBMS_OUTPUT.PUT_LINE('⚠️  Coluna CREATED_AT já existe em JOB_REPORT');
      ELSE
         RAISE;
      END IF;
END;
/

-- ============================================
-- 5. ATUALIZAR REGISTROS EXISTENTES (OPCIONAL)
-- ============================================
-- Se você já tem registros em JOB_REPORT sem ID_USER,
-- você pode atribuir a um usuário padrão ou ao primeiro usuário
-- Descomente as linhas abaixo se necessário:

-- UPDATE JOB_REPORT 
-- SET ID_USER = (SELECT MIN(ID) FROM USERS)
-- WHERE ID_USER IS NULL;

-- ============================================
-- 6. TORNAR A COLUNA ID_USER OBRIGATÓRIA
-- ============================================
-- Após atualizar os registros existentes, torne a coluna NOT NULL
BEGIN
   EXECUTE IMMEDIATE 'ALTER TABLE JOB_REPORT MODIFY ID_USER NUMBER NOT NULL';
   DBMS_OUTPUT.PUT_LINE('✅ Coluna ID_USER definida como NOT NULL');
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE = -1442 THEN  -- ORA-01442: column to be modified to NOT NULL is already NOT NULL
         DBMS_OUTPUT.PUT_LINE('⚠️  Coluna ID_USER já é NOT NULL');
      ELSE
         DBMS_OUTPUT.PUT_LINE('❌ Erro: ' || SQLERRM);
         DBMS_OUTPUT.PUT_LINE('💡 Certifique-se de que todos os registros têm ID_USER preenchido');
      END IF;
END;
/

-- ============================================
-- 7. CRIAR CONSTRAINT DE FOREIGN KEY
-- ============================================
BEGIN
   EXECUTE IMMEDIATE 'ALTER TABLE JOB_REPORT ADD CONSTRAINT FK_JOB_REPORT_USER 
      FOREIGN KEY (ID_USER) REFERENCES USERS(ID) ON DELETE CASCADE';
   DBMS_OUTPUT.PUT_LINE('✅ Foreign Key FK_JOB_REPORT_USER criada com sucesso');
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE = -2275 THEN  -- ORA-02275: such a referential constraint already exists
         DBMS_OUTPUT.PUT_LINE('⚠️  Foreign Key FK_JOB_REPORT_USER já existe');
      ELSE
         RAISE;
      END IF;
END;
/

-- ============================================
-- 8. CRIAR ÍNDICE PARA MELHORAR PERFORMANCE
-- ============================================
BEGIN
   EXECUTE IMMEDIATE 'CREATE INDEX IDX_JOB_REPORT_USER ON JOB_REPORT(ID_USER)';
   DBMS_OUTPUT.PUT_LINE('✅ Índice IDX_JOB_REPORT_USER criado com sucesso');
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE = -955 THEN  -- ORA-00955: name is already used by an existing object
         DBMS_OUTPUT.PUT_LINE('⚠️  Índice IDX_JOB_REPORT_USER já existe');
      ELSE
         RAISE;
      END IF;
END;
/

COMMIT;

-- ============================================
-- VERIFICAÇÃO
-- ============================================
-- Execute estas queries para verificar:

-- Ver estrutura da tabela:
-- SELECT column_name, data_type, nullable FROM user_tab_columns WHERE table_name = 'JOB_REPORT';

-- Ver constraints:
-- SELECT constraint_name, constraint_type FROM user_constraints WHERE table_name = 'JOB_REPORT';

-- ============================================
-- PRÓXIMO PASSO
-- ============================================
-- Se você teve erro ao tornar ID_USER NOT NULL (porque há registros NULL),
-- execute o script: fix_user_relationship.sql

