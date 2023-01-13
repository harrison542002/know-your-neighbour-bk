package com.kn.jwt.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7892829791819563001L;
	private String token;
}
