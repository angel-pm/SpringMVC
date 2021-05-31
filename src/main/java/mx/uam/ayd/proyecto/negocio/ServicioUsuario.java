package mx.uam.ayd.proyecto.negocio;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import mx.uam.ayd.proyecto.datos.GrupoRepository;
import mx.uam.ayd.proyecto.datos.UsuarioRepository;
import mx.uam.ayd.proyecto.dto.UsuarioDto;
import mx.uam.ayd.proyecto.negocio.modelo.Grupo;
import mx.uam.ayd.proyecto.negocio.modelo.Usuario;

@Slf4j
@Service
public class ServicioUsuario {
	
	@Autowired 
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private GrupoRepository grupoRepository;
	
	/**
	 * 
	 * Permite agregar un usuario
	 * 
	 * @param nombre
	 * @param apellido
	 * @param grupo
	 * @return
	 */
	public UsuarioDto agregaUsuario(UsuarioDto usuarioDto) {
		
		// Regla de negocio: No se permite agregar dos usuarios con el mismo nombre y apellido
		Usuario usuario = usuarioRepository.findByNombreAndApellido(usuarioDto.getNombre(), usuarioDto.getApellido());

		if(usuario != null) {
			throw new IllegalArgumentException("Ese usuario ya existe");
		}
		
		//Optional <Grupo> optGrupo = grupoRepository.findByNombre(usuarioDto.getGrupo());
		Optional <Grupo> optGrupo = grupoRepository.findById(usuarioDto.getGrupo());
		
		if(optGrupo.isEmpty()) {
			throw new IllegalArgumentException("No se encontró el grupo");
		}
		
		Grupo grupo = optGrupo.get();
		
		log.info("Agregando usuario nombre: " + usuarioDto.getNombre() +" apellido:" + usuarioDto.getApellido());
		usuario = new Usuario();
		
		usuario.setNombre(usuarioDto.getNombre());
		usuario.setApellido(usuarioDto.getApellido());
		usuario.setEdad(usuarioDto.getEdad());
		usuario.setGrupo(grupo);
		
		usuario = usuarioRepository.save(usuario);
		//usuarioRepository.save(usuario);
		
		grupo.addUsuario(usuario);
		grupoRepository.save(grupo);
		
		usuarioDto.setIdUsuario(usuario.getIdUsuario());
		
		//return UsuarioDto.creaDto(usuario);
		return usuarioDto;//.creaDto(usuario);
	}

	/**
	 * Recupera todos los usuarios existentes
	 * 
	 * @return Una lista con los usuarios (o lista vacía)
	 */
	public List <UsuarioDto> recuperaUsuarios() {

		log.info("usuarioRepository = " + usuarioRepository);
		
		List <UsuarioDto> usuarios = new ArrayList<>();
		
		for(Usuario usuario:usuarioRepository.findAll()) {
			usuarios.add(UsuarioDto.creaDto(usuario));
		}
				
		return usuarios;
	}
	
	public Usuario getUsuario(Long id) {
		log.info("GET de un solo usuario: " + id);
		Optional<Usuario> optUsuario = usuarioRepository.findById(id);
		//Usuario usuario = new Usuario();
		//usuario = optUsuario.get();
		//return usuario;
		return optUsuario.get();
	}

	public UsuarioDto putUsuario(Long id, UsuarioDto usuarioDto) {
		Optional <Usuario> optUsuario = usuarioRepository.findById(id);
		Usuario user = new Usuario(); 
		user = optUsuario.get();
		
		Optional<Grupo> optGrupo = grupoRepository.findById(usuarioDto.getGrupo());
		if(optGrupo.isEmpty()) {
			throw new IllegalArgumentException("No se encontró el grupo");
		}
		Grupo grupo = optGrupo.get();
		
		user.setNombre(usuarioDto.getNombre());
		user.setApellido(usuarioDto.getApellido());
		user.setEdad(usuarioDto.getEdad());
		user.setGrupo(grupo);
		
		user = usuarioRepository.save(user);
		
		grupo.addUsuario(user);
		grupoRepository.save(grupo);
		
		//user.setIdUsuario(usuarioDto.getIdUsuario());
		
		return UsuarioDto.creaDto(user);
	}

	public boolean delete(Long id) {
		Optional<Usuario> optUsuario = usuarioRepository.findById(id);
		Usuario user = new Usuario(); 
		user = optUsuario.get();
		if(user!=null) {
			//usuarioRepository.deleteById(id);
			usuarioRepository.delete(user);
			return true;
		}else {
			return false;
		}
	}
	
}
