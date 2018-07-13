package com.izmar.lunatech.model;

import java.util.List;

import com.izmar.lunatech.Database;

public class Airport {

	final private String ident;
	final private String name;
	
	
	public Airport(String ident, String name) {
		this.ident = ident;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public List<Runway> getRunways() {
		return Database.getRunways(ident);
	}
	
}
