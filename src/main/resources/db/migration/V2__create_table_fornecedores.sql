CREATE TABLE IF NOT EXISTS `tb_fornecedor` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `cnpj` varchar(255) DEFAULT NULL,
  `nome_contato` varchar(255) DEFAULT NULL,
  `razao_social` varchar(255) DEFAULT NULL,
  `telefone` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;