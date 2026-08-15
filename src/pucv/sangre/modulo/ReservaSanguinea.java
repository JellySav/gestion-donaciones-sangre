package pucv.sangre.modulo;

public class ReservaSanguinea {
    private String tipoSanguineo; // Aqui no hay dos variables de instancia para tener el tipo de sangre -> "O+", "A-"
    private int stockActual;      // Unidades o mililitros acumulados
    private int nivelCriticoMinimo;

    public ReservaSanguinea(String tipoSanguineo, int stockActual, int nivelCriticoMinimo) {
        this.tipoSanguineo = tipoSanguineo;
        this.stockActual = stockActual;
        this.nivelCriticoMinimo = nivelCriticoMinimo;
    }

    // Getters y Setters (SIA-3) 
    public String getTipoSanguineo() {
        return tipoSanguineo; 
    }
    
    public int getStockActual() {
        return stockActual; 
    }
    
    public void setStockActual(int stockActual) {
        this.stockActual = stockActual; 
    }
    public int getNivelCriticoMinimo() {
        return nivelCriticoMinimo; 
    }

    // Métodos de reserva 
    public void agregarStock(int cantidad) {
        this.stockActual += cantidad;
    }

    public boolean descontarStock(int cantidad) {
        if (this.stockActual >= cantidad) {
            this.stockActual -= cantidad;
            return true;
        }
        return false;
    }

    public boolean esNivelCritico() {
        return stockActual <= nivelCriticoMinimo;
    }

    @Override
    public String toString() {
        return String.format("Tipo: %-3s | Stock: %d U | Mínimo Crítico: %d U | Estado: %s",
                tipoSanguineo, stockActual, nivelCriticoMinimo, esNivelCritico() ? "[ALERTA: STOCK CRÍTICO]" : "[OK]");
    }
}