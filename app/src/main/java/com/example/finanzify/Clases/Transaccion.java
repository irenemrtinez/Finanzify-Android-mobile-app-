package com.example.finanzify.Clases;

import com.example.finanzify.Clases.Categoria;

public class Transaccion {
    private String fecha;
    private double cantidad;
    private Categoria categoria;
    private String mensaje;
    private String tipo;
    private boolean cobrado;

    // Constructor
    public Transaccion(){

    }
    public Transaccion(String fecha, double cantidad, Categoria categoria, String mensaje, String tipo, boolean cobrado) {
        this.fecha = fecha;
        this.cantidad = cantidad;
        this.categoria = categoria;
        this.mensaje = mensaje;
        this.tipo = tipo; // Establecer el tipo como "pago" por defecto
        this.cobrado = cobrado;
    }

    // Getters y Setters
    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    public boolean isCobrado() {
        return cobrado;
    }

    public void setCobrado(boolean b) {
        this.cobrado=b;
    }
}