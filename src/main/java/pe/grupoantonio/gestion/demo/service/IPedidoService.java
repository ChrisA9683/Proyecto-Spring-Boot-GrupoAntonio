package pe.grupoantonio.gestion.demo.service;

import pe.grupoantonio.gestion.demo.model.Pedido;
import pe.grupoantonio.gestion.demo.model.CheckoutData;
import jakarta.validation.ValidationException;

// La firma de crearYProcesar debe incluir el usuarioId para la lógica de negocio
public interface IPedidoService {

    Pedido obtenerUltimoPedidoPorCliente(Long usuarioId);

    Long crearYProcesar(Long usuarioId, CheckoutData checkoutData) throws ValidationException;

}