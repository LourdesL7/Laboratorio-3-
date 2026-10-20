import java.math.BigDecimal;
import java.math.RoundingMode;

public class Instalacion extends OrdenServicio {
    private final int puntos;

    public Instalacion(String cliente, String direccion,
                       double horasEstimadas, Zona zona, int puntos) {

        super(cliente, direccion, horasEstimadas, zona);

        if (puntos <= 0) {
            throw new IllegalArgumentException(
                "Los puntos deben ser un entero positivo."
            );
        }

        this.puntos = puntos;
    }

    @Override
    public BigDecimal calcularCosto() {
        BigDecimal horasPorPuntos = BigDecimal.valueOf(puntos)
                .multiply(new BigDecimal("0.75"));

        BigDecimal horasFacturables = BigDecimal.valueOf(getHorasEstimadas())
                .max(horasPorPuntos)
                .setScale(0, RoundingMode.CEILING);

        return calcularTarifaBase()
                .add(horasFacturables.multiply(new BigDecimal("90.00")))
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public int estimarDiasAtencion() {
        if (getZona() == Zona.METROPOLITANA) {
            return 3;
        }

        return 5;
    }

    @Override
    public int obtenerMaximoVisitas() {
        return 3;
    }

    @Override
    public String obtenerCategoria() {
        return "Instalación";
    }

    public int getPuntos() {
        return puntos;
    }

    @Override
    public String toString() {
        return super.toString() + " | Puntos: " + puntos;
    }
}