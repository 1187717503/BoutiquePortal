INSERT INTO `shipping_segment` (`shipping_segment_id`, `segment_sequence`, `shipping_routing_id`, `shipping_provider_id`, `shipment_type_id`, `custom`, `enabled`, `create_at`, `update_at`) VALUES ('208', '1', '33', '1', '3', '0', '1', '2018-04-14 12:11:18', '2018-04-14 12:11:21');

INSERT INTO `shipping_routing` (`shipping_routing_id`, `routing_name`, `consigner_country_id`, `consignee_country_id`, `enabled`, `create_at`, `update_at`) VALUES ('33', 'Italy to Transit Warehouse', '19', '52', '1', '2018-04-14 12:12:39', '2018-04-14 12:12:42');

INSERT INTO `address_country` (`address_country_id`, `name`, `geography_id`) VALUES ('52', 'Transit Warehouse', '6');