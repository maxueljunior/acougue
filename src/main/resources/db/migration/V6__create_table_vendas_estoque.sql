CREATE TABLE IF NOT EXISTS `tb_vendas_estoque` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `quantidade` double DEFAULT NULL,
  `valor_unitario` decimal(38,2) DEFAULT NULL,
  `id_estoque` bigint DEFAULT NULL,
  `id_vendas` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKhs9h3t244f6peujfmx9tne7f` (`id_estoque`),
  KEY `FKoy821y4mhu5k9umkemcv9gpc2` (`id_vendas`),
  CONSTRAINT `FKhs9h3t244f6peujfmx9tne7f` FOREIGN KEY (`id_estoque`) REFERENCES `tb_estoque` (`id`),
  CONSTRAINT `FKoy821y4mhu5k9umkemcv9gpc2` FOREIGN KEY (`id_vendas`) REFERENCES `tb_vendas` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

