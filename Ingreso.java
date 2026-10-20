import java.math.BigDecimal;
import java.math.RoundingMode;

public class Ingreso {
    private final OrdenServicio orden;
    private final ConceptoIngreso concepto;
    private final BigDecimal monto;

    public Ingreso(OrdenServicio orden, ConceptoIngreso concepto,
                   BigDecimal monto) {

        if (orden == null || orden.getCodigo() == null) {
            throw new IllegalArgumentException(
                "El ingreso requiere una orden registrada."
            );
        }

        if (concepto == null) {
            throw new IllegalArgumentException(
                "El concepto de ingreso no es válido."
            );
        }

        if (monto == null || monto.signum() <= 0) {
            throw new IllegalArgumentException(
                "El monto del ingreso debe ser positivo."
            );
        }

        this.orden = orden;
        this.concepto = concepto;
        this.monto = monto.setScale(2, RoundingMode.HALF_UP);
    }

    public OrdenServicio getOrden() {
        return orden;
    }

    public ConceptoIngreso getConcepto() {
        return concepto;
    }

    public BigDecimal getMonto() {
        return monto;
    }
}