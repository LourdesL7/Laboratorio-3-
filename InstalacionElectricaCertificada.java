import java.math.BigDecimal;
import java.math.RoundingMode;

public class InstalacionElectricaCertificada extends Instalacion {
    private final BigDecimal valorMateriales;

    public InstalacionElectricaCertificada(
            String cliente,
            String direccion,
            double horasEstimadas,
            Zona zona,
            int puntos,
            BigDecimal valorMateriales) {

        super(cliente, direccion, horasEstimadas, zona, puntos);

        if (valorMateriales == null || valorMateriales.signum() <= 0) {
            throw new IllegalArgumentException(
                "El valor de materiales debe ser positivo."
            );
        }

        this.valorMateriales = valorMateriales;
    }

    @Override
    public BigDecimal calcularCosto() {
        BigDecimal garantia = valorMateriales
                .multiply(new BigDecimal("0.05"));

        return super.calcularCosto()
                .add(new BigDecimal("120.00"))
                .add(garantia)
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public int estimarDiasAtencion() {
        return super.estimarDiasAtencion() + 2;
    }

    @Override
    public int obtenerMaximoVisitas() {
        return 2;
    }

    @Override
    public String obtenerCategoria() {
        return "Instalación eléctrica certificada";
    }

    public BigDecimal getValorMateriales() {
        return valorMateriales;
    }

    @Override
    public String toString() {
        return super.toString()
                + " | Materiales: Q"
                + valorMateriales.setScale(2, RoundingMode.HALF_UP);
    }
}