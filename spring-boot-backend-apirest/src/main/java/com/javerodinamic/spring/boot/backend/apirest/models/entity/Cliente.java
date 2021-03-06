package com.javerodinamic.spring.boot.backend.apirest.models.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Entity
@Table(name="clientes")
public class Cliente implements Serializable {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	//anotacion para validar 
	// @NotEmpty(message = "para mensajes en español por si el location est en english")
	//@Size(min = 4, max = 12, menssage = " mensaje ")
	@NotEmpty
	@Size(min = 4, max = 12)
	@Column(nullable = false)
	private String nombre;
	
	//anotaciones
	// @NotEmpty(message = "para mensajes en español por si el location est en english
	@NotEmpty
	private String apellido;
	
	//anotacion para validar
	// @NotEmpty(message = "para mensajes en español por si el location est en english
	//@Email(message =  "mensaje")
	@NotEmpty
	@Email
	@Column(nullable = false, unique=false)
	private String email;
	
	@NotNull(message = "No puede estar vacio")
	@Column(name="create_at")
	@Temporal(TemporalType.DATE)
	private Date createAt;
	
	private String foto;
	
	/* metodo para asignarle una fecha antes de persistir los datos de un save
	 * meto para guardar la fecha ante de ser guardao en la base
	 * comantado por la implementacion del campo atravez de un datapiker
	@PrePersist
	public void prePersist() {
		createAt = new Date();
	}
	*/
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombbre) {
		this.nombre = nombbre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getCreateAt() {
		return createAt;
	}
	
	

	public String getFoto() {
		return foto;
	}

	public void setFoto(String foto) {
		this.foto = foto;
	}

	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}

}
