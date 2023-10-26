package br.com.leuxam.acougue.domain.permissoes;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;

import br.com.leuxam.acougue.domain.usuario.Usuario;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity(name = "Permissoes")
@Table(name = "tb_permissoes")
public class Permissoes implements GrantedAuthority{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String descricao;
	
	@ManyToMany(mappedBy = "permissoes")
	private List<Usuario> usuarios;

	@Override
	public String getAuthority() {
		return descricao;
	}
}
