package com.example.sistemafidelizacion.Control;

import com.example.sistemafidelizacion.DAO.ClienteDAO;
import com.example.sistemafidelizacion.DAO.ErrorAccesoDatosException;
import com.example.sistemafidelizacion.Modelo.Cliente;
import java.time.LocalDate;

// Capa de Control o logica, en este caso actua como un hibrido entre service y controller en Springboot.
public class GestorClientes {
    private final ClienteDAO clienteDAO;

    public GestorClientes() {
        this(new ClienteDAO());
    }

    public GestorClientes(ClienteDAO clienteDAO) {
        this.clienteDAO = clienteDAO;
    }

    public boolean registrarCliente(int dni, String nombre, String tel, String email, LocalDate fechaNac)
            throws ErrorOperacionException {
        // Validar los datos del cliente antes de registrarlo
        validarCliente(dni, nombre, tel, email, fechaNac);
        if (!validarUnicidadDNI(dni)) {
            throw new ValidacionException("Ya existe un cliente con el DNI indicado.");
        }

        // aqui se trata la excepcion de acceso a datos definida en el DAO y se lanza una excepcion de operacion, para que la capa superior (controlador o vista) pueda manejarla de manera uniforme.
        try {
            return clienteDAO.insertar(new Cliente(dni, nombre, tel, email, fechaNac));
        } catch (ErrorAccesoDatosException e) {
            throw new ErrorOperacionException("No se pudo registrar el cliente.", e);
        }
    }

    public boolean modificarDatosCliente(Cliente cliente) throws ErrorOperacionException {
        if (cliente == null) {
            throw new ValidacionException("El cliente no puede ser nulo.");
        }
        validarCliente(cliente.getDni(), cliente.getNombreCompleto(), cliente.getTelefono(),
                cliente.getEmail(), cliente.getFechaNacimiento());
        try {
            if (clienteDAO.buscarPorDni(cliente.getDni()) == null) {
                throw new ValidacionException("No existe un cliente con el DNI indicado.");
            }
            return clienteDAO.actualizar(cliente);
        } catch (ErrorAccesoDatosException e) {
            throw new ErrorOperacionException("No se pudo modificar el cliente.", e);
        }
    }

    public Cliente buscarCliente(int dni) throws ErrorOperacionException {
        validarDni(dni);
        try {
            return clienteDAO.buscarPorDni(dni);
        } catch (ErrorAccesoDatosException e) {
            throw new ErrorOperacionException("No se pudo encontrar el cliente.", e);
        }
    }


    // metodos auxiliares para validar datos.


    // privado o publico??
    private boolean validarUnicidadDNI(int dni) throws ErrorOperacionException {
        validarDni(dni);
        try {
            return clienteDAO.buscarPorDni(dni) == null;
        } catch (ErrorAccesoDatosException e) {
            throw new ErrorOperacionException("No se pudo validar la unicidad del DNI.", e);
        }
    }

    // si el cliente es nulo o alguno de sus datos es nulo o vacio, lanza una
    // excepcion de validacion. de esta manera nos evitamos un tipo de retorno y no
    // necesitamos leer el resultado de la validacion, ya que si no lanza excepcion,
    // es valido.
    private void validarCliente(int dni, String nombre, String telefono, String email, LocalDate fechaNacimiento)
            throws ValidacionException {
        validarDni(dni);
        if (nombre == null || nombre.isBlank() || telefono == null || telefono.isBlank()
                || email == null || email.isBlank() || fechaNacimiento == null) {
            throw new ValidacionException("Todos los datos del cliente son obligatorios.");
        }
    }

    // valida que el dni sea un numero de 7 u 8 digitos, si no lo es lanza una
    // excepcion de validacion. usa la misma logica que la validacion anterior, si
    // no lanza excepcion, es valido.
    private void validarDni(int dni) throws ValidacionException {
        if (dni < 1_000_000 || dni > 99_999_999) {
            throw new ValidacionException("El DNI debe tener entre 7 y 8 dígitos.");
        }
    }
}
