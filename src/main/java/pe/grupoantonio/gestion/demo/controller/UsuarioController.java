package pe.grupoantonio.gestion.demo.controller;

// --- IMPORTS OBLIGATORIOS ---
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pe.grupoantonio.gestion.demo.dto.UsuarioDTO;
import pe.grupoantonio.gestion.demo.model.*;
import pe.grupoantonio.gestion.demo.repository.*;
import pe.grupoantonio.gestion.demo.service.IUsuarioService;
import pe.grupoantonio.gestion.demo.service.ProductoService;
import pe.grupoantonio.gestion.demo.service.UsuarioService;
import pe.grupoantonio.gestion.demo.service.PedidoService;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    // --- INYECCIONES DE DEPENDENCIA ---
@Autowired
    private ProductoService productoService;
    @Autowired
    private IUsuarioService usuarioService;
    @Autowired
    private UsuarioRepository repo;
    @Autowired
    private RolRepository rolRepository;
    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private DetallePedidoRepository detallePedidoRepository;
    @Autowired
    private DireccionRepository direccionRepository;
    @Autowired
    private PedidoService pedidoService;

     private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();
     private final SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();

     // ========================================================
// 🛠️ MÉTODO AYUDANTE (CRUCIAL PARA EL SIDEBAR)
// ========================================================
private void cargarDatosUsuario(Model model, Principal principal) {
        if (principal != null) {
            Usuario usuario = repo.findByEmail(principal.getName()).orElse(null);
            if (usuario != null) {
                model.addAttribute("nombreUsuario", usuario.getNombre());
                
// Enviamos el rol en MAYÚSCULAS para que el HTML lo entienda
if (usuario.getRol() != null) {
                    model.addAttribute("rolUsuario", usuario.getRol().getNombre().toUpperCase());
                  } else {

                    model.addAttribute("rolUsuario", "USUARIO");
                }

            }

        }

    

    }

// --- RUTAS PÚBLICAS ---
@GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/registro")
    public String registro() {
        return "registro";
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    // ========================================================
// ✅ MÉTODO DE CONFIRMACIÓN DE ORDEN
// ========================================================
@PostMapping("/confirmar-orden")
    
    public String confirmOrder(
            @RequestParam("metodoPago") String metodoPago,
            @RequestParam(value = "nombreTarjeta", required = false) String nombreTarjeta,
            @RequestParam(value = "referenciaExterna", required = false) String referenciaExterna,
            @ModelAttribute("checkoutData") CheckoutData checkoutData,
            SessionStatus status,
            RedirectAttributes redirectAttributes,
            Principal principal) {
        // 1. CHEQUEO BÁSICO DE SESIÓN
if (principal == null) {
            redirectAttributes.addFlashAttribute("error", "Debe iniciar sesión para completar la compra.");
            return "redirect:/usuario/login";
        }


        // Actualizar CheckoutData
checkoutData.setMetodoPago(metodoPago);
        checkoutData.setNombreTarjeta(nombreTarjeta);
        checkoutData.setReferenciaExterna(referenciaExterna);
        // Validación básica de campos de pago
if ("TARJETA".equals(metodoPago) && (nombreTarjeta == null || nombreTarjeta.isEmpty())) {
            redirectAttributes.addFlashAttribute("error", "Debe ingresar el nombre del titular de la tarjeta.");
            return "redirect:/checkout/pago";
        }


        try {
            // 2. OBTENER EL USUARIO LOGUEADO Y SU ID DE FORMA SEGURA
Usuario usuarioLogueado = usuarioService.buscarPorEmail(principal.getName());
            if (usuarioLogueado == null || usuarioLogueado.getId() == null) {
                throw new RuntimeException("No se pudo obtener el ID de usuario logueado.");
            }


            Long usuarioId = usuarioLogueado.getId();
            // 3. GUARDADO TRANSACCIONAL
Long pedidoId = pedidoService.crearYProcesar(usuarioId, checkoutData);
            // 4. Finalizar la sesión (eliminar checkoutData)
status.setComplete();
            return "redirect:/checkout/exito?pedidoId=" + pedidoId;
         } catch (ValidationException e) {
            redirectAttributes.addFlashAttribute("error", "Error de Validación: " + e.getMessage());
            return "redirect:/checkout/pago";
         } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error interno al procesar el pedido: " + e.getMessage());
            return "redirect:/checkout/pago";
        }

    

    }

// ========================================================
// 👤 GESTIÓN DE USUARIOS
// ========================================================
    /**
     * Guarda un nuevo usuario o actualiza uno existente en la base de datos.
     * Esta acción es invocada por el formulario de creación/edición de usuarios.
     * @param usuario Objeto Usuario recibido del formulario con los datos a guardar.
     * @param model Objeto Model de Spring para pasar datos a la vista.
     * @return String La redirección a la lista de usuarios.
     */
    @PostMapping("/guardar") // ✅ CORREGIDO: Mapea a /usuario/guardar
    public String guardarUsuario(@ModelAttribute Usuario usuario, Model model) {
        usuarioService.registrarUsuario(usuario);
        return "redirect:/usuario/panel_usuario";
    }

    /**
     * Elimina un usuario de la base de datos por su ID. Este método solo es
     * accesible por el rol ADMIN. Maneja excepciones si el usuario tiene
     * dependencias (ej: pedidos).
     *
     * @param id El ID del usuario a eliminar.
     * @return String "OK" si se eliminó, "NOT_FOUND" si no existe, o "ERROR" si
     * falla la eliminación (ej: Foreign Key).
     */
    @PostMapping("/eliminar/{id}")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')") // Solo Administradores pueden eliminar
    public String eliminarUsuario(@PathVariable Long id) {
        try {
            if (repo.existsById(id)) {
                // Por ahora, solo eliminamos
                repo.deleteById(id);
                return "OK";
            } else {
                return "NOT_FOUND";
            }
        } catch (Exception e) {
            // En caso de que el usuario tenga pedidos asociados (violación de Foreign Key)
            System.err.println("Error al eliminar usuario con ID: " + id + ". Posiblemente tiene dependencias.");
            e.printStackTrace();
            return "ERROR";
        }
    }

    // --- REGISTRO (ADAPTADO A ROL) ---
@PostMapping("/registro")
    
    public String crear(@ModelAttribute UsuarioDTO usuarioDTO, Model model) {
        if (!usuarioDTO.getPassword().equals(usuarioDTO.getConfirmPassword())) {
            model.addAttribute("error", "Las contraseñas no coinciden.");
            return "registro";
        }

        if (repo.existsByEmail(usuarioDTO.getEmail())) {
            model.addAttribute("error", "El correo ya existe.");
            return "registro";
        }


        Usuario usuario = new Usuario();
        usuario.setNombre(usuarioDTO.getNombre());
        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setPassword(passwordEncoder.encode(usuarioDTO.getPassword()));
        // --- ADAPTACIÓN: Buscar objeto Rol USER ---
Rol rolUser = rolRepository.findByNombre("USER").orElse(null);
        if (rolUser != null) {
            usuario.setRol(rolUser);
        }


        repo.save(usuario);
        return "redirect:/usuario/login";
    

    }

// --- CAMBIO RÁPIDO DE ROL (AJAX) ---
@PostMapping("/cambiar-rol/{id}")
    
    @ResponseBody
public String cambiarRolRapido(@PathVariable Long id, @RequestParam("rol") String nombreRol) {
        Usuario u = repo.findById(id).orElse(null);
        Rol r = rolRepository.findByNombre(nombreRol).orElse(null);
        if (u != null && r != null) {
            u.setRol(r);
            repo.save(u);
            return "OK";
        }

        return "ERROR";
    

    }

// ========================================================
// 📊 DASHBOARD PRINCIPAL (ADMIN)
// ========================================================
@GetMapping("/dashboard")
    
    public String dashboard(@RequestParam(defaultValue = "0") int page, Model model, Principal principal) {
        
// 1. CARGAMOS DATOS DEL USUARIO (NOMBRE Y ROL) PARA EL MENÚ
cargarDatosUsuario(model, principal);
        // 2. Estadísticas (Para el Admin)
long totalPedidos = pedidoRepository.count();
        long pedidosPendientes = pedidoRepository.countByEstado("PENDIENTE");
        long pedidosVendidos = pedidoRepository.countByEstado("VENDIDO");
        long totalClientes = repo.count();
        BigDecimal gananciasRaw = pedidoRepository.sumarGananciasTotales();
        BigDecimal totalGanancias = (gananciasRaw != null) ? gananciasRaw : BigDecimal.ZERO;
        // 3. Paginación de Pedidos
Page<Pedido> pedidoPage = pedidoRepository.findAllByOrderByFechaCreacionDesc(PageRequest.of(page, 14));
        model.addAttribute("listaPedidos", pedidoPage.getContent());
        model.addAttribute("totalPaginas", pedidoPage.getTotalPages());
        model.addAttribute("paginaActual", page);
        // 4. Gráficos (Filtrar solo VENDIDO)
List<Pedido> pedidosParaGrafico = pedidoPage.getContent().stream()
        .filter(p -> "VENDIDO".equals(p.getEstado()))
        .collect(Collectors.toList());
        List<String> fechas = new ArrayList<>();
        List<BigDecimal> montos = new ArrayList<>();
        for (Pedido p : pedidosParaGrafico) {
            fechas.add(p.getFechaCreacion().toLocalDate().toString());
            montos.add(p.getTotal());
        }


        // 5. Enviar variables a la vista
model.addAttribute("totalPedidos", totalPedidos);
        model.addAttribute("pedidosPendientes", pedidosPendientes);
        model.addAttribute("pedidosVendidos", pedidosVendidos);
        model.addAttribute("totalClientes", totalClientes);
        model.addAttribute("totalGanancias", totalGanancias);
        model.addAttribute("graficoFechas", fechas);
        model.addAttribute("graficoMontos", montos);
        return "dashboard/inicio-dashboard";
    

    }

// ========================================================
// 📦 GESTIÓN DE PEDIDOS (ADMIN / VENDEDOR)
// ========================================================

@PostMapping("/pedido/vender/{id}")
    
    @ResponseBody
public String marcarComoVendido(@PathVariable Long id) {
        Pedido pedido = pedidoRepository.findById(id).orElse(null);
        if (pedido != null) {
            pedido.setEstado("VENDIDO");
            pedidoRepository.save(pedido);
            return "OK";
        }

        return "ERROR";
    

    }

@PostMapping("/pedido/cambiar-estado/{id}")
    
    @ResponseBody
public String cambiarEstadoPedido(@PathVariable Long id, @RequestParam("estado") String nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(id).orElse(null);
        if (pedido != null) {
            pedido.setEstado(nuevoEstado);
            pedidoRepository.save(pedido);
            return "OK";
        }

        return "ERROR";
    

    }

@PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    
    @GetMapping("/pedidos")
public String verPedidos(Model model, Principal principal) {
        // ✅ Cargar Rol para el Menú Lateral
cargarDatosUsuario(model, principal);
        // Obtener lista ordenada
List<Pedido> listaPedidos = pedidoRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("listaPedidos", listaPedidos);
        model.addAttribute("totalPedidos", listaPedidos.size());
        return "dashboard/Pedido_detalle";
    

    }

// --- OBTENER DETALLE PEDIDO (AJAX) ---
@GetMapping("/pedido/detalle/{id}")
    
    @ResponseBody
public Map<String, Object> obtenerDetallePedido(@PathVariable Long id) {
        Map<String, Object> respuesta = new HashMap<>();
        Pedido pedido = pedidoRepository.findById(id).orElse(null);
        if (pedido != null) {
            respuesta.put("id", pedido.getId());
            respuesta.put("fecha", pedido.getFechaCreacion().toString());
            respuesta.put("estado", pedido.getEstado());
            respuesta.put("total", pedido.getTotal());
            respuesta.put("envio", pedido.getCostoEnvio());
            if (pedido.getMetodoPago() != null) {
                respuesta.put("metodoPago", pedido.getMetodoPago().getTipoPago());
              } else {

                respuesta.put("metodoPago", "No especificado");
            }


            Usuario usuario = pedido.getUsuario();
            Map<String, String> datosCliente = new HashMap<>();
            datosCliente.put("email", usuario.getEmail());
            Long idDireccion = pedido.getDireccionId();
            if (idDireccion != null) {
                Direccion dir = direccionRepository.findById(idDireccion).orElse(null);
                if (dir != null) {
                    datosCliente.put("nombre", dir.getNombreReceptor());
                    datosCliente.put("telefono", dir.getTelefonoReceptor());
                    datosCliente.put("calle", dir.getCalle());
                    datosCliente.put("referencia", dir.getReferencia());
                    datosCliente.put("distrito", dir.getDistrito());
                    datosCliente.put("ciudad", dir.getCiudad());
                    datosCliente.put("departamento", dir.getDepartamento());
                  } else {

                    datosCliente.put("nombre", usuario.getNombre());
                    datosCliente.put("calle", "Dirección no encontrada");
                }

              } else {

                datosCliente.put("nombre", usuario.getNombre());
                datosCliente.put("calle", "Retiro en tienda");
            }


            respuesta.put("cliente", datosCliente);
            // Obtener productos del pedido
List<DetallePedido> detalles = detallePedidoRepository.findByPedidoId(id);
            List<Map<String, Object>> productosJson = new ArrayList<>();
            for (DetallePedido dp : detalles) {
                Map<String, Object> pJson = new HashMap<>();
                Producto prod = dp.getProducto();
                if (prod != null) {
                    pJson.put("nombre", prod.getNombre());
                    pJson.put("imagen", prod.getImagenUrl());
                  } else {

                    pJson.put("nombre", "Producto no disponible");
                    pJson.put("imagen", "https://via.placeholder.com/50");
                }


                pJson.put("precio", dp.getPrecioUnitario());
                pJson.put("cantidad", dp.getCantidad());
                BigDecimal precio = dp.getPrecioUnitario() != null ? dp.getPrecioUnitario() : BigDecimal.ZERO;
                BigDecimal cantidad = new BigDecimal(dp.getCantidad() != null ? dp.getCantidad() : 0);
                pJson.put("subtotal", precio.multiply(cantidad));
                productosJson.add(pJson);
            }

            respuesta.put("productos", productosJson);
        }

        return respuesta;
    

    }

// ========================================================
// 🛍️ CATALOGO Y PRODUCTOS
// ========================================================
@GetMapping("/catalogo")
    
    public String catalogo(Model model) {
        List<Producto> productos = productoRepository.findAll().stream()
        .filter(p -> "Activo".equals(p.getEstado()))
        .collect(Collectors.toList());
        model.addAttribute("productos", productos);
        return "catalogo";
    

    }

@PostMapping("/producto/cambiar-estado/{id}")
    
    @ResponseBody
public String cambiarEstadoProducto(@PathVariable Long id) {
        Producto producto = productoRepository.findById(id).orElse(null);
        if (producto != null) {
            String nuevoEstado = "Activo".equals(producto.getEstado()) ? "Inactivo" : "Activo";
            producto.setEstado(nuevoEstado);
            productoRepository.save(producto);
            return nuevoEstado;
        }

        return "ERROR";
    

    }

@PreAuthorize("hasRole('ADMIN')")
    
    @GetMapping("/panel_usuario")
public String mostrarPanelUsuarios(Model model, Principal principal) {
        // ✅ Cargar Rol para el Menú Lateral
cargarDatosUsuario(model, principal);
        List<Usuario> listaUsuarios = usuarioService.obtenerTodosLosUsuarios();
        model.addAttribute("listaUsuarios", listaUsuarios);
        return "dashboard/panel_usuario";
    

    }

@PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    
    @GetMapping("/panel_productos")
public String panel_productos(Model model, Principal principal) {
        // ✅ Cargar Rol para el Menú Lateral
cargarDatosUsuario(model, principal);
        List<Producto> listaProductos = productoRepository.findAll();
        model.addAttribute("listaProductos", listaProductos);
        model.addAttribute("totalProductos", listaProductos.size());
        return "dashboard/panel_productos";
    

    }

@PostMapping("/producto/guardar")
    
    public String guardarProducto(@ModelAttribute Producto producto, @RequestParam("file") MultipartFile file) {
        if (!file.isEmpty()) {
            try {
                String rutaRaiz = System.getProperty("user.dir");
                Path directorioImagenes = Paths.get(rutaRaiz, "src", "main", "resources", "static", "imagenes", "productos");
                String rutaAbsoluta = directorioImagenes.toFile().getAbsolutePath();
                File directorio = new File(rutaAbsoluta);
                if (!directorio.exists()) {
                    directorio.mkdirs();
                }


                String nombreArchivo = file.getOriginalFilename();
                byte[] bytesImg = file.getBytes();
                Path rutaCompleta = Paths.get(rutaAbsoluta + "/" + nombreArchivo);
                Files.write(rutaCompleta, bytesImg);
                producto.setImagenUrl("/imagenes/productos/" + nombreArchivo);
             } catch (IOException e) {
                e.printStackTrace();
            }

          } else {

            if (producto.getId() != null) {
                Producto prodActual = productoRepository.findById(producto.getId()).orElse(null);
                if (prodActual != null) {
                    producto.setImagenUrl(prodActual.getImagenUrl());
                    if (producto.getEstado() == null) {
                        producto.setEstado(prodActual.getEstado());
                    }
                }

            }

        }


        productoRepository.save(producto);
        return "redirect:/usuario/panel_productos";
    

    }

@PostMapping("/producto/eliminar/{id}")
    
    @ResponseBody
public String eliminarProducto(@PathVariable Long id) {
        try {
            if (productoRepository.existsById(id)) {
                productoRepository.deleteById(id);
                return "OK";
              } else {

                return "NOT_FOUND";
            }

         } catch (Exception e) {
            return "ERROR";
        }

    

    }

// ========================================================
// 🚚 SEGUIMIENTO DEL CLIENTE (CORREGIDO 'USER')
// ========================================================
@PreAuthorize("hasAnyRole('ADMIN', 'USUARIO', 'USER')")
    
    @GetMapping("/seguimiento")
public String mostrarPanelSeguimientoCliente(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/usuario/login";
        }


        // ✅ Cargar Rol para el Menú Lateral
cargarDatosUsuario(model, principal);
        Usuario cliente = repo.findByEmail(principal.getName()).orElse(null);
        if (cliente == null) {
            return "redirect:/usuario/login";
        }


        Pedido ultimoPedido = pedidoService.obtenerUltimoPedidoPorCliente(cliente.getId());
        if (ultimoPedido == null) {
            model.addAttribute("nombreCliente", cliente.getNombre());
            return "seguimiento/sin_pedidos";
        }


        model.addAttribute("nombreCliente", cliente.getNombre());
        model.addAttribute("ultimoPedido", ultimoPedido);
        return "dashboard/panel_seguimiento";
    

    }


// --- PÁGINAS ESTÁTICAS ---
@GetMapping("/servicios")
    public String servicios() {
        return "servicios";
    }

    @GetMapping("/contacto")
    public String contacto() {
        return "contacto";
    }

     @GetMapping("/carrito")
    public String carrito() {
        return "carrito";
    }
}
