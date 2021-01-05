/*
 * Anotacion Transactional  en el metodo
 * Como es de solo lectura es un readOnly
 * Los metodos del CRUDRepository ya son transaccionales se ** podria omitir
 * Se sobreescrie en el service para tener control y hcerlo mas explicita
 * Este clase se guardara en el contenedor de spring y posterior se puede inyectar en el controlador y asi lo podemos utiliar
 */

package com.javerodinamic.spring.boot.backend.apirest.models.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.javerodinamic.spring.boot.backend.apirest.models.dao.IClienteDao;
import com.javerodinamic.spring.boot.backend.apirest.models.entity.Cliente;

@Service
public class ClienteServiceImpl implements IClienteService {
	
	//inyeccion de dependencia
	@Autowired
	private IClienteDao clienteDao;
	
	
	@Override
	@Transactional(readOnly = true)
	public List<Cliente> findAll() {

		return (List<Cliente>) clienteDao.findAll();
	}

}