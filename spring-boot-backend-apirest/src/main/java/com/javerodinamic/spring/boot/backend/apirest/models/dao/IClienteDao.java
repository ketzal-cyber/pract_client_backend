/*
 * Implementar la clase CrudRepository en lugar de un DAO de forma manual
 * Para los metoos propios sera necesario la anotacion Transaccional 
 */
package com.javerodinamic.spring.boot.backend.apirest.models.dao;

import org.springframework.data.repository.CrudRepository;

import com.javerodinamic.spring.boot.backend.apirest.models.entity.Cliente;

public interface IClienteDao extends CrudRepository<Cliente, Long> {

}
