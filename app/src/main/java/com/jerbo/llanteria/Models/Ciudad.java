package com.jerbo.llanteria.Models;

public class Ciudad {
    int id;
    String nombre;

    public Ciudad(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }


    @Override
    public String toString() {
        return "id ="+id +" nom= "+nombre;
    }
}
