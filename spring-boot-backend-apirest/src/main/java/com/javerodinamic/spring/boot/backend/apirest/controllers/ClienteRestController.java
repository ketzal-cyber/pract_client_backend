/*
 * Anotación RestController
 * Anotación CrossOrigin que es el cors para permitir el acceso a dominios
 * Metodo index para listar los clientes
 * implemantar metodos para el crud completo
 * implementacion el manejo de errores
 */
package com.javerodinamic.spring.boot.backend.apirest.controllers;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.javerodinamic.spring.boot.backend.apirest.models.entity.Cliente;
import com.javerodinamic.spring.boot.backend.apirest.models.services.IClienteService;

@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/api")
public class ClienteRestController {
	
	@Autowired
	private IClienteService clienteService;
	
	@GetMapping("/clientes")
	public List<Cliente> index(){
		return clienteService.findAll();
	}
	
	
	/*
	 * implementacion captura de errores
	 * usar clase ResponsEntity propia de spring en lugar de retornar Cliente
	 * 	como retornara un string en caso de error ResponsEntity sera de tipo generic (?)
	 * usamos un Map para tener el menaje de error 
	 * prueba de rama capturar errores
	 * bloque TRY CATCH para capturar errores al consultar DB
	 */
	@GetMapping("/clientes/{id}")
	public ResponseEntity<?> show(@PathVariable Long id) {
		// return clienteService.findById(id); para cuando retorna un cliente
		
		Cliente cliente = null;
		Map<String , Object> response = new HashMap<>();
		try {
			cliente = clienteService.findById(id);
		} 
		catch(DataAccessException e) {
			response.put("Mensaje", "Error en la consulta de base de datos");
			response.put("Mensaje", e.getMessage().concat(" : ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String , Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		
		
		if(cliente == null) {
			response.put("Mensaje", "El cliente ID: ".concat(id.toString().concat(" no existe en la ase de datos")));
			return new ResponseEntity<Map<String , Object>>(response, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Cliente>(cliente, HttpStatus.OK);
		
		
	}
	
	// como vienen en formato JSon le pasamos el Requestody para ue los convierta y mapie en las propiedades
	// le podemos pasar una fecha en el metodo
	@PostMapping("/clientes")
	//@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> create(@RequestBody Cliente cliente) {
		//cliente.setCreateAt(new Date());		
		//return clienteService.save(cliente);
		
		Cliente clienteNew = null;
		Map<String, Object> response = new HashMap<>();
		try {
			clienteNew = clienteService.save(cliente);
		} catch(DataAccessException e) {
			response.put("Mensaje", "Error en la insert en la base de datos");
			response.put("Mensaje", e.getMessage().concat(" : ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String , Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		//objeto Mao para mandar un mensaje de confirmacion
		response.put("mensaje", "El cliente ha sido creado con exito");
		response.put("cliente", clienteNew);
		return new ResponseEntity<Map<String , Object>>( response, HttpStatus.CREATED);
	}
	
	@PutMapping("/clientes/{id}")
	//	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> update(@RequestBody Cliente cliente, @PathVariable Long id) {
		// Cliente clienteActual  = clienteService.findById(id); si retorna un Cliente unicamente
		
		Cliente clienteActual  = clienteService.findById(id);
		Cliente clienteUpdated  = null;
		Map<String, Object> response = new HashMap<>();
		
		if(clienteActual == null) {
			response.put("Mensaje", "Error no se puede editar, el cliente ID: ".concat(id.toString().concat(" no existe en la ase de datos")));
			return new ResponseEntity<Map<String , Object>>(response, HttpStatus.NOT_FOUND);
		}
		
		try {
			clienteActual.setNombre(cliente.getNombre());
			clienteActual.setApellido(cliente.getApellido());
			clienteActual.setEmail(cliente.getEmail());
			clienteActual.setCreateAt(cliente.getCreateAt());
			clienteUpdated = clienteService.save(clienteActual);
		}catch(DataAccessException e) {
			response.put("Mensaje", "Error: al actualizar en la DB");
			response.put("Mensaje", e.getMessage().concat(" : ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String , Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("mensaje", "El cliente ha sido actualiado con exito");
		response.put("cliente", clienteUpdated);
		return new ResponseEntity<Map<String , Object>>( response, HttpStatus.CREATED);
	}
	
	
	@DeleteMapping("/clientes/{id}")
	// 	@ResponseStatus(HttpStatus.NO_CONTENT)
	public ResponseEntity<?> delete(@PathVariable Long id) {
		
		Map<String, Object> response = new HashMap<>();
		
		try {
			clienteService.delete(id);
		}catch(DataAccessException e) {
			response.put("Mensaje", "Error: al eliminar en la DB");
			response.put("Mensaje", e.getMessage().concat(" : ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String , Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("Mensaje", "El cliente ha sido eliminado con exito");
		return  new ResponseEntity<Map<String , Object>>(response, HttpStatus.OK);
	}

}
