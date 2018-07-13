package com.izmar.lunatech.model;

import java.util.List;

import com.izmar.lunatech.Database;

public class Country {

	final private String code;
		
	public Country(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
	
	public String getName() {
		return Database.getCountryName(code);		
	}
	
	public List<Airport> getAirports() {
		return Database.getAirports(code);
	}
	
	public int getAirportCount() {
		//return getAirports().size();
		return Database.getAirportCount(code);
	}
	
}
