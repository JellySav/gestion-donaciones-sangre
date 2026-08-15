package pucv.sangre.gestion;

// Importar excepciones personalizadas (SIA-12)
import pucv.sangre.excepciones.CampanaNoEncontradaException;
import pucv.sangre.excepciones.DonanteInvalidoException;

// Importar clases de "modulo"
import pucv.sangre.modulo.Campana;
import pucv.sangre.modulo.Donante;
import pucv.sangre.modulo.ReservaSanguinea;

// Importar utilidades de Java Collections Framework (JCF) (SIA-4) 
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase principal de gestión del sistema (Servicio).
 * Administra campañas, reservas de sangre y filtrados de elegibilidad.
 */
public class GestionCentroSangre {
    
    //Colecciones de datos principales (SIA-4)
    private Map<String, Campana> campanas;
    private final Map<String, ReservaSanguinea> reservas;

    public GestionCentroSangre() {
        this.campanas = new HashMap<>();
        this.reservas = new HashMap<>();
        inicializarReservas();
        cargarDatosIniciales(); 
    }

    //Carga inicial y configuración del entorno de pruebas (SIA-3)    
    private void inicializarReservas() {
        String[] tipos = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        for (String t : tipos) {
            reservas.put(t, new ReservaSanguinea(t, 0, 10)); // Nivel crítico por defecto 10 unidades
        }
    }

    private void cargarDatosIniciales() {
        LocalDate hoy = LocalDate.now();

        Campana c1 = new Campana("C01", "Campaña Universitaria Valparaíso", "Plaza Bisquertt", hoy.minusDays(5), hoy.plusDays(10), 50);
        Campana c2 = new Campana("C02", "Salva Vidas Verano", "Hospital Fricke", hoy.minusDays(20), hoy.minusDays(2), 30);
        Campana c3 = new Campana("C03", "Campaña Nueva Vida", "Hospital Soto Mayor", hoy, hoy.plusDays(15), 40);
        Campana c4 = new Campana("C04", "Festival Una Vida", "Hospital Mayor", hoy.minusDays(1), hoy.plusDays(5), 60);

        // Donantes iniciales con fechas de última donación (SIA-1)
        c1.agregarDonante(new Donante("19283746-5", "Ana Torres", "O", "+", 24, "+56911112222", hoy.minusMonths(4)));
        c1.agregarDonante(new Donante("20111222-3", "Carlos Gómez", "A", "-", 30, "+56933334444", hoy.minusMonths(1)));
        c2.agregarDonante(new Donante("18444555-K", "Beatriz Soto", "O", "-", 28, "+56955556666", null));
        c3.agregarDonante(new Donante("21567253-8", "Ricardo Beltran", "B", "-", 21, "+56977778888", hoy.minusMonths(5)));
        c4.agregarDonante(new Donante("18264732-5", "Alejandra Lopez", "A", "+", 25, "+56999990000", hoy.minusMonths(6)));
        c4.agregarDonante(new Donante("21836247-4", "Veronica Huerta", "O", "-", 24, "+56912345678", null));

        campanas.put(c1.getCodigo(), c1);
        campanas.put(c2.getCodigo(), c2);
        campanas.put(c3.getCodigo(), c3);
        campanas.put(c4.getCodigo(), c4);

        // Stock inicial de prueba
        registrarIngresoSangre("O-", 15);
        registrarIngresoSangre("A+", 5);
    }

     
    //Métodos CRUD para campañas - Colección 1 con Excepciones (SIA-7), (SIA-8) y (SIA-12)
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
        buscarCampana(codigo);
        campanas.remove(codigo);
        return true;
    }

    public boolean modificarCampana(String codigo, String nuevoNombre, String nuevoLugar) throws CampanaNoEncontradaException {
        Campana c = buscarCampana(codigo);
        c.setNombre(nuevoNombre);
        c.setLugar(nuevoLugar);
        return true;
    }

    public Map<String, Campana> getCampanas() {
        return campanas;
    }

    public void setCampanas(Map<String, Campana> campanas) {
        this.campanas = campanas;
    }

    /**
     * Métodos Sobrecargados con Excepciones (SIA-5) y (SIA-12)
     */

    // Versión 1: Busqueda global de un donante por RUT (1 parámetro) 
    public Donante buscarDonante(String rut) throws DonanteInvalidoException {
        for (Campana c : campanas.values()) {
            if (c.getDonantes().containsKey(rut)) {
                return c.getDonantes().get(rut);
            }
        }
        throw new DonanteInvalidoException("No se encontró ningún donante registrado con el RUT: " + rut);
    }

    // Versión 2: Busqueda de un donante en una campaña específica (2 parámetros) 
    public Donante buscarDonante(String codCampana, String rut) throws CampanaNoEncontradaException, DonanteInvalidoException {
        Campana campana = buscarCampana(codCampana);
        Donante d = campana.getDonantes().get(rut);
        if (d == null) {
            throw new DonanteInvalidoException("El donante con RUT " + rut + " no existe en la campaña " + codCampana);
        }
        return d;
    }

    // Versión 3: Busqueda de donantes por grupo, RH y edad mínima (3 parámetros) 
    public List<Donante> buscarDonante(String grupoSanguineo, String factorRh, int edadMinima) {
        List<Donante> resultado = new ArrayList<>();
        for (Campana c : campanas.values()) {
            for (Donante d : c.getDonantes().values()) {
                if (d.getGrupo().equalsIgnoreCase(grupoSanguineo) &&
                    d.getRh().equalsIgnoreCase(factorRh) &&
                    d.getEdad() >= edadMinima) {
                    resultado.add(d);
                }
            }
        }
        return resultado;
    }


    //Control de stock de reserva sanguínea y niveles críticos
    public Map<String, ReservaSanguinea> getReservas() {
        return reservas;
    }

    public void registrarIngresoSangre(String tipo, int cantidad) {
        if (reservas.containsKey(tipo)) {
            reservas.get(tipo).agregarStock(cantidad);
        }
    }

    public List<ReservaSanguinea> obtenerReservasCriticas() {
        List<ReservaSanguinea> criticas = new ArrayList<>();
        for (ReservaSanguinea r : reservas.values()) {
            if (r.esNivelCritico()) {
                criticas.add(r);
            }
        }
        return criticas;
    }

    /**
     * Reglas de negocio - Compatibilidad sanguínea y elegibilidad en emergencias (SIA-1, SIA-9)
     */
    
    // Obtener Donantes Compatibles 
    public List<Donante> obtenerDonantesCompatibles(String grupoReceptor, String rhReceptor) {
        List<Donante> compatibles = new ArrayList<>();
        for (Campana c : campanas.values()) {
            for (Donante d : c.getDonantes().values()) {
                if (esCompatible(d.getGrupo(), d.getRh(), grupoReceptor, rhReceptor)) {
                    compatibles.add(d);
                }
            }
        }
        return compatibles;
    }

    // Filtrado de Elegibilidad Para Emergencias -  Compatible + Campaña Activa + Apto según fecha (SIA-1)
    public List<Donante> obtenerDonantesElegiblesParaEmergencia(String grupoReceptor, String rhReceptor) {
        List<Donante> elegibles = new ArrayList<>();

        for (Campana c : campanas.values()) {
            if (!c.esActiva()) continue;

            for (Donante d : c.getDonantes().values()) {
                if (esCompatible(d.getGrupo(), d.getRh(), grupoReceptor, rhReceptor) && esAptoParaDonar(d)) {
                    elegibles.add(d);
                }
            }
        }
        return elegibles;
    }

    // Verifica la ventana de 90 días (3 meses) requerida desde la última donación
    public boolean esAptoParaDonar(Donante d) {
        if (d.getUltimaFechaDonacion() == null) return true;
        LocalDate fechaDisponible = d.getUltimaFechaDonacion().plusMonths(3);
        return !LocalDate.now().isBefore(fechaDisponible);
    }

    // Lógica universal de compatibilidad sanguínea
    public boolean esCompatible(String gDonante, String rhDonante, String gReceptor, String rhReceptor) {
        // Donante Universal (O-)
        if (gDonante.equalsIgnoreCase("O") && rhDonante.equals("-")) return true;
        // Receptor Universal (AB+)
        if (gReceptor.equalsIgnoreCase("AB") && rhReceptor.equals("+")) return true;
        // Coincidencia Exacta
        return gDonante.equalsIgnoreCase(gReceptor) && rhDonante.equals(rhReceptor);
    }
}