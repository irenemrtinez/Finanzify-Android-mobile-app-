package com.example.finanzify.Clases;

public class Limite {
    private Categoria categoria;
    private double cantidadLimite;
    private String fechaInicio;
    private boolean seRepite;
    private String frecuenciaRepetición;
    private String clave;
    public Limite() {

    }
    public Limite(Categoria categoria, double cantidadLimite, String fechaInicio, boolean seRepite, String frecuenciaRepetición, String clave) {
        this.categoria = categoria;
        this.cantidadLimite = cantidadLimite;
        this.fechaInicio = fechaInicio;
        this.seRepite = seRepite;
        this.frecuenciaRepetición = frecuenciaRepetición;
        this.clave = clave;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public double getCantidadLimite() {
        return cantidadLimite;
    }

    public void setCantidadLimite(double cantidadLimite) {
        this.cantidadLimite = cantidadLimite;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public boolean isSeRepite() {
        return seRepite;
    }

    public void setSeRepite(boolean seRepite) {
        this.seRepite = seRepite;
    }

    public String getFrecuenciaRepetición() {
        return frecuenciaRepetición;
    }

    public void setFrecuenciaRepetición(String frecuenciaRepetición) {
        this.frecuenciaRepetición = frecuenciaRepetición;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }
}
