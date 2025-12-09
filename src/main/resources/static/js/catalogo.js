document.addEventListener('DOMContentLoaded', () => {
    
    // ==========================================
    // 1. LÓGICA DEL CARRUSEL
    // ==========================================
    let slideActual = 0;
    const slides = document.querySelectorAll('.carrusel-item');
    const indicadores = document.querySelectorAll('.indicador');
    
    function mostrarSlide(n) {
        if(slides.length === 0) return;
        slides.forEach(slide => slide.classList.remove('activo'));
        indicadores.forEach(indicador => indicador.classList.remove('activo'));
        slideActual = (n + slides.length) % slides.length;
        slides[slideActual].classList.add('activo');
        indicadores[slideActual].classList.add('activo');
    }

    function siguienteSlide() { mostrarSlide(slideActual + 1); }
    if(slides.length > 0) setInterval(siguienteSlide, 4000);

    indicadores.forEach((indicador, index) => {
        indicador.addEventListener('click', () => mostrarSlide(index));
    });

    // ==========================================
    // 2. LÓGICA DEL MODAL Y PRODUCTOS
    // ==========================================
    const modal = document.getElementById('modal-producto');
    const cerrarModal = document.getElementById('cerrar-modal');
    const btnsAbrirModal = document.querySelectorAll('.btn-abrir-modal');
    
    const inputCantidad = document.getElementById('input-cantidad-modal');
    const btnMenos = document.getElementById('btn-menos-modal');
    const btnMas = document.getElementById('btn-mas-modal');
    const subtotalValor = document.getElementById('modal-subtotal-valor');
    const btnAgregar = document.getElementById('btn-agregar-carrito');
    
    const productoIdInput = document.getElementById('producto-id-seleccionado');
    const precioBaseInput = document.getElementById('producto-precio-base');
    const unidadMedida = document.getElementById('modal-unidad-medida');
    
    // CLAVE DE ALMACENAMIENTO (Debe ser igual en ambos archivos)
    const CARRITO_STORAGE_KEY = 'carritoGrupoAntonio'; 
    let precioBase = 0;

    // --- Función para guardar en LocalStorage (Modo Anónimo) ---
    const addToLocalCart = (id, nombre, cantidad, precio, unidad, imagen) => {
        let items = JSON.parse(localStorage.getItem(CARRITO_STORAGE_KEY) || '[]');
        
        // Convertimos ID a string para evitar errores de duplicados (1 vs "1")
        const index = items.findIndex(item => String(item.id) === String(id));

        if (index !== -1) {
            items[index].cantidad += cantidad;
        } else {
            items.push({ 
                id: id, 
                nombre: nombre, 
                cantidad: cantidad, 
                precio: precio, 
                unidad: unidad, 
                imagen: imagen 
            });
        }
        localStorage.setItem(CARRITO_STORAGE_KEY, JSON.stringify(items));
    };

    // --- Actualizar Subtotal Visual ---
    function actualizarSubtotal() {
        let cantidad = parseInt(inputCantidad.value);
        if (isNaN(cantidad) || cantidad < 1) {
            cantidad = 1;
            inputCantidad.value = 1;
        }
        const subtotal = cantidad * precioBase;
        subtotalValor.textContent = `S/ ${subtotal.toFixed(2)}`;
    }

    // --- Modal de Éxito Visual ---
    function showSuccessModal(message) {
        const successModal = document.getElementById('modal-exito');
        if(successModal) {
            document.getElementById('mensaje-exito').textContent = message;
            modal.style.display = 'none';
            successModal.style.display = 'flex';
            setTimeout(() => { successModal.style.display = 'none'; }, 2000);
        } else {
            alert(message); // Fallback
            modal.style.display = 'none';
        }
    }

    // --- Eventos de Apertura ---
    btnsAbrirModal.forEach(btn => {
        btn.addEventListener('click', (e) => {
            const productoItem = e.currentTarget.closest('.producto-item-general');
            
            // Datos del HTML
            const nombre = productoItem.dataset.nombre;
            const descripcion = productoItem.dataset.descripcion;
            const precio = productoItem.dataset.precio;
            const unidad = productoItem.dataset.unidad;
            const id = productoItem.dataset.id;
            const imagen = productoItem.dataset.imagen; 

            // Llenar Modal
            document.getElementById('modal-nombre-producto').textContent = nombre;
            document.getElementById('modal-descripcion-producto').textContent = descripcion;
            document.getElementById('modal-precio-unitario').textContent = `S/ ${parseFloat(precio).toFixed(2)} por ${unidad}`;
            document.getElementById('modal-unidad-medida').textContent = unidad;
            document.getElementById('modal-imagen-producto').src = imagen; 
            document.getElementById('modal-imagen-producto').dataset.srcUrl = imagen;

            precioBase = parseFloat(precio);
            productoIdInput.value = id;
            precioBaseInput.value = precio;
            
            inputCantidad.value = 1;
            actualizarSubtotal();
            
            modal.style.display = 'flex';
            modal.style.justifyContent = 'center';
            modal.style.alignItems = 'center';
        });
    });

    // --- Eventos de Cierre y Cantidad ---
    if(cerrarModal) cerrarModal.addEventListener('click', () => { modal.style.display = 'none'; });
    window.addEventListener('click', (e) => { if (e.target == modal) { modal.style.display = 'none'; } });

    if(btnMenos) btnMenos.addEventListener('click', () => {
        let cantidad = parseInt(inputCantidad.value);
        if (cantidad > 1) { inputCantidad.value = cantidad - 1; actualizarSubtotal(); }
    });

    if(btnMas) btnMas.addEventListener('click', () => {
        let cantidad = parseInt(inputCantidad.value);
        inputCantidad.value = cantidad + 1;
        actualizarSubtotal();
    });

    if(inputCantidad) inputCantidad.addEventListener('change', actualizarSubtotal);

    // =================================================================
    // ✅ LÓGICA DE AGREGAR (SIN INTERRUPCIONES)
    // =================================================================
    if(btnAgregar) {
        btnAgregar.addEventListener('click', async () => {
            const productoId = productoIdInput.value;
            const nombre = document.getElementById('modal-nombre-producto').textContent;
            const cantidad = parseInt(inputCantidad.value);
            const precio = parseFloat(precioBaseInput.value); 
            const unidad = unidadMedida.textContent; 
            const imagen = document.getElementById('modal-imagen-producto').dataset.srcUrl;

            btnAgregar.disabled = true;
            btnAgregar.textContent = 'Agregando...';

            const payload = { productoId: productoId, cantidad: cantidad };

            try {
                // 1. Intentar guardar en BD
                const response = await fetch('/api/carrito/agregar', { 
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
                });

                // 2. Si NO está logueado (401), guardar en local
                if (response.status === 401) {
                    addToLocalCart(productoId, nombre, cantidad, precio, unidad, imagen);
                    showSuccessModal(`¡${cantidad} ${nombre} agregado(s) temporalmente!`);
                    // NO REDIRIGIMOS. El usuario sigue comprando tranquilo.
                    return; 
                }

                // 3. Si está logueado (200 OK)
                if (response.ok) {
                    const data = await response.json();
                    if (data.success) {
                        showSuccessModal(`¡${cantidad} ${nombre} agregado(s) a tu cuenta!`);
                    } else {
                        alert('Error: ' + (data.error || 'No se pudo agregar.'));
                    }
                } else {
                    const errorText = await response.text(); 
                    console.error("Error servidor:", errorText);
                    alert("Error de conexión con el servidor.");
                }

            } catch (error) {
                // 4. Si hay error de red (Offline)
                console.error('Offline:', error);
                addToLocalCart(productoId, nombre, cantidad, precio, unidad, imagen);
                showSuccessModal(`¡${cantidad} ${nombre} agregado(s) (Offline)!`);
            } finally {
                btnAgregar.disabled = false;
                btnAgregar.textContent = 'Agregar al Carrito';
            }
        });
    }
});