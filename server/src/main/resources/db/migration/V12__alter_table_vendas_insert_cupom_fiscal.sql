ALTER TABLE tb_vendas ADD COLUMN(
	`data` blob default null,
    `file_name` varchar(255)
);