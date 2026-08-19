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
    // Colecciones de datos principales (SIA-4)
    private Map<String, Campana> campanas; // Clave: código de la campaña
    private final Map<String, List<Campana>> campanasPorFecha; // Clave: Fecha (String "YYYY-MM-DD")
    private final Map<String, ReservaSanguinea> reservas; // Clave: Tipo Sanguíneo ("A+", "O-", etc.)

    public GestionCentroSangre() {
        this.campanas = new HashMap<>();
        this.campanasPorFecha = new HashMap<>();
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

        try {
            agregarCampana(c1);
            agregarCampana(c2);
            agregarCampana(c3);
            agregarCampana(c4);
        } catch (DonanteInvalidoException e) {
            System.err.println("Error al cargar datos iniciales: " + e.getMessage());
        }

        // Stock inicial
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
        // Registro en el mapa principal
        campanas.put(campana.getCodigo(), campana);

        // Registro en la estructura por fecha
        String fechaClave = campana.getFechaInicio() != null ? campana.getFechaInicio().toString() : campana.getFecha();
        if (fechaClave != null && !fechaClave.isBlank()) {
            campanasPorFecha
                    .computeIfAbsent(fechaClave, k -> new ArrayList<>())
                    .add(campana);
        }
        return true;
    }


    public Campana buscarCampana(String codigo) throws CampanaNoEncontradaException {
        Campana c = campanas.get(codigo);
        if (c == null) {
            throw new CampanaNoEncontradaException("No se encontró la campaña con el código: " + codigo);
        }
        return c;
    }
    
    public List<Campana> buscarCampanasPorFecha(String fecha) throws CampanaNoEncontradaException {
        List<Campana> lista = campanasPorFecha.get(fecha);
        if (lista == null || lista.isEmpty()) {
            throw new CampanaNoEncontradaException("No hay campañas registradas para la fecha: " + fecha);
        }
        return lista;
    }

    public Campana buscarCampana(String fecha, String codigo) throws CampanaNoEncontradaException {
        List<Campana> lista = buscarCampanasPorFecha(fecha);
        for (Campana c : lista) {
            if (c.getCodigo().equalsIgnoreCase(codigo)) {
                return c;
            }
        }
        throw new CampanaNoEncontradaException("No se encontró la campaña con código '" + codigo + "' en la fecha " + fecha);
    }

    public boolean eliminarCampana(String codigo) throws CampanaNoEncontradaException {
        Campana c = buscarCampana(codigo);
        campanas.remove(codigo);

        String fechaClave = c.getFechaInicio() != null ? c.getFechaInicio().toString() : c.getFecha();
        if (fechaClave != null && campanasPorFecha.containsKey(fechaClave)) {
            List<Campana> lista = campanasPorFecha.get(fechaClave);
            lista.removeIf(item -> item.getCodigo().equalsIgnoreCase(codigo));
            if (lista.isEmpty()) {
                campanasPorFecha.remove(fechaClave);
            }
        }
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

    public Map<String, List<Campana>> getCampanasPorFecha() {
        return campanasPorFecha;
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
                if (esCompatible(d.getGrupoSanguineo(), d.getFactorRh(), grupoReceptor, rhReceptor)) {
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
                if (esCompatible(d.getGrupoSanguineo(), d.getFactorRh(), grupoReceptor, rhReceptor) && esAptoParaDonar(d)) {
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

    public boolean esCompatible(String gDonante, String rhDonante, String gReceptor, String rhReceptor) {
        // Validación del factor Rh: Un donante Rh(-) sirve a Rh(+) o Rh(-); Rh(+) solo a Rh(+)
        boolean rhCompatible = rhDonante.equals("-") || rhReceptor.equals("+");
        if (!rhCompatible) return false;

        // Reglas ABO -> Donante Universal (O-) y Receptor Universal (AB+)
        return switch (gReceptor.toUpperCase()) {
            case "O"  -> gDonante.equalsIgnoreCase("O");
            case "A"  -> gDonante.equalsIgnoreCase("O") || gDonante.equalsIgnoreCase("A");
            case "B"  -> gDonante.equalsIgnoreCase("O") || gDonante.equalsIgnoreCase("B");
            case "AB" -> true;
            default   -> false;
        };
    }

}