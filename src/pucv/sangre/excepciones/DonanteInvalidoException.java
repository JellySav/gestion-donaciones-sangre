package pucv.sangre.excepciones;

// Hereda de la clase Exception nativa de Java (SIA-12)
public class DonanteInvalidoException extends Exception {
    public DonanteInvalidoException(String mensaje) {
        super(mensaje);
    }
}