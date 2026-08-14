package pucv.sangre.gestion;

// Importar los modelos que están en la carpeta "modulo"
import pucv.sangre.modulo.Campana;
import pucv.sangre.modulo.Donante;

// Importar utilidades de Java Collections Framework (JCF) (SIA-4) 
import java.util.HashMap;
import java.util.Map;

public class GestionCentroSangre {
    private Map<String, Campana> campanas;  // Primera colección -> Mapa principal de Campañas (SIA-4)

    public GestionCentroSangre() {
        this.campanas = new HashMap<>();
        cargarDatosIniciales(); // Carga de datos iniciales requerida (SIA-3)
    }

    /**
     * Carga datos iniciales para permitir pruebas directas al ejecutar la app. (SIA-3)
     */
    private void cargarDatosIniciales() {
        Campana c1 = new Campana("C01", "Campaña Universitaria Valparaíso", "Plaza Bisquertt");
        Campana c2 = new Campana("C02", "Salva Vidas Verano", "Hospital Fricke");
        Campana c3 = new Campana("C03", "Campaña Nueva Vida", "Hospital Soto Mayor");
        Campana c4 = new Campana("C04", "Festival Una Vida", "Hospital Mayor");

        Donante d1 = new Donante("19283746-5", "Ana Torres", "O", "+", 24);
        Donante d2 = new Donante("20111222-3", "Carlos Gómez", "A", "-", 30);
        Donante d3 = new Donante("18444555-K", "Beatriz Soto", "O", "-", 28);
        Donante d4 = new Donante("21567253-8", "Ricardo Beltran", "B", "-", 21);
        Donante d5 = new Donante("18264732-5", "Alejandra Lopez", "A", "+", 25);
        Donante d6 = new Donante("21836247-4", "Veronica Huerta", "O", "-", 24);
        Donante d7 = new Donante("18444555-K", "Daniel Serrano", "B", "-", 32);


        c1.agregarDonante(d1);
        c1.agregarDonante(d2);
        c2.agregarDonante(d3);
        c2.agregarDonante(d4);
        c3.agregarDonante(d5);
        c3.agregarDonante(d6);
        c4.agregarDonante(d7);

        campanas.put(c1.getCodigo(), c1);
        campanas.put(c2.getCodigo(), c2);
        campanas.put(c3.getCodigo(), c3);
        campanas.put(c4.getCodigo(), c4);
    }

    public Map<String, Campana> getCampanas() {
        return campanas;
    }
}
