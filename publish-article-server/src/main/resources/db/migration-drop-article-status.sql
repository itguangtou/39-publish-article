USE publish_article;

ALTER TABLE article DROP INDEX idx_article_status;
ALTER TABLE article DROP COLUMN status;
