package com.example.sistemafidelizacion.Modelo;

public class Beneficio {
    //estructura 
    private int idBeneficio;
    private String nombre;
    private String descripcion;
    private boolean activo;

    // constructor
    public Beneficio() {
    }

    public Beneficio(int idBeneficio, String nombre, String descripcion, boolean activo) {
        this.idBeneficio = idBeneficio;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.activo = activo;
    }

    //getters y setters
    public int getIdBeneficio() {
        return idBeneficio;
    }

    public void setIdBeneficio(int idBeneficio) {
        this.idBeneficio = idBeneficio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    //toString
    @Override
    public String toString() {
        return "Beneficio{" +
                "idBeneficio=" + idBeneficio +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", activo=" + activo +
                '}';
    }

    // metodos especificos
    // no necesito metodos extra para esta clase.
}
