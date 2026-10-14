package com.example.sistemafidelizacion.Control;

import com.example.sistemafidelizacion.DAO.BeneficioAplicadoDAO;
import com.example.sistemafidelizacion.DAO.BeneficioDAO;
import com.example.sistemafidelizacion.DAO.ClienteDAO;
import com.example.sistemafidelizacion.DAO.CompraDAO;
import com.example.sistemafidelizacion.DAO.ErrorAccesoDatosException;
import com.example.sistemafidelizacion.Modelo.Beneficio;
import com.example.sistemafidelizacion.Modelo.BeneficioAplicado;
import com.example.sistemafidelizacion.Modelo.Cliente;
import com.example.sistemafidelizacion.Modelo.Compra;
import java.time.LocalDateTime;
import java.util.List;

public class GestorAplicacionBeneficios {
    private final BeneficioAplicadoDAO beneficioAplicadoDAO;
    private final BeneficioDAO beneficioDAO;
    private final ClienteDAO clienteDAO;
    private final CompraDAO compraDAO;

    public GestorAplicacionBeneficios() {
        this(new BeneficioAplicadoDAO(), new BeneficioDAO(), new ClienteDAO(), new CompraDAO());
    }

    public GestorAplicacionBeneficios(BeneficioAplicadoDAO beneficioAplicadoDAO, BeneficioDAO beneficioDAO,
            ClienteDAO clienteDAO, CompraDAO compraDAO) {
        this.beneficioAplicadoDAO = beneficioAplicadoDAO;
        this.beneficioDAO = beneficioDAO;
        this.clienteDAO = clienteDAO;
        this.compraDAO = compraDAO;
    }

    public List<Beneficio> evaluarBeneficiosDisponibles(int dniCliente) throws ErrorOperacionException {
        try {
            Cliente cliente = clienteDAO.buscarPorDni(dniCliente);
            if (cliente == null) {
                throw new ValidacionException("No existe un cliente con el DNI indicado.");
            }
            return beneficioDAO.listarActivos();
        } catch (ErrorAccesoDatosException e) {
            throw new ErrorOperacionException("No se pudieron evaluar los beneficios disponibles.", e);
        }
    }

    public boolean aplicarBeneficio(int idBeneficio, int idCompra, int dniCliente)
            throws ErrorOperacionException {
        try {
            Beneficio beneficio = beneficioDAO.buscarPorId(idBeneficio);
            if (beneficio == null || !beneficio.isActivo()) {
                throw new ValidacionException("El beneficio no existe o no está activo.");
            }
            Compra compra = buscarCompraDelCliente(idCompra, dniCliente);
            return insertarAplicacion(beneficio, compra);
        } catch (ErrorAccesoDatosException e) {
            throw new ErrorOperacionException("No se pudo aplicar el beneficio.", e);
        }
    }

    public boolean aplicarBeneficioCumpleanos(int dniCliente, int idCompra)
            throws ErrorOperacionException {
        try {
            Cliente cliente = clienteDAO.buscarPorDni(dniCliente);
            if (cliente == null) {
                throw new ValidacionException("No existe un cliente con el DNI indicado.");
            }
            LocalDateTime ahora = LocalDateTime.now();
            if (!validarCumpleanosYDisponibilidad(cliente, ahora.getYear())) {
                throw new ValidacionException(
                        "El beneficio de cumpleaños no está disponible o ya fue utilizado este año.");
            }
            Beneficio beneficioCumpleanos = beneficioDAO.listarActivos().stream()
                    .filter(b -> "Descuento Cumpleaños".equalsIgnoreCase(b.getNombre()))
                    .findFirst()
                    .orElseThrow(() -> new ValidacionException("No existe un beneficio de cumpleaños activo."));
            Compra compra = buscarCompraDelCliente(idCompra, dniCliente);
            return insertarAplicacion(beneficioCumpleanos, compra);
        } catch (ErrorAccesoDatosException e) {
            throw new ErrorOperacionException("No se pudo aplicar el beneficio de cumpleaños.", e);
        }
    }

    private boolean validarCumpleanosYDisponibilidad(Cliente cliente, int anioActual)
            throws ErrorOperacionException {
        try {
            return cliente.getFechaNacimiento().getYear() <= anioActual
                    && cliente.getFechaNacimiento().getMonthValue() == LocalDateTime.now().getMonthValue()
                    && !beneficioAplicadoDAO.verificarUsoCumpleanos(cliente.getDni(), anioActual);
        } catch (ErrorAccesoDatosException e) {
            throw new ErrorOperacionException("No se pudo validar el beneficio de cumpleaños.", e);
        }
    }

    private Compra buscarCompraDelCliente(int idCompra, int dniCliente)
            throws ErrorAccesoDatosException, ValidacionException {
        return compraDAO.obtenerHistorialCliente(dniCliente).stream()
                .filter(compra -> compra.getIdCompra() == idCompra)
                .findFirst()
                .orElseThrow(() -> new ValidacionException("La compra no existe o no pertenece al cliente."));
    }

    private boolean insertarAplicacion(Beneficio beneficio, Compra compra)
            throws ErrorAccesoDatosException {
        BeneficioAplicado aplicado = new BeneficioAplicado(
                0, beneficio, compra, LocalDateTime.now());
        boolean insertado = beneficioAplicadoDAO.insertar(aplicado);
        if (insertado) {
            compra.agregarBeneficio(aplicado);
        }
        return insertado;
    }
}
