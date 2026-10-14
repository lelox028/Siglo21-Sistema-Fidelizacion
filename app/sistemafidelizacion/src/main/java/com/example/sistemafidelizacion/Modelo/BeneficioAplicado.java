package com.example.sistemafidelizacion.Modelo;

import java.time.LocalDateTime;

public class BeneficioAplicado {
    // estructura
    private int idBeneficioAplicado;
    private Beneficio beneficio;
    private Compra compra;
    private LocalDateTime fechaAplicacion;

    // constructor
    public BeneficioAplicado() {
    }

    public BeneficioAplicado(int idBeneficioAplicado, Beneficio beneficio, Compra compra,
            LocalDateTime fechaAplicacion) {
        this.idBeneficioAplicado = idBeneficioAplicado;
        this.beneficio = beneficio;
        this.compra = compra;
        this.fechaAplicacion = fechaAplicacion;
    }

    // getters y setters

    public int getIdBeneficioAplicado() {
        return idBeneficioAplicado;
    }

    public void setIdBeneficioAplicado(int idBeneficioAplicado) {
        this.idBeneficioAplicado = idBeneficioAplicado;
    }

    public Beneficio getBeneficio() {
        return beneficio;
    }

    public void setBeneficio(Beneficio beneficio) {
        this.beneficio = beneficio;
    }

    public Compra getCompra() {
        return compra;
    }

    public void setCompra(Compra compra) {
        this.compra = compra;
    }

    public LocalDateTime getFechaAplicacion() {
        return fechaAplicacion;
    }

    public void setFechaAplicacion(LocalDateTime fechaAplicacion) {
        this.fechaAplicacion = fechaAplicacion;
    }

    // toString
    @Override
    public String toString() {
        return "BeneficioAplicado{" +
                "idBeneficioAplicado=" + idBeneficioAplicado +
                ", beneficio=" + beneficio +
                ", compra=" + compra +
                ", fechaAplicacion=" + fechaAplicacion +
                '}';
    }

    // metodos especificos
    // no necesito metodos especificos para esta clase.
}
