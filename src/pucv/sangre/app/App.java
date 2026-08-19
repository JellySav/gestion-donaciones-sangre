package pucv.sangre.app;

// Importar excepciones personalizadas (SIA-12)
import pucv.sangre.excepciones.CampanaNoEncontradaException;
import pucv.sangre.excepciones.DonanteInvalidoException;

// Importar clases del proyecto
import pucv.sangre.gestion.GestionCentroSangre;
import pucv.sangre.gui.VentanaPrincipal;
import pucv.sangre.modulo.Campana;
import pucv.sangre.modulo.Donante;
import pucv.sangre.persistencia.GestorPersistenciaCSV;

// Importar utilidades de Java (JCF) (SIA-4)
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * Clase principal e integradora del sistema (SIA-10) y (SIA-11).
 */
public class App {
    private static final GestionCentroSangre centro = new GestionCentroSangre();
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        // Carga Batch de datos desde CSV al iniciar (SIA-11)
        GestorPersistenciaCSV.cargarDatos(centro);

        // Selección de modo de ejecucion (Consola o GUI) (SIA-10)
        String[] opciones = {"1. Consola", "2. Ventana (GUI)"};
        String seleccion = (String) JOptionPane.showInputDialog(
                null,
                "Seleccione el modo de ejecución del sistema:",
                "Centro de Sangre - PUCV",
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]
        );

        if (seleccion != null && seleccion.startsWith("2")) {
            ejecutarModoVentanas();
        } else {
            ejecutarModoConsola();
            // Guardado automático al salir de la consola (SIA-11)
            GestorPersistenciaCSV.guardarDatos(centro.getCampanas());
        }
    }

    private static void ejecutarModoVentanas() {
        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal ventana = new VentanaPrincipal(centro);
            
            // Guardado automático de persistencia al cerrar la GUI (SIA-11)
            ventana.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    GestorPersistenciaCSV.guardarDatos(centro.getCampanas());
                }
            });
            
            ventana.setVisible(true);
        });
    }

    private static void ejecutarModoConsola() {
        int opc = -1;
        do {
            mostrarMenuConsola();
            opc = leerEntero("Ingrese opción: ");

            try {
                switch (opc) {
                    case 1 -> {
                        System.out.print("Código: "); String c = sc.nextLine();
                        System.out.print("Nombre: "); String n = sc.nextLine();
                        System.out.print("Lugar: "); String l = sc.nextLine();
                        System.out.print("Fecha Inicio (YYYY-MM-DD): "); String fi = sc.nextLine();
                        System.out.print("Fecha Fin (YYYY-MM-DD): "); String ff = sc.nextLine();
                        
                        centro.agregarCampana(new Campana(c, n, l, LocalDate.parse(fi), LocalDate.parse(ff)));
                        System.out.println("[✓] Campaña agregada exitosamente.");
                    }
                    case 2 -> {
                        System.out.println("\n--- CAMPAÑAS REGISTRADAS ---");
                        if (centro.getCampanas().isEmpty()) {
                            System.out.println("No hay campañas registradas.");
                        } else {
                            centro.getCampanas().forEach(System.out::println);
                        }
                    }
                    case 3 -> {
                        System.out.print("Código a editar: "); String c = sc.nextLine();
                        System.out.print("Nuevo Nombre: "); String n = sc.nextLine();
                        System.out.print("Nuevo Lugar: "); String l = sc.nextLine();
                        centro.modificarCampana(c, n, l);
                        System.out.println("[✓] Campaña editada.");
                    }
                    case 4 -> {
                        System.out.print("Código a eliminar: "); String c = sc.nextLine();
                        centro.eliminarCampana(c);
                        System.out.println("[✓] Campaña eliminada.");
                    }
                    case 5 -> {
                        System.out.print("Código a buscar: "); String c = sc.nextLine();
                        System.out.println(centro.buscarCampana(c));
                    }
                    case 6 -> {
                        System.out.print("Código de Campaña: "); String c = sc.nextLine();
                        Campana cmp = centro.buscarCampana(c);
                        
                        System.out.print("RUT Donante: "); String r = sc.nextLine();
                        System.out.print("Nombre Completo: "); String n = sc.nextLine();
                        System.out.print("Grupo Sanguíneo (A/B/AB/O): "); String g = sc.nextLine().toUpperCase();
                        System.out.print("Factor RH (+/-): "); String rh = sc.nextLine();
                        int ed = leerEntero("Edad: ");
                        System.out.print("Teléfono: "); String tel = sc.nextLine();
                        
                        // Corrección: usando el constructor adecuado de Donante
                        cmp.agregarDonante(new Donante(r, n, g, rh, ed, tel));
                        System.out.println("[✓] Donante registrado con éxito.");
                    }
                    case 7 -> {
                        System.out.print("Código de Campaña: "); String c = sc.nextLine();
                        Campana cmp = centro.buscarCampana(c);
                        if (cmp.getDonantes().isEmpty()) {
                            System.out.println("   (Sin donantes asignados)");
                        } else {
                            cmp.getDonantes().forEach(d -> System.out.println("   -> " + d));
                        }
                    }
                    case 8 -> {
                        System.out.print("Código de Campaña: "); String c = sc.nextLine();
                        System.out.print("RUT Donante: "); String r = sc.nextLine();
                        Donante d = centro.buscarDonante(c, r);
                        System.out.print("Nuevo Nombre: "); d.setNombre(sc.nextLine());
                        d.setEdad(leerEntero("Nueva Edad: "));
                        System.out.println("[✓] Donante editado.");
                    }
                    case 9 -> {
                        System.out.print("Código de Campaña: "); String c = sc.nextLine();
                        System.out.print("RUT Donante: "); String r = sc.nextLine();
                        Campana cmp = centro.buscarCampana(c);
                        cmp.eliminarDonante(r);
                        System.out.println("[✓] Donante eliminado.");
                    }
                    case 10 -> {
                        System.out.print("RUT Donante: "); String r = sc.nextLine();
                        Donante d = centro.buscarDonante(r);
                        System.out.println("[✓] Donante encontrado: " + d);
                    }
                    case 11 -> {
                        System.out.print("Grupo Receptor (A/B/AB/O): "); String g = sc.nextLine().toUpperCase();
                        System.out.print("Factor RH (+/-): "); String rh = sc.nextLine();
                        System.out.println("\n--- DONANTES COMPATIBLES (SIA-9) ---");
                        List<Donante> comp = centro.obtenerDonantesCompatibles(g, rh);
                        if (comp.isEmpty()) {
                            System.out.println("No se encontraron donantes compatibles disponibles.");
                        } else {
                            comp.forEach(d -> System.out.println("   [Apto] " + d));
                        }
                    }
                    case 0 -> System.out.println("\nSaliendo y guardando datos del sistema...");
                    default -> System.out.println("[!] Opción no válida. Por favor, elija un número del menú.");
                }
            } catch (CampanaNoEncontradaException | DonanteInvalidoException e) {
                System.err.println("[EXCEPCIÓN CAPTURADA]: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("[ERROR]: " + e.getMessage());
            }
        } while (opc != 0);
    }

    private static void mostrarMenuConsola() {
        System.out.println("\n==========================================");
        System.out.println("        MENÚ SISTEMA CENTRO SANGRE        ");
        System.out.println("==========================================");
        System.out.println("-- CAMPAÑAS --");
        System.out.println("1. Agregar Campaña");
        System.out.println("2. Listar Campañas");
        System.out.println("3. Editar Campaña");
        System.out.println("4. Eliminar Campaña");
        System.out.println("5. Buscar Campaña por Código");
        System.out.println("------------------------------------------");
        System.out.println("-- DONANTES --");
        System.out.println("6. Agregar Donante a Campaña");
        System.out.println("7. Listar Donantes de una Campaña");
        System.out.println("8. Editar Donante");
        System.out.println("9. Eliminar Donante");
        System.out.println("10. Buscar Donante por RUT (Global)");
        System.out.println("------------------------------------------");
        System.out.println("-- OPCIONES COMPLEMENTARIAS --");
        System.out.println("11. Consultar Compatibilidad Transfusional");
        System.out.println("0. Salir y Guardar");
        System.out.println("==========================================");
    }

    private static int leerEntero(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                return Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("[!] Entrada inválida. Ingrese un número entero.");
            }
        }
    }
}