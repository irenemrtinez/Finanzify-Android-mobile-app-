package com.example.finanzify.Clases;

public class CategoriaColor {
    private String categoria;
    private int color;
    private double cantidad;

    public CategoriaColor(String categoria, int color, double cantidad) {
        this.categoria = categoria;
        this.color = color;
        this.cantidad = cantidad;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }
}
