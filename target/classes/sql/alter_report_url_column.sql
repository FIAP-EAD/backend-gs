-- Aumenta o tamanho da coluna REPORT_URL para suportar URLs pré-assinadas da AWS
-- URLs pré-assinadas podem ter até 2000+ caracteres devido aos tokens de segurança

ALTER TABLE JOB_REPORT 
MODIFY REPORT_URL VARCHAR2(2000);

-- Verifica a alteração
SELECT column_name, data_type, data_length 
FROM user_tab_columns 
WHERE table_name = 'JOB_REPORT' 
AND column_name = 'REPORT_URL';

