package pucv.sangre.modulo;

// Importar excepciones personalizadas (SIA-12)
import pucv.sangre.excepciones.DonanteInvalidoException;

// Importar utilidades de Java Collections Framework (JCF) (SIA-4) 
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Modelo de Campaña. Representa una campaña de donacion en donde contiene sus donantes en una List en lugar de Map.
 */
public class Campana {
    private String codigo;
    private String nombre;
    private String lugar;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private final List<Donante> donantes;

    public Campana(String codigo, String nombre, String lugar, LocalDate fechaInicio, LocalDate fechaFin) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.lugar = lugar;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.donantes = new ArrayList<>();
    }

    // Sobrecarga de constructor para recibir fechas como String (Del tipo "2026-08-17")
    public Campana(String codigo, String nombre, String lugar, String fechaInicio, String fechaFin) {
        this(codigo, nombre, lugar, LocalDate.parse(fechaInicio), LocalDate.parse(fechaFin));
    }

    // Getters y Setters (SIA-3)

    public String getCodigo() { 
        return codigo; 
    }

    public void setCodigo(String codigo) { 
        this.codigo = codigo; 
    }

    public String getNombre() {
        return nombre; 
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre; 
    }

    public String getLugar() {
        return lugar; 
    }
    
    public void setLugar(String lugar) {
        this.lugar = lugar; 
    }

    public LocalDate getFechaInicio() {
        return fechaInicio; 
    }
    
    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio; 
    }

    public LocalDate getFechaFin() {
        return fechaFin; 
    }
    
    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin; 
    }

    public List<Donante> getDonantes() {
        return donantes; 
    }

    
    // Métodos de gestión para la colección anidada

    // Versión 1: Recibe el objeto instanciado
    public void agregarDonante(Donante donante) throws DonanteInvalidoException {
        if (donante == null) {
            throw new DonanteInvalidoException("El donante no puede ser nulo.");
        }
        // Validación de duplicidad por RUT en la List
        for (Donante d : donantes) {
            if (d.getRut().equalsIgnoreCase(donante.getRut())) {
                throw new DonanteInvalidoException("Ya existe un donante con el RUT " + donante.getRut() + " en esta campaña.");
            }
        }
        this.donantes.add(donante);
    }

    // Versión 2: Recibe atributos primitivos (Sobrecarga SIA-5)
    public void agregarDonante(String rut, String nombre, String grupoSanguineo, String factorRh, int edad, String telefono) throws DonanteInvalidoException {
        Donante nuevo = new Donante(rut, nombre, grupoSanguineo, factorRh, edad, telefono);
        this.agregarDonante(nuevo);
    }

    // Búsqueda de donante por RUT en la lista
    public Donante buscarDonante(String rut) {
        for (Donante d : donantes) {
            if (d.getRut().equalsIgnoreCase(rut)) {
                return d;
            }
        }
        return null;
    }

    // Eliminación de donante por RUT en la lista
    public boolean eliminarDonante(String rut) {
        return this.donantes.removeIf(d -> d.getRut().equalsIgnoreCase(rut));
    }



    // Estado de la campaña -> Comprobacion para funcion propia y busqueda para el hashmap

    /**
     * Evalua si la campaña está actualmente activa con base en la fecha actual.
     */
    public boolean esActiva() {
        LocalDate hoy = LocalDate.now();
        return (hoy.isEqual(fechaInicio) || hoy.isAfter(fechaInicio)) && 
               (hoy.isEqual(fechaFin) || hoy.isBefore(fechaFin));
    }

    /**
     * Retorna el estado descriptivo de la campaña según el rango de fechas.
     */
    public String getEstado() {
        LocalDate hoy = LocalDate.now();
        if (hoy.isBefore(fechaInicio)) {
            return "Programada";
        } else if (esActiva()) {
            return "Activa";
        } else {
            return "Finalizada";
        }
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s | Período: %s a %s | Estado: %s | Donantes: %d", 
                codigo, nombre, lugar, fechaInicio, fechaFin, getEstado(), donantes.size());
    }
}