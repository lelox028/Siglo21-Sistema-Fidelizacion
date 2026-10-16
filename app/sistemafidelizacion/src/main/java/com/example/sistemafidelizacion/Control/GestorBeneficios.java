package com.example.sistemafidelizacion.Control;

import com.example.sistemafidelizacion.DAO.BeneficioDAO;
import com.example.sistemafidelizacion.DAO.ErrorAccesoDatosException;
import com.example.sistemafidelizacion.Modelo.Beneficio;
import java.util.List;

public class GestorBeneficios {
    private final BeneficioDAO beneficioDAO;

    public GestorBeneficios() {
        this(new BeneficioDAO());
    }

    public GestorBeneficios(BeneficioDAO beneficioDAO) {
        this.beneficioDAO = beneficioDAO;
    }

    public boolean crearBeneficio(String nombre, String descripcion) throws ErrorOperacionException {
        if (nombre == null || nombre.isBlank() || descripcion == null || descripcion.isBlank()) {
            throw new ValidacionException("El nombre y la descripción del beneficio son obligatorios.");
        }
        try {
            return beneficioDAO.insertar(new Beneficio(0, nombre, descripcion, true));
        } catch (ErrorAccesoDatosException e) {
            throw new ErrorOperacionException("No se pudo crear el beneficio.", e);
        }
    }

    public boolean modificarEstadoBeneficio(int idBeneficio, boolean activo) throws ErrorOperacionException {
        try {
            Beneficio beneficio = beneficioDAO.buscarPorId(idBeneficio);
            if (beneficio == null) {
                throw new ValidacionException("No existe el beneficio indicado.");
            }
            beneficio.setActivo(activo);
            return beneficioDAO.actualizar(beneficio);
        } catch (ErrorAccesoDatosException e) {
            throw new ErrorOperacionException("No se pudo modificar el estado del beneficio.", e);
        }
    }

    public boolean modificarBeneficio(Beneficio beneficio) throws ErrorOperacionException {
        if (beneficio == null || beneficio.getNombre() == null || beneficio.getNombre().isBlank()
                || beneficio.getDescripcion() == null || beneficio.getDescripcion().isBlank()) {
            throw new ValidacionException("El nombre y la descripción del beneficio son obligatorios.");
        }
        try {
            return beneficioDAO.actualizar(beneficio);
        } catch (ErrorAccesoDatosException e) {
            throw new ErrorOperacionException("No se pudo modificar el beneficio.", e);
        }
    }

    public List<Beneficio> consultarBeneficiosActivos() throws ErrorOperacionException {
        try {
            return beneficioDAO.listarActivos();
        } catch (ErrorAccesoDatosException e) {
            throw new ErrorOperacionException("No se pudieron consultar los beneficios activos.", e);
        }
    }

    public List<Beneficio> consultarTodosLosBeneficios() throws ErrorOperacionException {
        try {
            return beneficioDAO.listarTodos();
        } catch (ErrorAccesoDatosException e) {
            throw new ErrorOperacionException("No se pudieron consultar todos los beneficios.", e);
        }
    }
}
