-- 修复 admin 密码（须通过 mysql < fix-admin-password.sql 执行，勿在 shell 里直接粘贴含 $ 的 SQL）
-- 默认账号：admin / chaoge666
USE publish_article;

UPDATE admin_user
SET password = '$2b$10$Hk.mXCc3HNC4EJ1LPeDZTOwmv8ne.ATg8YW.h2hHZ8h4.fFLVK6R2'
WHERE username = 'admin';
