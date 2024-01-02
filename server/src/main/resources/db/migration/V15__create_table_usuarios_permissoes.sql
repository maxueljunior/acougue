CREATE TABLE IF NOT EXISTS `tb_usuario_permissoes`(
	`id_usuario` bigint not null,
    `id_permissao` bigint not null,
    PRIMARY KEY(`id_usuario`, `id_permissao`),
    KEY `fk_user_permissiao_permissao` (`id_permissao`),
    CONSTRAINT `fk_user_permission` FOREIGN KEY (`id_usuario`) REFERENCES `tb_usuario` (`id`),
    CONSTRAINT `fk_user_permission_permission` FOREIGN KEY (`id_permissao`) REFERENCES `tb_permissoes` (`id`)
);