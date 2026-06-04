package mx.com.ventiflow.domain.producto;

import java.math.BigDecimal;

public record DatosProducto(
        Long idProducto,
        String nombreProducto,
        BigDecimal precio,
        String areaPreparacion,
        Long idCategoria
) {
    public DatosProducto(Producto producto){
        this(
                producto.getIdProducto(),
                producto.getNombre(),
                producto.getPrecio(),
                producto.getCategoria().getAreaPreparacion(),
                producto.getCategoria().getIdCategoria()
        );
    }
}
