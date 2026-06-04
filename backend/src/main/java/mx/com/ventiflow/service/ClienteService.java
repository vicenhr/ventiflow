package mx.com.ventiflow.service;

import jakarta.transaction.Transactional;
import mx.com.ventiflow.domain.cliente.Cliente;
import mx.com.ventiflow.domain.cliente.ClienteRepository;
import mx.com.ventiflow.domain.cliente.DatosRegistroCliente;
import mx.com.ventiflow.domain.cliente.DatosRespuestaCliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository repository;

    @Transactional
    public DatosRespuestaCliente registrar(DatosRegistroCliente datos) {
        Cliente cliente = new Cliente(datos);
        repository.save(cliente);
        return new DatosRespuestaCliente(cliente);
    }


    public Optional<DatosRespuestaCliente> buscarPorTelefono(String telefono) {
        // Buscamos al cliente y, si existe, lo transformamos en nuestro DTO de respuesta
        return repository.findByTelefono(telefono)
                .map(DatosRespuestaCliente::new);
    }
}
