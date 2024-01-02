CREATE TABLE IF NOT EXISTS `tb_permissoes`(
	`id` bigint not null auto_increment,
    `descricao` varchar(255) default null,
    PRIMARY KEY(`id`)
);