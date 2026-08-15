package pucv.sangre.app;

// Importar clases del paquete "modulo"
import pucv.sangre.gestion.GestionCentroSangre;
import pucv.sangre.modulo.Campana;
import pucv.sangre.modulo.Donante;

// Importar utilidades de Java Collections Framework (JCF) (SIA-4) 
import java.util.List;
import java.util.Scanner;

/**
 * 
 */

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final GestionCentroSangre centro = new GestionCentroSangre();

    public static void main(String[] args) {
        int opcion;
        do {
            mostrarMenu();
            opcion = leerEntero("Seleccione una opción: ");
            switch (opcion) {
                case 1 -> listarCampanas();
                case 2 -> agregarCampana();
                case 3 -> agregarDonanteACampana();
                case 4 -> buscarDonantePorRut();
                case 5 -> buscarDonantesCompatibles();
                case 6 -> eliminarCampana();
                case 0 -> System.out.println("\nSaliendo del sistema de gestión de sangre");
                default -> System.out.println("\n[!] Opción no válida. Por favor, elija alguna de las opciones.");
            }
        } while (opcion != 0);
    }

    private static void mostrarMenu() {
        System.out.println("\n==========================================");
        System.out.println("   SISTEMA DE GESTIÓN DE DONACIONES SANGRE ");
        System.out.println("==========================================");
        System.out.println("1. Listar campañas y sus donantes");
        System.out.println("2. Registrar nueva campaña");
        System.out.println("3. Registrar nuevo donante en una campaña");
        System.out.println("4. Buscar donante por RUT");   // Sobrecarga (SIA-5)
        System.out.println("5. Buscar donantes compatibles");  // Funcion Propia (SIA-9)
        System.out.println("6. Eliminar una campaña");
        System.out.println("0. Salir");
        System.out.println("------------------------------------------");
    }

    private static void listarCampanas() {
        System.out.println("\n--- CAMPAÑAS REGISTRADAS ---");
        if (centro.getCampanas().isEmpty()) {
            System.out.println("No hay campañas registradas.");
            return;
        }
        for (Campana c : centro.getCampanas().values()) {
            System.out.println(c);
            if (!c.getDonantes().isEmpty()) {
                c.getDonantes().values().forEach(d -> System.out.println("   -> " + d));
            } else {
                System.out.println("   (Sin donantes asignados)");
            }
        }
    }

    private static void agregarCampana() {
        System.out.println("\n--- REGISTRAR NUEVA CAMPAÑA ---");
        System.out.print("Código: ");
        String cod = scanner.nextLine();
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();
        System.out.print("Lugar: ");
        String lugar = scanner.nextLine();

        if (centro.agregarCampana(new Campana(cod, nombre, lugar))) {
            System.out.println("[✓] Campaña agregada exitosamente.");
        } else {
            System.out.println("[!] Error: Ya existe una campaña con ese código.");
        }
    }

    private static void agregarDonanteACampana() {
        System.out.println("\n--- AGREGAR DONANTE A CAMPAÑA ---");
        System.out.print("Código de la campaña: ");
        String cod = scanner.nextLine();
        Campana c = centro.buscarCampana(cod);

        if (c == null) {
            System.out.println("[!] Error: La campaña no existe.");
            return;
        }

        System.out.print("RUT Donante: ");
        String rut = scanner.nextLine();
        System.out.print("Nombre Completo: ");
        String nombre = scanner.nextLine();
        System.out.print("Grupo Sanguíneo (A, B, AB, O): ");
        String grupo = scanner.nextLine().toUpperCase();
        System.out.print("Factor RH (+ / -): ");
        String rh = scanner.nextLine();
        int edad = leerEntero("Edad: ");

        c.agregarDonante(new Donante(rut, nombre, grupo, rh, edad));
        System.out.println("[✓] Donante registrado con éxito en la campaña " + cod);
    }

    private static void buscarDonantePorRut() {
        System.out.println("\n--- BÚSQUEDA DE DONANTE ---");
        System.out.print("Ingrese RUT del donante: ");
        String rut = scanner.nextLine();
        
        Donante d = centro.buscarDonante(rut);
        if (d != null) {
            System.out.println("[✓] Donante Encontrado: " + d);
        } else {
            System.out.println("[!] No se encontró ningún donante registrado con ese RUT.");
        }
    }

    private static void buscarDonantesCompatibles() {
        System.out.println("\n--- BÚSQUEDA DE DONANTES COMPATIBLES (EMERGENCIA) ---");
        System.out.print("Grupo del receptor (A, B, AB, O): ");
        String grupo = scanner.nextLine().toUpperCase();
        System.out.print("Factor RH del receptor (+ / -): ");
        String rh = scanner.nextLine();

        List<Donante> compatibles = centro.obtenerDonantesCompatibles(grupo, rh);
        System.out.println("\nDonantes aptos para receptor " + grupo + rh + ":");
        if (compatibles.isEmpty()) {
            System.out.println("No se encontraron donantes compatibles disponibles.");
        } else {
            compatibles.forEach(d -> System.out.println("   [Apto] " + d));
        }
    }

    private static void eliminarCampana() {
        System.out.print("\nIngrese código de la campaña a eliminar: ");
        String cod = scanner.nextLine();
        if (centro.eliminarCampana(cod)) {
            System.out.println("[✓] Campaña eliminada con éxito.");
        } else {
            System.out.println("[!] Error: No se encontró la campaña especificada.");
        }
    }

    private static int leerEntero(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("[!] Entrada inválida. Ingrese un número entero.");
            }
        }
    }
}