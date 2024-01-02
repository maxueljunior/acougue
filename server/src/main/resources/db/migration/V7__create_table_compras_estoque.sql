CREATE TABLE IF NOT EXISTS `tb_compras_estoque` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `preco_unitario` decimal(38,2) DEFAULT NULL,
  `quantidade` double DEFAULT NULL,
  `id_compras` bigint DEFAULT NULL,
  `id_estoque` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK1jdxr2vkgo4s62iwfiteyocuy` (`id_compras`),
  KEY `FK9o1gmemysawch13t4r08nwgoy` (`id_estoque`),
  CONSTRAINT `FK1jdxr2vkgo4s62iwfiteyocuy` FOREIGN KEY (`id_compras`) REFERENCES `tb_compras` (`id`),
  CONSTRAINT `FK9o1gmemysawch13t4r08nwgoy` FOREIGN KEY (`id_estoque`) REFERENCES `tb_estoque` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
