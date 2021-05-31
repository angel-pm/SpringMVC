package mx.uam.ayd.proyecto.Servicios;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import lombok.extern.slf4j.Slf4j;
import mx.uam.ayd.proyecto.dto.UsuarioDto;
import mx.uam.ayd.proyecto.negocio.ServicioUsuario;
import mx.uam.ayd.proyecto.negocio.modelo.Usuario;

@RestController
@RequestMapping("/v1") //versionamiento
@Slf4j
public class UsuarioRestController {

	@Autowired
	private ServicioUsuario servicioUsuarios;
	
	/**
	 * Permite recuperar todos los usuarios
	 * @return
	 */
	@GetMapping(path="/usuarios", produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<UsuarioDto>> retrieveAll(){
		List<UsuarioDto> usuarios = servicioUsuarios.recuperaUsuarios();
		return ResponseEntity.status(HttpStatus.OK).body(usuarios);
	}
	
	/**
	 * Metodo que permite agregar un nuevo usuario
	 * @param nuevoUsuario 
	 * @return
	 */
	@PostMapping(path="/usuarios", consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UsuarioDto> create(@RequestBody UsuarioDto nuevoUsuario){
		
		log.info("Recibi el usuario: " + nuevoUsuario);
		try {
			UsuarioDto usuarioDto = servicioUsuarios.agregaUsuario(nuevoUsuario);
			return ResponseEntity.status(HttpStatus.CREATED).body(usuarioDto);
		}catch (Exception ex) {
			HttpStatus status;
			
			if(ex instanceof IllegalArgumentException) {
				status = HttpStatus.BAD_REQUEST;
			}else {
				status = HttpStatus.INTERNAL_SERVER_ERROR;
			}
			
			throw new ResponseStatusException(status, ex.getMessage());
		}
	}
	
	/**
	 * Permite recuperar un usuario a partir de su ID
	 * @param id
	 * @return
	 */
	@GetMapping(path="/usuarios/{id}", produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UsuarioDto> retrieve(@PathVariable("id") Long id){
		log.info("Retrieve del usuario con id: " + id);
		try {
			Usuario usuario = new Usuario(); 
			usuario = servicioUsuarios.getUsuario(id);
			return ResponseEntity.status(HttpStatus.OK).body(UsuarioDto.creaDto(usuario));
		}catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
	}
	
	/**
	 * Metodo que actualiza un usuario
	 * @param id
	 * @param usuario
	 * @return
	 */
	@PutMapping(path="/usuarios/{id}", consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UsuarioDto> put(@PathVariable("id") Long id, @RequestBody UsuarioDto usuario) {
		log.info("Put del usuario con id: " + id);
		try {
			UsuarioDto usuarioDto = new UsuarioDto(); 
			usuarioDto = servicioUsuarios.putUsuario(id, usuario);
			return ResponseEntity.status(HttpStatus.CREATED).body(usuarioDto);
		}catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
	}
	
	@DeleteMapping(path="/usuarios/{id}", produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> delete(@PathVariable("id") Long id){
		
		Usuario usuario = new Usuario();
		usuario = servicioUsuarios.getUsuario(id);
		
		if(usuario!=null) {
		
			servicioUsuarios.delete(id);
			log.info("Delete al usuario con id: " + id);
			
			return ResponseEntity.status(HttpStatus.OK).build();
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontro al usuario");
		}
	}
	
}
