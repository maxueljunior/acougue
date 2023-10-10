CREATE TABLE `tb_estoque` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `data_compra` date DEFAULT NULL,
  `data_validade` date DEFAULT NULL,
  `descricao` varchar(255) DEFAULT NULL,
  `quantidade` double DEFAULT NULL,
  `unidade` enum('KG','UN') DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;