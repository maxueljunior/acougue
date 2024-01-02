CREATE TABLE IF NOT EXISTS `tb_vendas` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `condicao_pagamento` enum('CREDITO','DEBITO','DINHEIRO','PIX') DEFAULT NULL,
  `data_venda` datetime(6) DEFAULT NULL,
  `valor_total` decimal(38,2) DEFAULT NULL,
  `cliente_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKpg3lw256imfd0jhxj5e3l1etr` (`cliente_id`),
  CONSTRAINT `FKpg3lw256imfd0jhxj5e3l1etr` FOREIGN KEY (`cliente_id`) REFERENCES `tb_cliente` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
