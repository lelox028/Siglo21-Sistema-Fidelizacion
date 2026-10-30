package com.example.sistemafidelizacion.Control;

import com.example.sistemafidelizacion.DAO.CompraDAO;
import com.example.sistemafidelizacion.DAO.ErrorAccesoDatosException;
import com.example.sistemafidelizacion.Modelo.Compra;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import java.util.HashMap;

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

    public Reporte generarReporte(String mostrar, String agruparPor, LocalDate desde,
                LocalDate hasta, Double montoMin, Double montoMax, boolean soloBeneficiosActivos)
                throws ErrorOperacionException {
            if (desde != null && hasta != null && desde.isAfter(hasta)) {
                throw new ValidacionException("El rango de fechas no es válido.");
    }
            if (montoMin != null && montoMax != null && montoMin > montoMax) {
                throw new ValidacionException("El rango de montos no es válido.");
            }
            try {
                List<Compra> compras = compraDAO.obtenerParaReporte(
                        desde == null ? null : desde.atStartOfDay(),
                        hasta == null ? null : hasta.plusDays(1).atStartOfDay().minusNanos(1));
                compras.removeIf(c -> (montoMin != null && c.getMonto() < montoMin)
                        || (montoMax != null && c.getMonto() > montoMax)
                        || (soloBeneficiosActivos && (c.getBeneficiosAplicados().isEmpty()
                        || c.getBeneficiosAplicados().stream()
                        .anyMatch(b -> !b.getBeneficio().isActivo()))));
                return construirReporte(mostrar, agruparPor, compras);
            } catch (ErrorAccesoDatosException e) {
                throw new ErrorOperacionException("No se pudo generar el reporte.", e);
            }
        }

        private Reporte construirReporte(String mostrar, String agruparPor, List<Compra> compras) {
            List<Object[]> filas = new ArrayList<>();
            if ("Clientes".equals(mostrar) && "Nombre del cliente".equals(agruparPor)) {
                for (Compra compra : compras) {
                    filas.add(new Object[] {compra.getCliente().getNombreCompleto(),
                            compra.getCliente().getDni(), compra.getFechaHora(), compra.getMonto(),
                            compra.getBeneficiosAplicados().size()});
                }
                return new Reporte(new String[] {"Cliente", "DNI", "Fecha", "Monto", "Beneficios"},
                        filas, compras);
            }
            if ("Beneficios".equals(mostrar)) {
                return construirReporteBeneficios(agruparPor, compras);
            }
            if ("Clientes".equals(mostrar)) {
                Map<Integer, Boolean> clientes = new LinkedHashMap<>();
                Map<Integer, Double> totales = new HashMap<>();
                for (Compra compra : compras) {
                    int dni = compra.getCliente().getDni();
                    clientes.put(dni, clientes.getOrDefault(dni, false)
                            || !compra.getBeneficiosAplicados().isEmpty());
                    totales.put(dni, totales.getOrDefault(dni, 0.0) + compra.getMonto());
                }
                Map<String, Resumen> resumenClientes = new LinkedHashMap<>();
                clientes.forEach((dni, uso) -> {
                    String clave = uso ? "Usaron beneficios" : "No usaron beneficios";
                    Resumen resumen = resumenClientes.computeIfAbsent(clave, k -> new Resumen());
                    resumen.cantidad++;
                    resumen.total += totales.get(dni);
                });
                for (Map.Entry<String, Resumen> entry : resumenClientes.entrySet()) {
                    filas.add(new Object[] {entry.getKey(), entry.getValue().cantidad,
                            entry.getValue().total});
                }
                return new Reporte(new String[] {"Grupo", "Clientes", "Monto total"}, filas, compras);
            }

            Map<String, Resumen> agrupados = new LinkedHashMap<>();
            for (Compra compra : compras) {
                String clave = obtenerClave(mostrar, agruparPor, compra);
                Resumen resumen = agrupados.computeIfAbsent(clave, k -> new Resumen());
                resumen.cantidad++;
                resumen.total += compra.getMonto();
            }
            for (Map.Entry<String, Resumen> entry : agrupados.entrySet()) {
                filas.add(new Object[] {entry.getKey(), entry.getValue().cantidad,
                        entry.getValue().total});
            }
            return new Reporte(new String[] {"Grupo", "Cantidad", "Monto total"}, filas, compras);
        }

        private Reporte construirReporteBeneficios(String agruparPor, List<Compra> compras) {
            Map<String, Integer> usosPorBeneficio = new LinkedHashMap<>();
            Map<String, Boolean> estadoPorBeneficio = new LinkedHashMap<>();
            for (Compra compra : compras) {
                compra.getBeneficiosAplicados().forEach(aplicado -> {
                    String nombre = aplicado.getBeneficio().getNombre();
                    usosPorBeneficio.put(nombre, usosPorBeneficio.getOrDefault(nombre, 0) + 1);
                    estadoPorBeneficio.put(nombre, aplicado.getBeneficio().isActivo());
                });
            }
            Map<String, Integer> grupos = new LinkedHashMap<>();
            usosPorBeneficio.forEach((nombre, usos) -> {
                String clave;
                if ("Activos vs Inactivos".equals(agruparPor)) {
                    clave = estadoPorBeneficio.get(nombre) ? "Activos" : "Inactivos";
                } else if (usos <= 10) {
                    clave = "10 o menos";
                } else if (usos < 100) {
                    clave = "Entre 10 y 100";
                } else {
                    clave = "100 o más";
                }
                grupos.put(clave, grupos.getOrDefault(clave, 0) + usos);
            });
            List<Object[]> filas = new ArrayList<>();
            grupos.forEach((grupo, usos) -> filas.add(new Object[] {grupo, usos}));
            return new Reporte(new String[] {"Grupo", "Usos de beneficios"}, filas, compras);
        }

        private String obtenerClave(String mostrar, String agruparPor, Compra compra) {
            if ("Compras".equals(mostrar)) {
                if ("Monto".equals(agruparPor)) {
                    if (compra.getMonto() <= 10_000) return "$0 - $10.000";
                    if (compra.getMonto() <= 50_000) return "$10.000,01 - $50.000";
                    return "Más de $50.000";
                }
                if ("Cantidad de beneficios".equals(agruparPor)) {
                    int cantidad = compra.getBeneficiosAplicados().size();
                    return cantidad == 0 ? "0" : cantidad == 1 ? "1" : "2 o más";
                }
                LocalTime hora = compra.getFechaHora().toLocalTime();
                if (hora.isBefore(LocalTime.NOON)) return "Mañana";
                if (hora.isBefore(LocalTime.of(19, 0))) return "Tarde";
                return "Noche";
            }
            if ("Clientes".equals(mostrar)) {
                return compra.getBeneficiosAplicados().isEmpty()
                        ? "No usaron beneficios" : "Usaron beneficios";
            }
            if ("Activos vs Inactivos".equals(agruparPor)) {
                return compra.getBeneficiosAplicados().stream().anyMatch(b -> !b.getBeneficio().isActivo())
                        ? "Inactivos" : "Activos";
            }
            int cantidadUsos = compra.getBeneficiosAplicados().size();
            if (cantidadUsos <= 10) return "10 o menos";
            if (cantidadUsos < 100) return "Entre 10 y 100";
            return "100 o más";
        }

        private static class Resumen {
            int cantidad;
            double total;
        }

        public static class Reporte {
            private final String[] columnas;
            private final List<Object[]> filas;
            private final List<Compra> compras;

            public Reporte(String[] columnas, List<Object[]> filas, List<Compra> compras) {
                this.columnas = columnas;
                this.filas = filas;
                this.compras = compras;
            }
            public String[] getColumnas() { return columnas; }
            public List<Object[]> getFilas() { return filas; }
            public List<Compra> getCompras() { return compras; }
        }
    }
