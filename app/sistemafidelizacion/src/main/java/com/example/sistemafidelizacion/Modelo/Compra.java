package com.example.sistemafidelizacion.Modelo;
import java.util.Date;
import java.util.List;

public class Compra {
    //estructura
    private int idCompra;
    private Date fechaHora;
    private double monto;
    private Cliente cliente;
    private List<Beneficio> beneficiosAplicados;


    // constructor
    public Compra() {
    }

    public Compra(int idCompra, Date fechaHora, double monto, Cliente cliente, List<Beneficio> beneficiosAplicados) {
        this.idCompra = idCompra;
        this.fechaHora = fechaHora;
        this.monto = monto;
        this.cliente = cliente;
        this.beneficiosAplicados = beneficiosAplicados;
    }

    //getters y setters
    public int getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(int idCompra) {
        this.idCompra = idCompra;
    }

    public Date getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(Date fechaHora) {
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

    public List<Beneficio> getBeneficiosAplicados() {
        return beneficiosAplicados;
    }

    public void setBeneficiosAplicados(List<Beneficio> beneficiosAplicados) {
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

    public void agregarBeneficio(Beneficio beneficio) {
        beneficiosAplicados.add(beneficio);
    }
}
