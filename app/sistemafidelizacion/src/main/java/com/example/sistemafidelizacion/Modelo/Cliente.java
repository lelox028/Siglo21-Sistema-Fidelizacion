package com.example.sistemafidelizacion.Modelo;

import java.time.LocalDate;

public class Cliente {
    private int dni;
    private String nombreCompleto;
    private String email;
    private String telefono;
    private LocalDate fechaNacimiento;

    // constructor
    public Cliente() {
    }

    public Cliente(int dni, String nombreCompleto, String email, String telefono, LocalDate fechaNacimiento) {
        this.dni = dni;
        this.nombreCompleto = nombreCompleto;
        this.email = email;
        this.telefono = telefono;
        this.fechaNacimiento = fechaNacimiento;
    }

    // getters y setters
    public int getDni() {
        return dni;
    }

    public void setDni(int dni) {
        this.dni = dni;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    // toString
    @Override
    public String toString() {
        return "Cliente{" +
                "dni=" + dni +
                ", nombreCompleto='" + nombreCompleto + '\'' +
                ", email='" + email + '\'' +
                ", telefono='" + telefono + '\'' +
                ", fechaNacimiento=" + fechaNacimiento +
                '}';
    }

    // metodos especificos
    public boolean utilizoBeneficioCumpleanos() {
        // logica para determinar si el cliente utilizo el beneficio de cumpleaños
        return false;
    }
}
