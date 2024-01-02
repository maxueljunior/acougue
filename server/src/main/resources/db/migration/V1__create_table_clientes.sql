CREATE TABLE IF NOT EXISTS `tb_cliente` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `data_nascimento` date DEFAULT NULL,
  `bairro` varchar(255) DEFAULT NULL,
  `numero` int DEFAULT NULL,
  `rua` varchar(255) DEFAULT NULL,
  `lucratividade` decimal(38,2) DEFAULT NULL,
  `nome` varchar(255) DEFAULT NULL,
  `sexo` enum('F','M') DEFAULT NULL,
  `sobrenome` varchar(255) DEFAULT NULL,
  `telefone` varchar(255) DEFAULT NULL,
  `ativo` bit(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

