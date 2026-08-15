package pucv.sangre.modulo;

// Importar utilidades de Java Collections Framework (JCF) (SIA-4) 
import java.time.LocalDate;
import java.util.HashMap;     
import java.util.Map;        

/**
 * Representa una campaña de donación que contiene una colección de Donantes -> Una Colección Anidada.
*/

public class Campana {
    private String codigo;
    private String nombre;
    private String lugar;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private int metaUnidades;
    private Map<String, Donante> donantes; // Colección anidada (Mapa de Donantes)

    public Campana(String codigo, String nombre, String lugar, LocalDate fechaInicio, LocalDate fechaFin, int metaUnidades) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.lugar = lugar;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.metaUnidades = metaUnidades;
        this.donantes = new HashMap<>();
    }

    // Métodos de gestión para la colección anidada
    
    // Versión 1: Recibe el objeto instanciado
    public void agregarDonante(Donante donante) {
        if (donante != null) {
            this.donantes.put(donante.getRut(), donante);
        }
    }

    // Versión 2 : Recibe atributos primitivos (SIA-5) Sobrecarga
    public void agregarDonante(String rut, String nombre, String grupoSanguineo, String factorRh, int edad) {
        Donante nuevo = new Donante(rut, nombre, grupoSanguineo, factorRh, edad);
        this.donantes.put(rut, nuevo);
    }

    public Donante buscarDonante(String rut) {
        return this.donantes.get(rut);
    }

    public boolean eliminarDonante(String rut) {
        return this.donantes.remove(rut) != null;
    }

    public boolean esActiva() {
        LocalDate hoy = LocalDate.now();
        return (hoy.isEqual(fechaInicio) || hoy.isAfter(fechaInicio)) && (hoy.isEqual(fechaFin) || hoy.isBefore(fechaFin));
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

    public LocalDate getFechaFin() {
        return fechaFin; 
    }
    
    public int getMetaUnidades() {
        return metaUnidades; 
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
        return String.format("[%s] %s (%s) | Periodo: %s a %s | Meta: %d unidades | Donantes: %d | Activa: %s",
                codigo, nombre, lugar, fechaInicio, fechaFin, metaUnidades, donantes.size(), esActiva() ? "SÍ" : "NO");
    }
    
}