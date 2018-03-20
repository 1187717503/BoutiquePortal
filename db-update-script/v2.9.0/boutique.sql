ALTER TABLE `geography`
ADD COLUMN `pack_group`  int(2) NULL COMMENT '打包组id' AFTER `english_name`,
ADD COLUMN `pack_english_name`  varchar(255) NULL COMMENT '打包组英文名' AFTER `pack_group`;


UPDATE `geography` SET `pack_english_name`='China exl. Taiwan',pack_group` = 1 WHERE `name`='中国大陆' ;
UPDATE `geography` SET `pack_english_name`='China exl. Taiwan',pack_group` = 1 WHERE `name`='港澳地区' ;
UPDATE `geography` SET `pack_english_name`='European Union',pack_group` = 2 WHERE `name`='欧盟' ;
UPDATE `geography` SET `pack_english_name`='Asia',pack_group` = 3 WHERE `name`='亚洲' ;
UPDATE `geography` SET `pack_english_name`='Other',pack_group` = 4 WHERE `name`='其他地区' ;