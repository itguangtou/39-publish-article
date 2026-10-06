-- 请用文件导入执行：Get-Content seed.sql | mysql -uroot -p
-- 勿在 PowerShell/cmd 里直接粘贴含 $ 的 INSERT，否则 BCrypt 会被截断
USE publish_article;

INSERT INTO admin_user (username, password, status)
SELECT 'admin', '$2b$10$Hk.mXCc3HNC4EJ1LPeDZTOwmv8ne.ATg8YW.h2hHZ8h4.fFLVK6R2', 1
WHERE NOT EXISTS (SELECT 1 FROM admin_user WHERE username = 'admin');

INSERT INTO system_setting (id, avatar_path, nickname, intro)
SELECT 1, NULL, '超哥评球', '爱足球，爱生活！'
WHERE NOT EXISTS (SELECT 1 FROM system_setting WHERE id = 1);
