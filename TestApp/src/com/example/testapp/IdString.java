package com.example.testapp;

public class IdString {
    String sid;
    int id;
	String name;

	//Constructor
	public IdString(int id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

    public IdString(String sid, String name){
        super();
        this.id = 0;
        this.sid = sid;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IdString)) return false;

        IdString idString = (IdString) o;

        if (id != idString.id) return false;
        if (name != null ? !name.equals(idString.name) : idString.name != null) return false;
        if (sid != null ? !sid.equals(idString.sid) : idString.sid != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = sid != null ? sid.hashCode() : 0;
        result = 31 * result + id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
	public String toString() {
		return name;
	}
	public int getId() {
		return id;
	}

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }



}