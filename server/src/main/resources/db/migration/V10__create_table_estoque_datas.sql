CREATE TABLE IF NOT EXISTS `tb_estoque_data` (
	`id` bigint NOT NULL AUTO_INCREMENT,
    `id_estoque` bigint DEFAULT NULL,
    `quantidade` double DEFAULT NULL,
    `data_compra` date DEFAULT NULL,
    `data_validade` date DEFAULT NULL,
    PRIMARY KEY(`id`),
    KEY `fk_id_estoque_123` (`id_estoque`),
    CONSTRAINT `fk_id_estoque_9999` FOREIGN KEY (`id_estoque`) REFERENCES `tb_estoque` (`id`)
);