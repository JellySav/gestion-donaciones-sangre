package pucv.sangre.excepciones;

// Hereda de la clase Exception nativa de Java (SIA-12)
public class CampanaNoEncontradaException extends Exception {
    public CampanaNoEncontradaException(String mensaje) {
        super(mensaje);
    }
}