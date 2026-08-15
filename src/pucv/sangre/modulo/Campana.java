package pucv.sangre.modulo;

// Importar utilidades de Java Collections Framework (JCF) (SIA-4) 
import java.util.HashMap;     
import java.util.Map;        

/**
 * Representa una campaña de donación que contiene una colección de Donantes -> Una Colección Anidada.
 */

public class Campana {
    private String codigo;
    private String nombre;
    private String lugar;
    private Map<String, Donante> donantes; // Colección anidada (Mapa de Donantes)

    public Campana(String codigo, String nombre, String lugar) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.lugar = lugar;
        this.donantes = new HashMap<>();
    }

    // Métodos de gestión para la colección anidada
    public void agregarDonante(Donante donante) {
        if (donante != null) {
            this.donantes.put(donante.getRut(), donante);
        }
    }

    public Donante buscarDonante(String rut) {
        return this.donantes.get(rut);
    }

    public boolean eliminarDonante(String rut) {
        return this.donantes.remove(rut) != null;
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

    public Map<String, Donante> getDonantes() {
        return donantes;
    }

    public void setDonantes(Map<String, Donante> donantes) {
        this.donantes = donantes;
    }

    // Sobreescritura -> Para mostrar la campaña formateada en consola
    @Override
    public String toString() {
        return "[" + codigo + "] " + nombre + " - Lugar: " + lugar + " (" + donantes.size() + " donantes registrados)";
    }
}