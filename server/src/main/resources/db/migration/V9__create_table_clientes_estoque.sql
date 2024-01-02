CREATE TABLE IF NOT EXISTS `tb_cliente_estoque`(
	`id` bigint NOT NULL AUTO_INCREMENT,
    `id_estoque` bigint DEFAULT NULL,
    `id_cliente` bigint DEFAULT NULL,
    `lucratividade` decimal(38,2) DEFAULT NULL,
    `data_atualizado` datetime(6) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `estoque` (`id_estoque`),
    KEY `cliente` (`id_cliente`),
    CONSTRAINT `fk_id_estoque` FOREIGN KEY (`id_estoque`) REFERENCES `tb_estoque` (`id`),
    CONSTRAINT `fk_id_cliente` FOREIGN KEY (`id_cliente`) REFERENCES `tb_cliente` (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;