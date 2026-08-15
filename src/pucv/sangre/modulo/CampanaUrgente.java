package pucv.sangre.modulo;

public class CampanaUrgente extends Campana {
    private String nivelUrgencia; // Seria en formato "ALTA", "CRITICA"

    
    public CampanaUrgente(String codigo, String nombre, String lugar, String nivelUrgencia) {
        super(codigo, nombre, lugar);
        this.nivelUrgencia = nivelUrgencia;
    }

    // Sobreescritura del método heredado (SIA-6)
    @Override
    public String getNombre() {
        return super.getNombre() + " [URGENCIA: " + nivelUrgencia + "]";
    }

    public String getNivelUrgencia() {
        return nivelUrgencia;
    }
}