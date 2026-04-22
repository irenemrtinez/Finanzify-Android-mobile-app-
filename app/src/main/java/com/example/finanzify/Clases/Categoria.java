package com.example.finanzify.Clases;

public class Categoria {
    private String nombre;
    private String urlImagen;
    private String tipo;

    public Categoria() {
        // Constructor vacío requerido por Firebase
    }

    public Categoria(String nombre, String urlImagen, String tipo) {
        this.nombre = nombre;
        this.urlImagen = urlImagen;
        this.tipo = tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUrlImagen() {
        return urlImagen;
    }

    public void setUrlImagen(String urlImagen) {
        this.urlImagen = urlImagen;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
