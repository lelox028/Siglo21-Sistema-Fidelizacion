package com.example.sistemafidelizacion.Modelo;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Compra {
    //estructura
    private int idCompra;
    private LocalDateTime fechaHora;
    private double monto;
    private Cliente cliente;
    private List<BeneficioAplicado> beneficiosAplicados;


    // constructor
    public Compra() {
    }

    public Compra(int idCompra, LocalDateTime fechaHora, double monto, Cliente cliente,
            List<BeneficioAplicado> beneficiosAplicados) {
        this.idCompra = idCompra;
        this.fechaHora = fechaHora;
        this.monto = monto;
        this.cliente = cliente;
        this.beneficiosAplicados = beneficiosAplicados == null ? new ArrayList<>() : beneficiosAplicados;
    }

    //getters y setters
    public int getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(int idCompra) {
        this.idCompra = idCompra;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public List<BeneficioAplicado> getBeneficiosAplicados() {
        return beneficiosAplicados;
    }

    public void setBeneficiosAplicados(List<BeneficioAplicado> beneficiosAplicados) {
        this.beneficiosAplicados = beneficiosAplicados;
    }

    //toString

    @Override
    public String toString() {
        return "Compra{" +
                "idCompra=" + idCompra +
                ", fechaHora=" + fechaHora +
                ", monto=" + monto +
                ", cliente=" + cliente +
                ", beneficiosAplicados=" + beneficiosAplicados +
                '}';
    }

    // metodos especificos

    public void agregarBeneficio(BeneficioAplicado beneficioAplicado) {
        if (beneficioAplicado == null) {
            throw new IllegalArgumentException("El beneficio aplicado no puede ser nulo.");
        }
        beneficiosAplicados.add(beneficioAplicado);
    }
}
