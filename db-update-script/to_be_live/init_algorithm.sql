

delete from im_price_algorithm;

delete from im_price_algorithm_rule;

delete from im_price_algorithm_rule_brand;

delete from im_price_discount_mapping;

delete from im_price_discount_model;


-- adult
insert into im_price_algorithm(`category_type`,`season_code`,`name`,`created_at`,`updated_at`,`enabled`)
values(1,'17FW','日常',now(),now(),1);
insert into im_price_algorithm(`category_type`,`season_code`,`name`,`created_at`,`updated_at`,`enabled`)
values(1,'17SS','日常',now(),now(),1);
insert into im_price_algorithm(`category_type`,`season_code`,`name`,`created_at`,`updated_at`,`enabled`)
values(1,'18FW','日常',now(),now(),1);
insert into im_price_algorithm(`category_type`,`season_code`,`name`,`created_at`,`updated_at`,`enabled`)
values(1,'18SS','日常',now(),now(),1);
insert into im_price_algorithm(`category_type`,`season_code`,`name`,`created_at`,`updated_at`,`enabled`)
values(1,'CarryOver','日常',now(),now(),1);
insert into im_price_algorithm(`category_type`,`season_code`,`name`,`created_at`,`updated_at`,`enabled`)
values(3,'爆款','日常',now(),now(),1);

insert into im_price_algorithm(`category_type`,`season_code`,`name`,`created_at`,`updated_at`,`enabled`)
values(1,'17FW','大促',now(),now(),1);
insert into im_price_algorithm(`category_type`,`season_code`,`name`,`created_at`,`updated_at`,`enabled`)
values(1,'17SS','大促',now(),now(),1);
insert into im_price_algorithm(`category_type`,`season_code`,`name`,`created_at`,`updated_at`,`enabled`)
values(1,'18FW','大促',now(),now(),1);
insert into im_price_algorithm(`category_type`,`season_code`,`name`,`created_at`,`updated_at`,`enabled`)
values(1,'18SS','大促',now(),now(),1);
insert into im_price_algorithm(`category_type`,`season_code`,`name`,`created_at`,`updated_at`,`enabled`)
values(1,'CarryOver','大促',now(),now(),1);

insert into im_price_algorithm(`category_type`,`season_code`,`name`,`created_at`,`updated_at`,`enabled`)
values(1,'17FW','小促',now(),now(),1);
insert into im_price_algorithm(`category_type`,`season_code`,`name`,`created_at`,`updated_at`,`enabled`)
values(1,'17SS','小促',now(),now(),1);
insert into im_price_algorithm(`category_type`,`season_code`,`name`,`created_at`,`updated_at`,`enabled`)
values(1,'18FW','小促',now(),now(),1);
insert into im_price_algorithm(`category_type`,`season_code`,`name`,`created_at`,`updated_at`,`enabled`)
values(1,'18SS','小促',now(),now(),1);
insert into im_price_algorithm(`category_type`,`season_code`,`name`,`created_at`,`updated_at`,`enabled`)
values(1,'CarryOver','小促',now(),now(),1);

-- kids
insert into im_price_algorithm(`category_type`,`season_code`,`name`,`created_at`,`updated_at`,`enabled`)
values(2,'17FW','日常',now(),now(),1);
insert into im_price_algorithm(`category_type`,`season_code`,`name`,`created_at`,`updated_at`,`enabled`)
values(2,'17SS','日常',now(),now(),1);
insert into im_price_algorithm(`category_type`,`season_code`,`name`,`created_at`,`updated_at`,`enabled`)
values(2,'18FW','日常',now(),now(),1);
insert into im_price_algorithm(`category_type`,`season_code`,`name`,`created_at`,`updated_at`,`enabled`)
values(2,'18SS','日常',now(),now(),1);
insert into im_price_algorithm(`category_type`,`season_code`,`name`,`created_at`,`updated_at`,`enabled`)
values(2,'CarryOver','日常',now(),now(),1);

insert into im_price_algorithm(`category_type`,`season_code`,`name`,`created_at`,`updated_at`,`enabled`)
values(2,'17FW','大促',now(),now(),1);
insert into im_price_algorithm(`category_type`,`season_code`,`name`,`created_at`,`updated_at`,`enabled`)
values(2,'17SS','大促',now(),now(),1);
insert into im_price_algorithm(`category_type`,`season_code`,`name`,`created_at`,`updated_at`,`enabled`)
values(2,'18FW','大促',now(),now(),1);
insert into im_price_algorithm(`category_type`,`season_code`,`name`,`created_at`,`updated_at`,`enabled`)
values(2,'18SS','大促',now(),now(),1);
insert into im_price_algorithm(`category_type`,`season_code`,`name`,`created_at`,`updated_at`,`enabled`)
values(2,'CarryOver','大促',now(),now(),1);

insert into im_price_algorithm(`category_type`,`season_code`,`name`,`created_at`,`updated_at`,`enabled`)
values(2,'17FW','小促',now(),now(),1);
insert into im_price_algorithm(`category_type`,`season_code`,`name`,`created_at`,`updated_at`,`enabled`)
values(2,'17SS','小促',now(),now(),1);
insert into im_price_algorithm(`category_type`,`season_code`,`name`,`created_at`,`updated_at`,`enabled`)
values(2,'18FW','小促',now(),now(),1);
insert into im_price_algorithm(`category_type`,`season_code`,`name`,`created_at`,`updated_at`,`enabled`)
values(2,'18SS','小促',now(),now(),1);
insert into im_price_algorithm(`category_type`,`season_code`,`name`,`created_at`,`updated_at`,`enabled`)
values(2,'CarryOver','小促',now(),now(),1);

insert into im_price_algorithm_rule(`im_price_algorithm_id`,`algorithm_rule_type`,`created_at`,`updated_at`,`enabled`)
select im_price_algorithm_id,'1',created_at,updated_at,1 from im_price_algorithm;

insert into im_price_algorithm_rule_brand (`im_price_algorithm_rule_id`,`brand_id`,`enabled`)
select im_price_algorithm_rule_id,'-1',1 from im_price_algorithm_rule;

INSERT INTO `im_price_discount_model` VALUES (1, 100, 86);
INSERT INTO `im_price_discount_model` VALUES (2, 95, 82);
INSERT INTO `im_price_discount_model` VALUES (3, 90, 78);
INSERT INTO `im_price_discount_model` VALUES (4, 85, 73);
INSERT INTO `im_price_discount_model` VALUES (5, 80, 69);
INSERT INTO `im_price_discount_model` VALUES (6, 77, 68);
INSERT INTO `im_price_discount_model` VALUES (7, 75, 66);
INSERT INTO `im_price_discount_model` VALUES (8, 70, 62);
INSERT INTO `im_price_discount_model` VALUES (9, 65, 57);
INSERT INTO `im_price_discount_model` VALUES (10, 60, 54);
INSERT INTO `im_price_discount_model` VALUES (11, 55, 50);
INSERT INTO `im_price_discount_model` VALUES (12, 50, 50);
INSERT INTO `im_price_discount_model` VALUES (13, 45, 50);
INSERT INTO `im_price_discount_model` VALUES (14, 40, 50);
INSERT INTO `im_price_discount_model` VALUES (15, 35, 40);
INSERT INTO `im_price_discount_model` VALUES (16, 30, 40);
INSERT INTO `im_price_discount_model` VALUES (17, 25, 40);


insert into im_price_discount_mapping (im_price_algorithm_rule_id,boutique_discount_off,im_discount_off,created_at,updated_at,enabled)
select im_price_algorithm_rule_id,boutique_discount_off,im_discount_off,now(),now(),1 from im_price_discount_model ,im_price_algorithm_rule ;



