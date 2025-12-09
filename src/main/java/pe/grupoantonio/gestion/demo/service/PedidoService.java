package pe.grupoantonio.gestion.demo.service;

import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.grupoantonio.gestion.demo.dto.CartTransferDTO;
import pe.grupoantonio.gestion.demo.model.*;
import pe.grupoantonio.gestion.demo.repository.*;

import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PedidoService implements IPedidoService {

    @Autowired private PedidoRepository pedidoRepository;
    @Autowired private DetallePedidoRepository detallePedidoRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private ProductoRepository productoRepository;
    @Autowired private DireccionRepository direccionRepository;
    @Autowired private MetodoPagoRepository metodoPagoRepository;

    // =======================================================
    // 1. MÉTODO PARA OBTENER EL ÚLTIMO PEDIDO
    // =======================================================

    @Override
    @Transactional(readOnly = true)
    public Pedido obtenerUltimoPedidoPorCliente(Long usuarioId) {
        return pedidoRepository.findTopByUsuarioIdOrderByFechaCreacionDesc(usuarioId)
                .orElse(null);
    }

    // =======================================================
    // 2. MÉTODO PRINCIPAL DE CREACIÓN Y PROCESAMIENTO (crearYProcesar)
    // =======================================================

    @Override 
    @Transactional
    public Long crearYProcesar(Long usuarioId, CheckoutData checkoutData) throws ValidationException {

        // 1. BUSCAR EL USUARIO REAL
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + usuarioId));

        // 1.1 Validaciones de datos mínimos
        if (checkoutData.getMetodoPago() == null || checkoutData.getTotal().signum() <= 0) {
             throw new ValidationException("Datos de pago o total inválidos.");
        }
        if (checkoutData.getItemsCarrito() == null || checkoutData.getItemsCarrito().isEmpty()) {
             throw new ValidationException("El carrito está vacío.");
        }
        
        // 1.2 Validación de Facturación
        if (checkoutData.isSolicitaFactura()) {
            if (checkoutData.getRuc() == null || checkoutData.getRuc().trim().isEmpty() || checkoutData.getRuc().length() < 11) {
                throw new ValidationException("Debe ingresar un RUC válido para solicitar factura.");
            }
            if (checkoutData.getRazonSocial() == null || checkoutData.getRazonSocial().trim().isEmpty()) {
                throw new ValidationException("Debe ingresar la Razón Social.");
            }
        }

        // 2. GUARDAR LA DIRECCIÓN DE ENVÍO
        Direccion direccion = new Direccion();
        direccion.setUsuario(usuario);
        direccion.setTipoDireccion("ENVIO");
        
        // Asignación de campos de dirección detallada
        direccion.setCalle(checkoutData.getDireccion());
        direccion.setReferencia(checkoutData.getReferencia());
        direccion.setDepartamento(checkoutData.getDepartamento());
        direccion.setDistrito(checkoutData.getDistrito());
        direccion.setTelefonoReceptor(checkoutData.getTelefono());
        direccion.setNombreReceptor(checkoutData.getNombre());
        direccion.setCiudad(checkoutData.getDepartamento()); 

        direccion = direccionRepository.save(direccion);

        // 3. GUARDAR EL MÉTODO DE PAGO
        MetodoPago metodoPago = new MetodoPago();
        metodoPago.setUsuario(usuario);
        
        String tipoPagoInput = checkoutData.getMetodoPago();
        String estadoPedido;
        String numeroTarjeta = checkoutData.getNumTarjeta();

        if ("TARJETA".equalsIgnoreCase(tipoPagoInput)) {
            // Lógica para TARJETA (Simulación)
            if (numeroTarjeta == null || numeroTarjeta.length() < 16) {
                throw new ValidationException("Número de tarjeta incompleto o inválido.");
            }
            
            // ✅ CORRECCIÓN FINAL Y DEFINITIVA: Usamos el valor exacto de la DB.
            metodoPago.setTipoPago("TARJETA_CREDITO"); 
            metodoPago.setNombreTitular(checkoutData.getNombreTarjeta());
            metodoPago.setMarcaTarjeta("VISA/MC");
            
            metodoPago.setUltimosDigitosTarjeta(numeroTarjeta.substring(numeroTarjeta.length() - 4));
            
            estadoPedido = "APROBADO"; 
            
        } else if ("YAPE".equalsIgnoreCase(tipoPagoInput) || "TRANSFERENCIA".equalsIgnoreCase(tipoPagoInput)) {
            // Lógica para Pagos Manuales
            if (checkoutData.getReferenciaExterna() == null || checkoutData.getReferenciaExterna().isEmpty()) {
                throw new ValidationException("Debe ingresar la Referencia Externa para pagos manuales.");
            }
            metodoPago.setTipoPago(tipoPagoInput.toUpperCase());
            metodoPago.setReferenciaExterna(checkoutData.getReferenciaExterna());
            estadoPedido = "PENDIENTE"; 
            
        } else {
            throw new ValidationException("Método de pago no soportado.");
        }

        metodoPago = metodoPagoRepository.save(metodoPago);

        // 4. CREAR Y GUARDAR EL PEDIDO CABECERA
        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setEstado(estadoPedido); 
        pedido.setFechaCreacion(LocalDateTime.now()); 
        pedido.setTotal(checkoutData.getTotal());
        pedido.setCostoEnvio(checkoutData.getCostoEnvio());
        
        // ASIGNACIÓN DE DATOS DE FACTURACIÓN (Si corresponde)
        if (checkoutData.isSolicitaFactura()) {
            // Si tu modelo Pedido tiene estos setters:
            // pedido.setRuc(checkoutData.getRuc()); 
            // pedido.setRazonSocial(checkoutData.getRazonSocial());
        }

        // Asignamos las relaciones
        pedido.setDireccionId(direccion.getId());
        pedido.setMetodoPago(metodoPago);

        pedido = pedidoRepository.save(pedido);

        // 5. GUARDAR LOS DETALLES DEL PEDIDO
        List<DetallePedido> detalles = new ArrayList<>();

        for (CartTransferDTO item : checkoutData.getItemsCarrito()) {
            // Buscar el producto real
            Long idProducto = Long.valueOf(item.getId()); 
            Producto producto = productoRepository.findById(idProducto)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado: ID " + idProducto));

            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedido);
            detalle.setProducto(producto);
            detalle.setCantidad(item.getCantidad());

            if (item.getPrecio() != null) {
                detalle.setPrecioUnitario(java.math.BigDecimal.valueOf(item.getPrecio()));
            }

            detalles.add(detalle);
        }

        detallePedidoRepository.saveAll(detalles);

        return pedido.getId();
    }
}