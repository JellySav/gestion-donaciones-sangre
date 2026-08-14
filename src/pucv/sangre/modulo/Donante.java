package pucv.sangre.modulo;

/**
 * Representa a un donante registrado en el sistema.
 * Tiene como variables de instancia -> rut, nombre, edad, grupoSanguineo y facto Rh
 * Estos dos ultimos se mostraran de forma conjunta.
 */
public class Donante {
    private String rut;              // Se pasaria a String, no importaria el formato.
    private String nombre;
    private String grupoSanguineo;   // "A", "B", "AB", "O"
    private String factorRh;        // "+", "-" -> Factor a identificar tipo 
    private int edad;

    public Donante(String rut, String nombre, String grupoSanguineo, String factorRh, int edad) {
        this.rut = rut;
        this.nombre = nombre;
        this.grupoSanguineo = grupoSanguineo;
        this.factorRh = factorRh;
        this.edad = edad;
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


    // Sobreescritura -> Convertir todo a String (SIA-5) 
    @Override
    public String toString() {
        return "RUT: " + rut + " | Nombre: " + nombre + " | Tipo: " + grupoSanguineo + factorRh + " | Edad: " + edad;
    }
}
