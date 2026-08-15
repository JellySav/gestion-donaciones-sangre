package pucv.sangre.persistencia;

// Importar clases del proyecto
import pucv.sangre.excepciones.CampanaNoEncontradaException;
import pucv.sangre.excepciones.DonanteInvalidoException;
import pucv.sangre.gestion.GestionCentroSangre;
import pucv.sangre.modulo.Campana;
import pucv.sangre.modulo.Donante;

// Importar utilidades de Java Collections Framework (JCF) (SIA-4) 
import java.io.*;   // Importar todo de la coleccion io 
import java.util.Map;  // Importat de la coleccion util el mapa 

/**
 * Gestor de persistencia en archivos CSV (SIA-11)
 * Separa la información en dos archivos para mantener un orden -> Crea dos archivos potenciales
 */
public class GestorPersistenciaCSV {

    private static final String RUTA_CAMPANAS = "campanas.csv";
    private static final String RUTA_DONANTES = "donantes.csv";
    private static final String SEPARADOR = ";";

    /**
     * Guarda campañas y donantes en archivos CSV independientes (SIA-11)
     */
    
    public static void guardarDatos(Map<String, Campana> campanas) {
        try (BufferedWriter bwCampanas = new BufferedWriter(new FileWriter(RUTA_CAMPANAS));
             BufferedWriter bwDonantes = new BufferedWriter(new FileWriter(RUTA_DONANTES))) {

            for (Campana c : campanas.values()) {
                // Formato CSV: codigo;nombre;lugar
                bwCampanas.write(c.getCodigo() + SEPARADOR + c.getNombre() + SEPARADOR + c.getLugar());
                bwCampanas.newLine();

                // Formato CSV: codCampana;rut;nombre;grupo;rh;edad
                for (Donante d : c.getDonantes().values()) {
                    bwDonantes.write(c.getCodigo() + SEPARADOR +
                            d.getRut() + SEPARADOR +
                            d.getNombre() + SEPARADOR +
                            d.getGrupoSanguineo() + SEPARADOR +
                            d.getFactorRh() + SEPARADOR +
                            d.getEdad());
                    bwDonantes.newLine();
                }
            }
            System.out.println("[✓] Persistencia completada correctamente en CSV.");
        } catch (IOException e) {
            System.err.println("[!] Error al guardar los datos en CSV: " + e.getMessage());
        }
    }

    /**
     * Carga de datos batch al iniciar la aplicación (SIA-11)
     */

    public static void cargarDatos(GestionCentroSangre centro) {
        File fCampanas = new File(RUTA_CAMPANAS);
        File fDonantes = new File(RUTA_DONANTES);

        if (!fCampanas.exists()) {
            System.out.println("[i] No se encontró el archivo " + RUTA_CAMPANAS + ". Se iniciará con datos base.");
            return;
        }

        // 1. Cargar Campañas
        try (BufferedReader br = new BufferedReader(new FileReader(fCampanas))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(SEPARADOR);
                if (datos.length >= 3) {
                    try {
                        centro.agregarCampana(new Campana(datos[0].trim(), datos[1].trim(), datos[2].trim()));
                    } catch (DonanteInvalidoException ignored) {
                        // Ignora duplicados si ya existen
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("[!] Error al leer " + RUTA_CAMPANAS + ": " + e.getMessage());
        }

        if (!fDonantes.exists()) return;

        // 2. Cargar Donantes anidados a sus campañas correspondientes
        try (BufferedReader br = new BufferedReader(new FileReader(fDonantes))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(SEPARADOR);
                if (datos.length >= 6) {
                    try {
                        String codCampana = datos[0].trim();
                        Donante donante = new Donante(
                                datos[1].trim(),
                                datos[2].trim(),
                                datos[3].trim(),
                                datos[4].trim(),
                                Integer.parseInt(datos[5].trim())
                        );
                        Campana c = centro.buscarCampana(codCampana);
                        c.agregarDonante(donante);
                    } catch (CampanaNoEncontradaException | NumberFormatException ignored) {
                        // Salta registros incompletos o asociados a campañas inexistentes
                    }
                }
            }
            System.out.println("[✓] Carga batch inicial desde CSV ejecutada con éxito.");
        } catch (IOException e) {
            System.err.println("[!] Error al leer " + RUTA_DONANTES + ": " + e.getMessage());
        }
    }
}