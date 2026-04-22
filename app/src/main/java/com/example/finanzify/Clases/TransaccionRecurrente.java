package com.example.finanzify.Clases;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TransaccionRecurrente {
    private String fechaInicio;
    private String fechaFin;
    private double cantidad;
    private String mensaje;
    private Categoria categoria;
    private String recurrencia;
    private String tipo;
    private List<String> fechasPagosRealizados;

    private String clave; // String clave para guardar la clave de la transacción recurrente
    private UUID uuidWorker; // UUID worker para guardar el identificador del worker asociado
    private boolean primeravez;

    public TransaccionRecurrente() {
        // Constructor vacío requerido para Firebase
    }

    public TransaccionRecurrente(String fechaInicio, String fechaFin, double cantidad, String mensaje, Categoria categoria, String recurrencia, String tipo, String clave, boolean primeravez) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.cantidad = cantidad;
        this.mensaje = mensaje;
        this.categoria = categoria;
        this.recurrencia = recurrencia;
        this.tipo = tipo;
        this.clave = clave;
        this.uuidWorker = uuidWorker;
        this.fechasPagosRealizados = new ArrayList<>();
        this.primeravez = primeravez;
    }

    public boolean isPrimeravez(){
        return primeravez;
    }

    public void setPrimeravez(boolean primeravez) {
        this.primeravez = primeravez;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public UUID getUuidWorker() {
        return uuidWorker;
    }

    public void setUuidWorker(UUID uuidWorker) {
        this.uuidWorker = uuidWorker;
    }
    // Getters y setters
    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public String getRecurrencia() {
        return recurrencia;
    }

    public void setRecurrencia(String recurrencia) {
        this.recurrencia = recurrencia;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public List<String> getFechasPagosRealizados() {
        return fechasPagosRealizados;
    }

    public void setFechasPagosRealizados(List<String> fechasPagosRealizados) {
        this.fechasPagosRealizados = fechasPagosRealizados;
    }

    // Función para agregar una nueva fecha a fechasPagosRealizados
    public void agregarFechaPagoRealizado(String fecha) {
        if (fechasPagosRealizados == null) {
            fechasPagosRealizados = new ArrayList<>();
        }
        fechasPagosRealizados.add(fecha);
    }


}
