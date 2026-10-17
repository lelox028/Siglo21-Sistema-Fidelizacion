package com.example.sistemafidelizacion.Control;

import com.example.sistemafidelizacion.DAO.CompraDAO;
import com.example.sistemafidelizacion.DAO.ErrorAccesoDatosException;
import com.example.sistemafidelizacion.Modelo.Compra;
import java.time.LocalDate;
import java.util.List;

public class GestorReportes {
    private final CompraDAO compraDAO;

    public GestorReportes() {
        this(new CompraDAO());
    }

    public GestorReportes(CompraDAO compraDAO) {
        this.compraDAO = compraDAO;
    }

    public List<Compra> generarHistorialGeneral(LocalDate fechaDesde, LocalDate fechaHasta)
            throws ErrorOperacionException {
        if (fechaDesde == null || fechaHasta == null || fechaDesde.isAfter(fechaHasta)) {
            throw new ValidacionException("El rango de fechas no es válido.");
        }
        try {
            return compraDAO.obtenerEntre(
                    fechaDesde.atStartOfDay(),
                    fechaHasta.plusDays(1).atStartOfDay().minusNanos(1));
        } catch (ErrorAccesoDatosException e) {
            throw new ErrorOperacionException("No se pudo generar el historial general.", e);
        }
    }

    public List<Compra> generarReporte(LocalDate fechaDesde, LocalDate fechaHasta,
            Double montoMinimo, Double montoMaximo, boolean soloBeneficiosActivos)
            throws ErrorOperacionException {
        if (fechaDesde != null && fechaHasta != null && fechaDesde.isAfter(fechaHasta)) {
            throw new ValidacionException("El rango de fechas no es válido.");
        }
        if (montoMinimo != null && montoMaximo != null && montoMinimo > montoMaximo) {
            throw new ValidacionException("El rango de montos no es válido.");
        }
        try {
            return compraDAO.obtenerParaReporte(
                    fechaDesde == null ? null : fechaDesde.atStartOfDay(),
                    fechaHasta == null ? null : fechaHasta.plusDays(1).atStartOfDay().minusNanos(1),
                    montoMinimo, montoMaximo, soloBeneficiosActivos);
        } catch (ErrorAccesoDatosException e) {
            throw new ErrorOperacionException("No se pudo generar el reporte.", e);
        }
    }
}
