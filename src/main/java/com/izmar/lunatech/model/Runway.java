package com.izmar.lunatech.model;

import java.util.List;

import com.izmar.lunatech.Database;

/*
 * Models a runway. We don't see a lot happening here, because the more
 * exciting query in this app where all different kinds of runway surfaces
 * per country is done directly from Country to Database for efficiency. 
 */

public class Runway {

	final private int id;

	public Runway(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public static List<String> getMostCommonIdents() {
		return Database.getMostCommonRunwayIdents();
	}

}
