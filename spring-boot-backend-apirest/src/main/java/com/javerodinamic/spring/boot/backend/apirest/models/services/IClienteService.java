/*
 * En esta interface le dare un contrato de implementación los metodos del CRUD
 */
package com.javerodinamic.spring.boot.backend.apirest.models.services;

import java.util.List;

import com.javerodinamic.spring.boot.backend.apirest.models.entity.Cliente;


public interface IClienteService {
	
	public List<Cliente> findAll();

}
