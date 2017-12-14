ALTER TABLE block ADD text_color CHAR(32) NULL;
ALTER TABLE block
  MODIFY COLUMN text_color CHAR(32) AFTER content;