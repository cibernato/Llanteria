package com.jerbo.llanteria.Models;

public class Cliente {
    String sni,nombre,apellido,direccion,correo;
    int id;

    public Cliente(String sni, String nombre, String apellido, String direccion, String correo, int id) {
        this.sni = sni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.direccion = direccion;
        this.correo = correo;
        this.id = id;
    }
}
