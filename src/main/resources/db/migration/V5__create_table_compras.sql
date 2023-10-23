CREATE TABLE IF NOT EXISTS `tb_compras` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `valor_total` decimal(38,2) DEFAULT NULL,
  `fornecedor_id` bigint DEFAULT NULL,
  `data` datetime(6) DEFAULT NULL,
  `dat` BLOB DEFAULT NULL,
  `file_name` VARCHAR(255) DEFAULT NULL,
  `file_type` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK7tynx64nk4k4ilihswgyfdlt5` (`fornecedor_id`),
  CONSTRAINT `FK7tynx64nk4k4ilihswgyfdlt5` FOREIGN KEY (`fornecedor_id`) REFERENCES `tb_fornecedor` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
