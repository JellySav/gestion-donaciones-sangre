package pucv.sangre.modulo;

/**
 * Subclase que hereda de Campana (SIA-6)
 */

public class CampanaUrgente extends Campana {
    private String nivelUrgencia; // Seria en formato "ALTA", "CRITICA"


    public CampanaUrgente(String codigo, String nombre, String lugar, LocalDate fechaInicio, LocalDate fechaFin, String nivelUrgencia) {
        super(codigo, nombre, lugar, fechaInicio, fechaFin);
        this.nivelUrgencia = nivelUrgencia;
    }

    // Sobrecaega del constructor por fechas en formato String 
    public CampanaUrgente(String codigo, String nombre, String lugar, String fechaInicio, String fechaFin, String nivelUrgencia) {
        super(codigo, nombre, lugar, fechaInicio, fechaFin);
        this.nivelUrgencia = nivelUrgencia;
    }

    // Sobreescritura del método heredado (SIA-6)
    @Override
    public String getNombre() {
        return super.getNombre() + " [URGENCIA: " + nivelUrgencia + "]";
    }

    // Getters y Setters (SIA-3)
    public String getNivelUrgencia() {
        return nivelUrgencia;
    }

    public void setNivelUrgencia(String nivelUrgencia) {
        this.nivelUrgencia = nivelUrgencia;
    }
}