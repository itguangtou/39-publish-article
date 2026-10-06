-- 默认管理员：admin / chaoge666
USE publish_article;

UPDATE admin_user
SET password = '$2b$10$Hk.mXCc3HNC4EJ1LPeDZTOwmv8ne.ATg8YW.h2hHZ8h4.fFLVK6R2'
WHERE username = 'admin';
