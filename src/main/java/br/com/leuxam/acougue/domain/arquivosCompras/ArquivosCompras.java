package br.com.leuxam.acougue.domain.arquivosCompras;

import br.com.leuxam.acougue.domain.compras.Compras;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "ArquivosCompras")
@Table(name = "tb_arquivos_compras")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ArquivosCompras {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "id_compras")
	private Compras compras;
	
	@Lob
	private byte[] data;
	
	@JoinColumn(name = "file_name")
	private String fileName;
	
	@JoinColumn(name = "file_type")
	private String fileType;
}
