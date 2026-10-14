package com.example.sistemafidelizacion.Control;

import com.example.sistemafidelizacion.DAO.ClienteDAO;
import com.example.sistemafidelizacion.DAO.CompraDAO;
import com.example.sistemafidelizacion.DAO.ErrorAccesoDatosException;
import com.example.sistemafidelizacion.Modelo.Cliente;
import com.example.sistemafidelizacion.Modelo.Compra;
import java.time.LocalDateTime;

public class GestorCompras {
    private final CompraDAO compraDAO;
    private final ClienteDAO clienteDAO;

    public GestorCompras() {
        this(new CompraDAO(), new ClienteDAO());
    }

    public GestorCompras(CompraDAO compraDAO, ClienteDAO clienteDAO) {
        this.compraDAO = compraDAO;
        this.clienteDAO = clienteDAO;
    }

    public Compra registrarCompra(int dniCliente, double monto, LocalDateTime fechaHora)
            throws ErrorOperacionException {
        if (monto <= 0) {
            throw new ValidacionException("El monto de la compra debe ser mayor a cero.");
        }
        if (fechaHora == null) {
            throw new ValidacionException("La fecha y hora de la compra son obligatorias.");
        }
        try {
            Cliente cliente = clienteDAO.buscarPorDni(dniCliente);
            if (cliente == null) {
                throw new ValidacionException("No existe un cliente con el DNI indicado.");
            }
            Compra compra = new Compra(0, fechaHora, monto, cliente, null);
            if (!compraDAO.insertar(compra)) {
                throw new ErrorOperacionException("No se pudo registrar la compra.");
            }
            return compra;
        } catch (ErrorAccesoDatosException e) {
            throw new ErrorOperacionException("No se pudo registrar la compra.", e);
        }
    }
}
