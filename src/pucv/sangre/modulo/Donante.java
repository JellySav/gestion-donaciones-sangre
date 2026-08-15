package pucv.sangre.modulo;

// Importar utilidades de Java Collections Framework (JCF) (SIA-4) 
import java.time.LocalDate;

/**
 * Representa a un donante registrado en el sistema.
 * Tiene como variables de instancia -> rut, nombre, edad, grupoSanguineo y facto Rh
 * Estos dos ultimos se mostraran de forma conjunta.
 */
public class Donante {
    private String rut;   // Se pasaria a String, no importaria el formato.
    private String nombre;
    private String grupoSanguineo;  // "A", "B", "AB", "O"
    private String factorRh;    // "+", "-" -> Factor a identificar  
    private int edad;
    private String telefono;  // Utilizar para reseva sanguinea
    private LocalDate ultimaFechaDonacion; // Se usa java.time.LocalDate para evaluar elegibilidad

    public Donante(String rut, String nombre, String grupo, String rh, int edad, String telefono, LocalDate ultimaFechaDonacion) {
        this.rut = rut;
        this.nombre = nombre;
        this.grupo = grupo;
        this.rh = rh;
        this.edad = edad;
        this.telefono = telefono;
        this.ultimaFechaDonacion = ultimaFechaDonacion;
    }

    // Getters y Setters (SIA-3) 
    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getGrupoSanguineo() {
        return grupoSanguineo;
    }

    public void setGrupoSanguineo(String grupoSanguineo) {
        this.grupoSanguineo = grupoSanguineo;
    }

    public String getFactorRh() {
        return factorRh;
    }

    public void setFactorRh(String factorRh) {
        this.factorRh = factorRh;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public LocalDate getUltimaFechaDonacion() { 
        return ultimaFechaDonacion; 
    }
    
    public void setUltimaFechaDonacion(LocalDate ultimaFechaDonacion) {
        this.ultimaFechaDonacion = ultimaFechaDonacion; 
    }


    // Sobreescritura -> Convertir todo a String (SIA-5) 
    @Override
    public String toString() {
        return String.format("%s | %s | %s%s | %d años | Tel: %s | Últ. Donación: %s", rut, nombre, grupo, rh, edad, telefono, (ultimaFechaDonacion != null ? ultimaFechaDonacion : "Sin registros"));
    }
}
