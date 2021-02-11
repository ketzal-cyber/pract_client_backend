/*
 * Anotación RestController
 * Anotación CrossOrigin que es el cors para permitir el acceso a dominios
 * Metodo index para listar los clientes
 * implemantar metodos para el crud completo
 * implementacion el manejo de errores
 * implementación de subida de imagen de forma independiente
 */
package com.javerodinamic.spring.boot.backend.apirest.controllers;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.javerodinamic.spring.boot.backend.apirest.models.entity.Cliente;
import com.javerodinamic.spring.boot.backend.apirest.models.services.IClienteService;

@CrossOrigin(origins = { "http://localhost:4200" })
@RestController
@RequestMapping("/api")
public class ClienteRestController {

	@Autowired
	private IClienteService clienteService;

	@GetMapping("/clientes")
	public List<Cliente> index() {
		return clienteService.findAll();
	}
	
	/* metodo para la paginacion
	 * */ 
	@GetMapping("/clientes/page/{page}")
	public Page<Cliente> index(@PathVariable Integer page) {
		//otra opcion podria ser crear la instancia Pageable pageable PageRequest.of(page, 4);
		return clienteService.findAll(PageRequest.of(page, 4));
	}
	

	/*
	 * implementacion captura de errores usar clase ResponsEntity propia de spring
	 * en lugar de retornar Cliente como retornara un string en caso de error
	 * ResponsEntity sera de tipo generic (?) usamos un Map para tener el menaje de
	 * error prueba de rama capturar errores bloque TRY CATCH para capturar errores
	 * al consultar DB
	 */
	@GetMapping("/clientes/{id}")
	public ResponseEntity<?> show(@PathVariable Long id) {
		// return clienteService.findById(id); para cuando retorna un cliente

		Cliente cliente = null;
		Map<String, Object> response = new HashMap<>();
		try {
			cliente = clienteService.findById(id);
		} catch (DataAccessException e) {
			response.put("Mensaje", "Error en la consulta de base de datos");
			response.put("Mensaje", e.getMessage().concat(" : ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (cliente == null) {
			response.put("Mensaje", "El cliente ID: ".concat(id.toString().concat(" no existe en la ase de datos")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Cliente>(cliente, HttpStatus.OK);

	}

	/*
	 * como vienen en formato JSon le pasamos el Requestody para ue los convierta y
	 * mapie en las propiedades le podemos pasar una fecha en el metodo anotacion
	 * para la validación @Valid sin esta las anotaciones de Entity no funcionan
	 * inyectar objeto que contiene todos los mensajes de error BindingResult
	 */

	@PostMapping("/clientes")
	// @ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> create(@Valid @RequestBody Cliente cliente, BindingResult result) {
		// cliente.setCreateAt(new Date());
		// return clienteService.save(cliente);

		Cliente clienteNew = null;
		Map<String, Object> response = new HashMap<>();

		if (result.hasErrors()) {

			/*
			 * metodo para versiones antes de JDK8 List<String> errors = new
			 * ArrayList<String>(); for(FieldError err: result.getFieldErrors()) {
			 * errors.add("El campo "+ err.getField() + " ' "+ err.getDefaultMessage()); }
			 */

			List<String> errors = result.getFieldErrors().stream()
					.map(err -> "El campo " + err.getField() + " ' " + err.getDefaultMessage())
					.collect(Collectors.toList());

			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}

		try {
			clienteNew = clienteService.save(cliente);
		} catch (DataAccessException e) {
			response.put("Mensaje", "Error en la insert en la base de datos");
			response.put("Mensaje", e.getMessage().concat(" : ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		// objeto Mao para mandar un mensaje de confirmacion
		response.put("mensaje", "El cliente ha sido creado con exito");
		response.put("cliente", clienteNew);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	@PutMapping("/clientes/{id}")
	// @ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> update(@Valid @RequestBody Cliente cliente, BindingResult result, @PathVariable Long id) {
		// Cliente clienteActual = clienteService.findById(id); si retorna un Cliente
		// unicamente

		Cliente clienteActual = clienteService.findById(id);
		Cliente clienteUpdated = null;
		Map<String, Object> response = new HashMap<>();

		if (result.hasErrors()) {

			/*
			 * metodo para versiones antes de JDK8 List<String> errors = new
			 * ArrayList<String>(); for(FieldError err: result.getFieldErrors()) {
			 * errors.add("El campo "+ err.getField() + " ' "+ err.getDefaultMessage()); }
			 */

			List<String> errors = result.getFieldErrors().stream()
					.map(err -> "El campo " + err.getField() + " ' " + err.getDefaultMessage())
					.collect(Collectors.toList());

			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}

		if (clienteActual == null) {
			response.put("Mensaje", "Error no se puede editar, el cliente ID: "
					.concat(id.toString().concat(" no existe en la ase de datos")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		try {
			clienteActual.setNombre(cliente.getNombre());
			clienteActual.setApellido(cliente.getApellido());
			clienteActual.setEmail(cliente.getEmail());
			clienteActual.setCreateAt(cliente.getCreateAt());
			clienteUpdated = clienteService.save(clienteActual);
		} catch (DataAccessException e) {
			response.put("Mensaje", "Error: al actualizar en la DB");
			response.put("Mensaje", e.getMessage().concat(" : ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "El cliente ha sido actualiado con exito");
		response.put("cliente", clienteUpdated);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	@DeleteMapping("/clientes/{id}")
	// @ResponseStatus(HttpStatus.NO_CONTENT)
	public ResponseEntity<?> delete(@PathVariable Long id) {

		Map<String, Object> response = new HashMap<>();

		try {
			
			Cliente cliente = clienteService.findById(id);
			//validacion para boorar foto anterior
			String nombreFotoAnterior = cliente.getFoto();
			if(nombreFotoAnterior != null && nombreFotoAnterior.length() > 0) {
				Path rutaFotoAnterior = Paths.get("uploads").resolve(nombreFotoAnterior).toAbsolutePath();
				File archivoFotoAnterior = rutaFotoAnterior.toFile();
				if(archivoFotoAnterior.exists() && archivoFotoAnterior.canRead()) {
					archivoFotoAnterior.delete();
				}
			}
			
			clienteService.delete(id);
		} catch (DataAccessException e) {
			response.put("Mensaje", "Error: al eliminar en la DB");
			response.put("Mensaje", e.getMessage().concat(" : ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("Mensaje", "El cliente ha sido eliminado con exito");
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
	
	/*
	 * Metodo para tratar la subida de la imagen
	 * configurar tamaño de imagen en application properties
	 * clase UUID para generar un codigo unico y concatenarlo al nombre - replace(" ", "") remplaza espacio en nada
	 * borrar foto anterior
	 */
	 @PostMapping("/clientes/upload")
	public ResponseEntity<?> upload(@RequestParam("archivo") MultipartFile archivo, @RequestParam("id") Long id){
		Map<String, Object> response = new HashMap<>();
		
		Cliente cliente = clienteService.findById(id);
		
		//validar si viene la imagen es distinto a empty
		if(!archivo.isEmpty()) {
			String nombreArchivo = UUID.randomUUID().toString() + "_"+ archivo.getOriginalFilename().replace(" ", "");
			Path rutaArchivo = Paths.get("uploads").resolve(nombreArchivo).toAbsolutePath();
			
			try {
				Files.copy(archivo.getInputStream(), rutaArchivo);
			} catch (IOException e) {
				response.put("Mensaje", "Error: al subir la imagen "+ nombreArchivo);
				response.put("Mensaje", e.getMessage().concat(" : ").concat(e.getCause().getMessage()));
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			
			//validacion para boorar foto anterior
			String nombreFotoAnterior = cliente.getFoto();
			if(nombreFotoAnterior != null && nombreFotoAnterior.length() > 0) {
				Path rutaFotoAnterior = Paths.get("uploads").resolve(nombreFotoAnterior).toAbsolutePath();
				File archivoFotoAnterior = rutaFotoAnterior.toFile();
				if(archivoFotoAnterior.exists() && archivoFotoAnterior.canRead()) {
					archivoFotoAnterior.delete();
				}
			}
			
			
			cliente.setFoto(nombreArchivo);
			clienteService.save(cliente);
			
			response.put("cliente", cliente);
			response.put("mensaje", "Ha suido correctamente la imagen "+ nombreArchivo);
		}
		
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	 
	@GetMapping("/uploads/img/{nombreFoto:.+}")
	public ResponseEntity<Resource> verfoto(@PathVariable String nombreFoto){
		Path rutaArchivo = Paths.get("uploads").resolve(nombreFoto).toAbsolutePath();
		Resource recurso = null;
		
		try {
			recurso = new UrlResource(rutaArchivo.toUri());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(!recurso.exists() && !recurso.isReadable()) {
			throw new RuntimeException("Error no se pudo cargar la imagen "+ nombreFoto);
		}
		
		//pasar cabeceras para forzar la descarga
		// attachment forzar la descarga para utiliar en html
		HttpHeaders cabecera = new HttpHeaders();
		// cabecera.add("Content-Disposition", "");
		cabecera.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"");
		
		return new ResponseEntity<Resource>(recurso, cabecera,  HttpStatus.OK);
	}

}
