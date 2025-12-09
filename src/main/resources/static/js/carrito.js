// ==========================================
// CONFIGURACIÓN Y REFERENCIAS
// ==========================================
const CARRITO_STORAGE_KEY = 'carritoGrupoAntonio';
const IGV_RATE = 0.18;
const ENVIO_COSTO = 50.00;

// Referencias del DOM
const listaContainer = document.getElementById('lista-productos-carrito');
const mensajeVacio = document.getElementById('carrito-vacio-mensaje');
const btnContinuar = document.querySelector('.btn-continuar');

// Elementos de Resumen
const lblSubtotal = document.getElementById('resumen-subtotal');
const lblEnvio = document.getElementById('resumen-envio');
const lblIgv = document.getElementById('resumen-igv');
const lblTotal = document.getElementById('resumen-total');

// Estado Global
let isUserLoggedIn = false;

// ==========================================
// 1. INICIALIZACIÓN Y AUTO-SYNC INTELIGENTE
// ==========================================
document.addEventListener('DOMContentLoaded', async () => {
    
    try {
        // Consultamos el estado actual al servidor
        const response = await fetch('/api/carrito');
        
        if (response.ok) {
            // ✅ ESTADO: LOGUEADO
            isUserLoggedIn = true;
            let itemsDB = await response.json();
            
            // 🚨 DETECCIÓN DE DATOS LOCALES (FUSIÓN)
            const itemsLocal = JSON.parse(localStorage.getItem(CARRITO_STORAGE_KEY) || '[]');
            
            if (itemsLocal.length > 0) {
                console.log("Detectados items locales. Iniciando Fusión...");
                
                // 1. Subimos los datos locales al servidor
                const exitoSync = await sincronizarCarritoConBackend(itemsLocal);
                
                if (exitoSync) {
                    console.log("Fusión correcta. Obteniendo carrito actualizado...");
                    
                    // 2. Limpiamos navegador (ya están seguros en DB)
                    localStorage.removeItem(CARRITO_STORAGE_KEY);
                    
                    // 3. PEDIMOS LA LISTA ACTUALIZADA A LA DB (SIN RECARGAR PÁGINA)
                    const responseFresh = await fetch('/api/carrito');
                    if (responseFresh.ok) {
                        itemsDB = await responseFresh.json(); // Actualizamos la lista con la fusión
                    }
                } else {
                    console.warn("Fallo en sincronización. Manteniendo copia local por seguridad.");
                }
            }
            
            // Renderizamos los datos finales (sean solo de DB o fusionados)
            renderizarCarrito(itemsDB, 'DB');
            
        } else if (response.status === 401) {
            // 🔒 ESTADO: ANÓNIMO
            isUserLoggedIn = false;
            const itemsLocal = JSON.parse(localStorage.getItem(CARRITO_STORAGE_KEY) || '[]');
            renderizarCarrito(itemsLocal, 'LOCAL');
        } 
    } catch (error) {
        console.error("Error crítico en inicialización:", error);
        // Fallback para no dejar al usuario en blanco
        const itemsLocal = JSON.parse(localStorage.getItem(CARRITO_STORAGE_KEY) || '[]');
        renderizarCarrito(itemsLocal, 'LOCAL');
    }

    if (btnContinuar) {
        btnContinuar.addEventListener('click', procesarPedido);
    }
});

// ==========================================
// 2. RENDERIZADO VISUAL
// ==========================================
function renderizarCarrito(items, origen) {
    if (!listaContainer) return;

    listaContainer.innerHTML = '<h2 class="titulo-seccion-carrito">Productos en tu Carrito</h2>';
    
    if (!items || items.length === 0) {
        mensajeVacio.style.display = 'block';
        actualizarTotales([]);
        if(btnContinuar) btnContinuar.disabled = true;
        return;
    }

    mensajeVacio.style.display = 'none';
    if(btnContinuar) btnContinuar.disabled = false;

    items.forEach(item => {
        // Normalización de datos para que funcione con DB y LocalStorage
        const id = item.id || item.productoId; 
        const nombre = item.nombre || (item.producto ? item.producto.nombre : 'Producto');
        const imagen = item.imagen || (item.producto ? item.producto.imagenUrl : '/imagenes/placeholder.png');
        // Aseguramos conversión a número
        const precio = parseFloat(item.precioUnitario !== undefined ? item.precioUnitario : (item.precio || 0));
        const cantidad = parseInt(item.cantidad || 0);
        
        // ID para borrar/editar: Si es DB usamos el ID del detalle, si es Local el del producto
        const dataId = origen === 'DB' ? item.id : item.id; 

        const html = `
            <div class="producto-carrito-item">
                <img src="${imagen}" alt="${nombre}" class="producto-carrito-imagen">
                
                <div class="producto-carrito-info">
                    <div class="producto-carrito-nombre">${nombre}</div>
                    <div class="producto-carrito-precio">S/ ${precio.toFixed(2)}</div>
                    <button class="btn-eliminar-producto" onclick="eliminarItem(${dataId}, '${origen}')">
                        <i class="fas fa-trash"></i> Eliminar
                    </button>
                </div>
                
                <div class="cantidad-producto">
                    <input type="number" 
                           class="input-cantidad" 
                           value="${cantidad}" 
                           min="1" 
                           onchange="actualizarCantidad(${dataId}, this.value, '${origen}')"
                           style="width: 60px; padding: 5px; text-align: center;">
                </div>

                <div class="subtotal-producto" style="font-weight: 700; width: 100px; text-align: right;">
                    S/ ${(precio * cantidad).toFixed(2)}
                </div>
            </div>
        `;
        listaContainer.insertAdjacentHTML('beforeend', html);
    });

    actualizarTotales(items);
}

// ==========================================
// 3. CÁLCULOS
// ==========================================
function actualizarTotales(items) {
    let subtotal = 0;
    
    items.forEach(item => {
        const precio = parseFloat(item.precioUnitario !== undefined ? item.precioUnitario : (item.precio || 0));
        const cantidad = parseInt(item.cantidad || 0);
        subtotal += precio * cantidad;
    });

    const igv = subtotal * IGV_RATE;
    const envio = subtotal > 0 ? ENVIO_COSTO : 0;
    const total = subtotal + igv + envio;

    if(lblSubtotal) lblSubtotal.textContent = `S/ ${subtotal.toFixed(2)}`;
    if(lblIgv) lblIgv.textContent = `S/ ${igv.toFixed(2)}`;
    if(lblEnvio) lblEnvio.textContent = `S/ ${envio.toFixed(2)}`;
    if(lblTotal) lblTotal.textContent = `S/ ${total.toFixed(2)}`;
}

// ==========================================
// 4. ACCIONES (Eliminar / Actualizar)
// ==========================================
async function eliminarItem(id, origen) {
    if(!confirm("¿Eliminar producto?")) return;

    if (origen === 'LOCAL') {
        let items = JSON.parse(localStorage.getItem(CARRITO_STORAGE_KEY) || '[]');
        items = items.filter(i => String(i.id) !== String(id));
        localStorage.setItem(CARRITO_STORAGE_KEY, JSON.stringify(items));
        renderizarCarrito(items, 'LOCAL'); // Repintar sin recargar
    } else {
        try {
            const res = await fetch(`/api/carrito/eliminar/${id}`, { method: 'DELETE' });
            if(res.ok) window.location.reload(); // Aquí sí recargamos para asegurar consistencia DB
        } catch(e) { console.error(e); }
    }
}

async function actualizarCantidad(id, cantidad, origen) {
    if (cantidad < 1) return;

    if (origen === 'LOCAL') {
        let items = JSON.parse(localStorage.getItem(CARRITO_STORAGE_KEY) || '[]');
        const item = items.find(i => String(i.id) === String(id));
        if (item) {
            item.cantidad = parseInt(cantidad);
            localStorage.setItem(CARRITO_STORAGE_KEY, JSON.stringify(items));
            renderizarCarrito(items, 'LOCAL'); // Repintar sin recargar
        }
    } else {
        try {
            await fetch(`/api/carrito/actualizar/${id}`, {
                method: 'PUT',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({ cantidad: cantidad })
            });
            window.location.reload();
        } catch(e) { console.error(e); }
    }
}

// ==========================================
// 5. PROCESAR PEDIDO
// ==========================================
async function procesarPedido(e) {
    e.preventDefault();

    if (isUserLoggedIn) {
        // Estamos listos. Verificar por última vez si hay algo pendiente
        const itemsLocal = JSON.parse(localStorage.getItem(CARRITO_STORAGE_KEY) || '[]');
        if (itemsLocal.length > 0) {
            // Intento final de sync
            await sincronizarCarritoConBackend(itemsLocal);
            localStorage.removeItem(CARRITO_STORAGE_KEY);
        }
        
        // Ir a pagar
        window.location.href = '/checkout/datos'; 
        return;
    }

    // FLUJO NO LOGUEADO
    alert("Para procesar su pedido, necesita iniciar sesión.");
    window.location.href = '/usuario/login?redirect=/usuario/carrito';
}

// ✅ SINCRONIZACIÓN SEGURA
async function sincronizarCarritoConBackend(items) {
    // Mapeo seguro de datos
    const payload = items.map(item => ({
        id: String(item.id),
        nombre: item.nombre,
        cantidad: parseInt(item.cantidad),
        precio: parseFloat(item.precio),
        unidad: item.unidad,
        imagen: item.imagen
    }));

    try {
        const response = await fetch('/checkout/iniciar-transferencia', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        return response.ok;
    } catch (error) {
        console.error("Error sync:", error);
        return false;
    }
}

// Globales
window.eliminarItem = eliminarItem;
window.actualizarCantidad = actualizarCantidad;