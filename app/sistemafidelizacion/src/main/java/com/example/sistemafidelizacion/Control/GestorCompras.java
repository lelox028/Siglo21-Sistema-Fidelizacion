package com.example.sistemafidelizacion.Control;

import com.example.sistemafidelizacion.DAO.ClienteDAO;
import com.example.sistemafidelizacion.DAO.BeneficioAplicadoDAO;
import com.example.sistemafidelizacion.DAO.CompraDAO;
import com.example.sistemafidelizacion.DAO.ErrorAccesoDatosException;
import com.example.sistemafidelizacion.Modelo.Beneficio;
import com.example.sistemafidelizacion.Modelo.BeneficioAplicado;
import com.example.sistemafidelizacion.Modelo.Cliente;
import com.example.sistemafidelizacion.Modelo.Compra;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

public class GestorCompras {
    private final CompraDAO compraDAO;
    private final ClienteDAO clienteDAO;
    private final BeneficioAplicadoDAO beneficioAplicadoDAO;

    public GestorCompras() {
        this(new CompraDAO(), new ClienteDAO(), new BeneficioAplicadoDAO());
    }

    public GestorCompras(CompraDAO compraDAO, ClienteDAO clienteDAO) {
        this(compraDAO, clienteDAO, new BeneficioAplicadoDAO());
    }

    public GestorCompras(CompraDAO compraDAO, ClienteDAO clienteDAO,
            BeneficioAplicadoDAO beneficioAplicadoDAO) {
        this.compraDAO = compraDAO;
        this.clienteDAO = clienteDAO;
        this.beneficioAplicadoDAO = beneficioAplicadoDAO;
    }

    public Compra registrarCompra(int dniCliente, double monto, LocalDateTime fechaHora)
            throws ErrorOperacionException {
        return registrarCompra(dniCliente, monto, fechaHora, List.of());
    }

    public boolean clienteUsoBeneficioCumpleanos(int dniCliente)
            throws ErrorOperacionException {
        try {
            return beneficioAplicadoDAO.verificarUsoCumpleanos(
                    dniCliente, LocalDate.now().getYear());
        } catch (ErrorAccesoDatosException e) {
            throw new ErrorOperacionException(
                    "No se pudo verificar el uso del beneficio de cumpleaños.", e);
        }
    }

    public boolean clienteUsoBeneficioCumpleanos(int dniCliente, int anio)
            throws ErrorOperacionException {
        try {
            return beneficioAplicadoDAO.verificarUsoCumpleanos(
                    dniCliente, anio);
        } catch (ErrorAccesoDatosException e) {
            throw new ErrorOperacionException(
                    "No se pudo verificar el uso del beneficio de cumpleaños.", e);
        }
    }

    public Compra registrarCompra(int dniCliente, double monto, LocalDateTime fechaHora,
            List<Beneficio> beneficios) throws ErrorOperacionException {
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
            for (Beneficio beneficio : beneficios) {
                BeneficioAplicado aplicado = new BeneficioAplicado(
                        0, beneficio, compra, fechaHora);
                if (!beneficioAplicadoDAO.insertar(aplicado)) {
                    throw new ErrorOperacionException(
                            "No se pudo registrar el beneficio aplicado.");
                }
                compra.agregarBeneficio(aplicado);
            }
            return compra;
        } catch (ErrorAccesoDatosException e) {
            throw new ErrorOperacionException("No se pudo registrar la compra.", e);
        }
    }
}
