package mx.com.ventiflow.domain.categoria;

public enum AreaPreparacion {
    BARRA("BARRA"),
    COCINA("COCINA");

    private final String valor;

    AreaPreparacion(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }
}
