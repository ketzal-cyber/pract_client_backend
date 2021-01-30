/*
 * En esta interface le dare un contrato de implementación los metodos del CRUD
 * siguente paso declarar metodos para crud findyid, save, delete
 * incluir metodo para paginacion findAll()
 */
package com.javerodinamic.spring.boot.backend.apirest.models.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.javerodinamic.spring.boot.backend.apirest.models.entity.Cliente;


public interface IClienteService {
	
	//metodo para listar los clientes
	public List<Cliente> findAll();
	
	//metodo para la paginacion 
	public Page<Cliente> findAll(Pageable pageable);
	
	// metodo para buscar por id
	public Cliente findById(Long id);
	
	//metodo para guardar un cliente
	public Cliente save(Cliente cliente);
	
	//metodo para eliminar
	public void delete(Long id);

}
