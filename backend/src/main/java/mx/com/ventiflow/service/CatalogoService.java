package mx.com.ventiflow.service;

import mx.com.ventiflow.domain.producto.DatosProducto;
import mx.com.ventiflow.domain.producto.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CatalogoService {

    private final ProductoRepository productoRepository;

    public CatalogoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public List<DatosProducto> obtenerProductosDisponibles() {
        return productoRepository.findByEstadoTrue()
                .stream()
                .map(DatosProducto::new)
                .collect(Collectors.toList());
    }
}