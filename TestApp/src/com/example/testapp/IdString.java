package com.example.testapp;

public class IdString{
	int id;
	String name;
	//Constructor
	public IdString(int id, String nombre) {
		super();
		this.id = id;
		this.name = nombre;
	}
	@Override
	public String toString() {
		return name;
	}
	public int getId() {
		return id;
	}
}