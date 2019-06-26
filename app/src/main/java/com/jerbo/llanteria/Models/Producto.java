package com.jerbo.llanteria.Models;

import java.io.Serializable;

public class Producto implements Serializable {


    int id,stock,status,tipo_producto_id,precio,cantidad;
    String producto,descripcion,created,modified;

    public Producto(int id, int stock, int status, int tipo_producto_id, String producto, String descripcion, String created, String modified,int precio) {
        this.id = id;
        this.stock = stock;
        this.status = status;
        this.tipo_producto_id = tipo_producto_id;
        this.producto = producto;
        this.descripcion = descripcion;
        this.created = created;
        this.modified = modified;
        this.precio=precio;
    }

    public int getStock() {
        return stock;
    }

    public String getProducto() {
        return producto;
    }
    public int getId() {
        return id;
    }

    public int getPrecio() {
        return precio;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    @Override
    public String toString() {
        return id+" "+stock+" "+status+" "+tipo_producto_id+" "+producto+" "+descripcion+" "+created+" "+modified+" "+ precio;
    }
}
