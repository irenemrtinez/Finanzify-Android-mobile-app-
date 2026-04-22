package com.example.finanzify.Clases;

public class PresupuestoA {
    private String nombreCategoria;
    private String frecuencia;
    private String porcentaje;
    private double gastado;
    private double total;
    private String clave;
    private String fecha;
    private boolean seRepite;

    public PresupuestoA(String nombreCategoria, String frecuencia, String porcentaje, double gastado, double total, String clave, String fecha, boolean seRepite) {
        this.nombreCategoria = nombreCategoria;
        this.frecuencia = frecuencia;
        this.porcentaje = porcentaje;
        this.gastado = gastado;
        this.total = total;
        this.clave = clave;
        this.fecha = fecha;
        this.seRepite = seRepite;
    }

    public String getNombreCategoria() {
        return nombreCategoria;
    }

    public void setNombreCategoria(String nombreCategoria) {
        this.nombreCategoria = nombreCategoria;
    }

    public String getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(String frecuencia) {
        this.frecuencia = frecuencia;
    }

    public String getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(String porcentaje) {
        this.porcentaje = porcentaje;
    }

    public double getGastado() {
        return gastado;
    }

    public void setGastado(double gastado) {
        this.gastado = gastado;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public boolean isSeRepite() {
        return seRepite;
    }

    public void setSeRepite(boolean seRepite) {
        this.seRepite = seRepite;
    }
}
