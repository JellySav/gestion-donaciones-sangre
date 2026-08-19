package pucv.sangre.persistencia;

// Importar clases del proyecto
import pucv.sangre.excepciones.CampanaNoEncontradaException;
import pucv.sangre.excepciones.DonanteInvalidoException;
import pucv.sangre.gestion.GestionCentroSangre;
import pucv.sangre.modulo.Campana;
import pucv.sangre.modulo.Donante;

// Importar utilidades de Java Collections Framework (JCF) (SIA-4) 
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;  

/**
 * Gestor de persistencia en archivos CSV (SIA-11)
 * Separa la información en dos archivos para mantener un orden -> Crea dos archivos potenciales
 */
public class GestorPersistenciaCSV {
    private static final String ARCHIVO_CAMPANAS = "campanas.csv";
    private static final String ARCHIVO_DONANTES = "donantes.csv";
    private static final String SEPARADOR = ";";

    /**
     * Guarda campañas y donantes en archivos CSV independientes (SIA-11)
     */
    
    public static void guardarDatos(Map<String, List<Campana>> campanasPorFecha) {
        try (PrintWriter writerCampanas = new PrintWriter(new FileWriter(ARCHIVO_CAMPANAS));
             PrintWriter writerDonantes = new PrintWriter(new FileWriter(ARCHIVO_DONANTES))) {

            for (List<Campana> lista : campanasPorFecha.values()) {
                for (Campana c : lista) {
                    // Formato Campaña: Codigo;Nombre;Lugar;Fecha
                    writerCampanas.println(c.getCodigo() + SEPARADOR +
                                           c.getNombre() + SEPARADOR +
                                           c.getLugar() + SEPARADOR +
                                           c.getFecha());

                    // Formato Donante: RUT;Nombre;Grupo;RH;Edad;CodigoCampana;FechaCampana
                    for (Donante d : c.getDonantes()) {
                        writerDonantes.println(d.getRut() + SEPARADOR +
                                               d.getNombre() + SEPARADOR +
                                               d.getGrupoSanguineo() + SEPARADOR +
                                               d.getFactorRh() + SEPARADOR +
                                               d.getEdad() + SEPARADOR +
                                               c.getCodigo() + SEPARADOR +
                                               c.getFecha());
                    }
                }
            }
            System.out.println("[Persistencia] Datos guardados exitosamente en CSV.");

        } catch (IOException e) {
            System.err.println("[Error Persistencia] No se pudieron guardar los datos: " + e.getMessage());
        }
    }
    
    

    /**
     * Carga de datos batch al iniciar la aplicación (SIA-11)
     */

    public static void cargarDatos(GestionCentroSangre centro) {
        if (!Files.exists(Paths.get(ARCHIVO_CAMPANAS))) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO_CAMPANAS))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split(SEPARADOR);
                if (datos.length == 4) {
                    // Codigo, Nombre, Lugar, Fecha
                    centro.agregarCampana(new Campana(datos[0], datos[1], datos[2], datos[3]));
                }
            }
        } catch (Exception e) {
            System.err.println("[Error Persistencia] Error al cargar campañas: " + e.getMessage());
        }

        if (!Files.exists(Paths.get(ARCHIVO_DONANTES))) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO_DONANTES))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split(SEPARADOR);
                if (datos.length == 7) {
                    String rut = datos[0];
                    String nombre = datos[1];
                    String grupo = datos[2];
                    String rh = datos[3];
                    int edad = Integer.parseInt(datos[4]);
                    String codCampana = datos[5];
                    String fechaCampana = datos[6];

                    try {
                        Campana cmp = centro.buscarCampana(fechaCampana, codCampana);
                        cmp.agregarDonante(new Donante(rut, nombre, grupo, rh, edad));
                    } catch (Exception e) {
                        System.err.println("[Error Carga Donante] No se halló campaña " + codCampana + " en fecha " + fechaCampana);
                    }
                }
            }
            System.out.println("[Persistencia] Carga batch completada.");
        } catch (Exception e) {
            System.err.println("[Error Persistencia] Error al cargar donantes: " + e.getMessage());
        }
    }
}