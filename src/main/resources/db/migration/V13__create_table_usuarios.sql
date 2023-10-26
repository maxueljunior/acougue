CREATE TABLE IF NOT EXISTS `tb_usuario`(
	`id` bigint not null auto_increment,
    `username` varchar(255) default null,
    `password` varchar(255) default null,
    `data_criacao` datetime default null,
    `ativo` tinyint(1) not null default 0,
    PRIMARY KEY(`id`)
);