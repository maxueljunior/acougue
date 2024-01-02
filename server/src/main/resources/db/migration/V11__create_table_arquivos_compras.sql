CREATE TABLE IF NOT EXISTS `tb_arquivos_compras`(
	`id` bigint NOT NULL AUTO_INCREMENT,
    `id_compras` bigint DEFAULT NULL,
    `data` longblob default null,
    `file_name` varchar(255) default null,
    `file_type` varchar(255) default null,
    PRIMARY KEY(`id`),
    KEY `fk_id_compras_arquivos` (`id_compras`),
    CONSTRAINT `fk_id_compras_arquivos_123` FOREIGN KEY (`id_compras`) REFERENCES `tb_compras` (`id`)
);