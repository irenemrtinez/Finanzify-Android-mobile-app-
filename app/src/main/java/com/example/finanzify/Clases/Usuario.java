package com.example.finanzify.Clases;
public class Usuario {
    private String email;
    private String contraseña;
    private String UID;
    private boolean autorizado2FA;
    private String monedaPreferida;
    private double balanceTotal;
    private String telefono; // Nuevo campo telefono

    // Constructor vacío requerido por Firebase
    public Usuario() {
    }

    // Constructor con parámetros
    public Usuario(String email, String UID, boolean autorizado2FA, String monedaPreferida, double balanceTotal, String telefono) {
        this.email = email;
        this.UID = UID;
        this.autorizado2FA = autorizado2FA;
        this.monedaPreferida = monedaPreferida;
        this.balanceTotal = balanceTotal;
        this.telefono = telefono; // Asignamos el valor del telefono
    }

    // Getters y setters para todos los campos
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public boolean isAutorizado2FA() {
        return autorizado2FA;
    }

    public void setAutorizado2FA(boolean autorizado2FA) {
        this.autorizado2FA = autorizado2FA;
    }

    public String getMonedaPreferida() {
        return monedaPreferida;
    }

    public void setMonedaPreferida(String monedaPreferida) {
        this.monedaPreferida = monedaPreferida;
    }

    public double getBalanceTotal() {
        return balanceTotal;
    }

    public void setBalanceTotal(double balanceTotal) {
        this.balanceTotal = balanceTotal;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
