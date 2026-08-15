package pucv.sangre.gestion;

// Importar clases de "modulo"
import pucv.sangre.modulo.Campana;
import pucv.sangre.modulo.Donante;

// Importar excepciones personalizadas (SIA-12)
import pucv.sangre.excepciones.CampanaNoEncontradaException;
import pucv.sangre.excepciones.DonanteInvalidoException;

// Importar utilidades de Java Collections Framework (JCF) (SIA-4) 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase principal de gestión del sistema (Servicio).
 * Contiene la primera colección del JCF (Map de Campañas).
 */

public class GestionCentroSangre {
    
    // Primera colección (Mapa principal de Campañas) (SIA-4)
    private Map<String, Campana> campanas;

    public GestionCentroSangre() {
        this.campanas = new HashMap<>();
        cargarDatosIniciales(); // Carga automática de datos para pruebas (SIA-3)
    }

    /**
     * Permite inicializar el sistema con información ficticia
     * para probar los menús inmediatamente al ejecutar. (SIA-3)
     */
    private void cargarDatosIniciales() {
        Campana c1 = new Campana("C01", "Campaña Universitaria Valparaíso", "Plaza Bisquertt");
        Campana c2 = new Campana("C02", "Salva Vidas Verano", "Hospital Fricke");
        Campana c3 = new Campana("C03", "Campaña Nueva Vida", "Hospital Soto Mayor");
        Campana c4 = new Campana("C04", "Festival Una Vida", "Hospital Mayor");

        // Donantes iniciales a las campañas (Colección Anidada)
        c1.agregarDonante(new Donante("19283746-5", "Ana Torres", "O", "+", 24));
        c1.agregarDonante(new Donante("20111222-3", "Carlos Gómez", "A", "-", 30));
        c2.agregarDonante(new Donante("18444555-K", "Beatriz Soto", "O", "-", 28));
        c3.agregarDonante(new Donante("21567253-8", "Ricardo Beltran", "B", "-", 21));
        c4.agregarDonante(new Donante("18264732-5", "Alejandra Lopez", "A", "+", 25));
        c4.agregarDonante(new Donante("21836247-4", "Veronica Huerta", "O", "-", 24));
        c4.agregarDonante(new Donante("18444555-K", "Daniel Serrano", "B", "-", 32));

        this.campanas.put(c1.getCodigo(), c1);
        this.campanas.put(c2.getCodigo(), c2);
        this.campanas.put(c3.getCodigo(), c3);
        this.campanas.put(c4.getCodigo(), c4);
    }

    /**
     * Métodos CRUD para campañas - Colección 1 con Excepciones (SIA-7), (SIA-8) y (SIA-12)
     */
    public boolean agregarCampana(Campana campana) throws DonanteInvalidoException {
        if (campana == null || campana.getCodigo() == null || campana.getCodigo().trim().isEmpty()) {
            throw new DonanteInvalidoException("La campaña ingresada no tiene un código válido.");
        }
        if (campanas.containsKey(campana.getCodigo())) {
            throw new DonanteInvalidoException("Ya existe una campaña registrada con el código: " + campana.getCodigo());
        }
        campanas.put(campana.getCodigo(), campana);
        return true;
    }

    public Campana buscarCampana(String codigo) throws CampanaNoEncontradaException {
        Campana c = campanas.get(codigo);
        if (c == null) {
            throw new CampanaNoEncontradaException("No se encontró la campaña con el código: " + codigo);
        }
        return c;
    }

    public boolean eliminarCampana(String codigo) throws CampanaNoEncontradaException {
        buscarCampana(codigo); // Valida existencia
        campanas.remove(codigo);
        return true;
    }

    public boolean modificarCampana(String codigo, String nuevoNombre, String nuevoLugar) throws CampanaNoEncontradaException {
        Campana c = buscarCampana(codigo); // Valida existencia
        c.setNombre(nuevoNombre);
        c.setLugar(nuevoLugar);
        return true;
    }

    /**
     * Métodos Sobrecargados con Excepciones (SIA-5) y (SIA-12)
     */

    // Versión 1 : Búsqueda global de un donante por RUT (1 parámetro)
    public Donante buscarDonante(String rut) throws DonanteInvalidoException {
        for (Campana c : campanas.values()) {
            Donante d = c.buscarDonante(rut);
            if (d != null) {
                return d;
            }
        }
        throw new DonanteInvalidoException("No se encontro ningún donante registrado con el RUT: " + rut);
    }

    // Versión 2 : Búsqueda de un donante en una campaña específica (2 parámetros)
    public Donante buscarDonante(String codCampana, String rut) throws CampanaNoEncontradaException, DonanteInvalidoException {
        Campana campana = buscarCampana(codCampana);
        Donante d = campana.buscarDonante(rut);
        if (d == null) {
            throw new DonanteInvalidoException("El donante con RUT " + rut + " no existe en la campaña " + codCampana);
        }
        return d;
    }

    // Versión 3 : Búsqueda de donantes por grupo, RH y edad mínima (3 parámetros)
    public List<Donante> buscarDonante(String grupoSanguineo, String factorRh, int edadMinima) {
        List<Donante> resultado = new ArrayList<>();
        for (Campana c : campanas.values()) {
            for (Donante d : c.getDonantes().values()) {
                if (d.getGrupoSanguineo().equalsIgnoreCase(grupoSanguineo) && 
                    d.getFactorRh().equalsIgnoreCase(factorRh) &&
                    d.getEdad() >= edadMinima) {
                    resultado.add(d);
                }
            }
        }
        return resultado;
    }

    /**
     * Funcionalidad Propia: Filtra y retorna los donantes compatibles según el tipo de un paciente (SIA-9)
     */
    public List<Donante> obtenerDonantesCompatibles(String grupoReceptor, String rhReceptor) {
        List<Donante> compatibles = new ArrayList<>();
        for (Campana c : campanas.values()) {
            for (Donante d : c.getDonantes().values()) {
                if (esCompatible(d.getGrupoSanguineo(), d.getFactorRh(), grupoReceptor, rhReceptor)) {
                    compatibles.add(d);
                }
            }
        }
        return compatibles;
    }

    private boolean esCompatible(String gDonante, String rhDonante, String gReceptor, String rhReceptor) {
        // O- es donante universal 
        if (gDonante.equalsIgnoreCase("O") && rhDonante.equals("-")) return true;
        // Coincidencia exacta
        if (gDonante.equalsIgnoreCase(gReceptor) && rhDonante.equals(rhReceptor)) return true;
        // Receptor AB+ recibe de cualquiera 
        if (gReceptor.equalsIgnoreCase("AB") && rhReceptor.equals("+")) return true;

        return false;
    }

    // Getters y Setters (SIA-3)
    public Map<String, Campana> getCampanas() {
        return campanas;
    }

    public void setCampanas(Map<String, Campana> campanas) {
        this.campanas = campanas;
    }
}