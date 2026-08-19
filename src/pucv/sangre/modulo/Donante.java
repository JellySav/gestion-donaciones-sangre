package pucv.sangre.modulo;

// Importar utilidades de Java Collections Framework (JCF) (SIA-4) 
import java.time.LocalDate;

/**
 * Representa a un donante registrado en el sistema.
 * Tiene como variables de instancia -> rut, nombre, edad, grupoSanguineo y facto Rh
 * Estos dos ultimos se mostraran de forma conjunta.
 */
public class Donante {
    private String rut;
    private String nombre;
    private String grupoSanguineo;  // "A", "B", "AB", "O"
    private String factorRh;        // "+", "-"
    private int edad;
    private String telefono;
    private LocalDate ultimaFechaDonacion;

    public Donante(String rut, String nombre, String grupoSanguineo, String factorRh, int edad, String telefono, LocalDate ultimaFechaDonacion) {
        this.rut = rut;
        this.nombre = nombre;
        this.grupoSanguineo = grupoSanguineo;
        this.factorRh = factorRh;
        this.edad = edad;
        this.telefono = telefono;
        this.ultimaFechaDonacion = ultimaFechaDonacion;
    }

    // Sobrecarga de constructor para registros rápidos sin historial de donación previo
    public Donante(String rut, String nombre, String grupoSanguineo, String factorRh, int edad, String telefono) {
        this(rut, nombre, grupoSanguineo, factorRh, edad, telefono, null);
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
        return String.format("%s | %s | %s%s | %d años | Tel: %s | Últ. Donación: %s", 
                rut, nombre, grupoSanguineo, factorRh, edad, 
                (telefono != null ? telefono : "Sin tel"), 
                (ultimaFechaDonacion != null ? ultimaFechaDonacion : "Sin registros"));
    }
}
