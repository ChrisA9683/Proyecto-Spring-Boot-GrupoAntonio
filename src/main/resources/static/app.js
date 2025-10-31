// Cargar todos los usuarios al inicio
function cargarUsuarios() {
    fetch('http://localhost:8080/usuario')
    .then(res => res.json())
    .then(data => {
        const tbody = document.querySelector('#tablaUsuarios tbody');
        tbody.innerHTML = '';
        data.forEach(u => {
            const tr = document.createElement('tr');
            tr.innerHTML = `<td>${u.id}</td><td>${u.nombre}</td><td>${u.email}</td><td>${u.rol}</td>`;
            tbody.appendChild(tr);
        });
    });
}

// Insertar nuevo usuario
function insertarUsuario() {
    const usuario = {
        nombre: document.getElementById('nombre').value,
        email: document.getElementById('email').value,
        password: document.getElementById('password').value,
        rol: document.getElementById('rol').value
    };
    fetch('http://localhost:8080/usuario', {
        method: 'POST',
        headers: {'Content-Type':'application/json'},
        body: JSON.stringify(usuario)
    })
    .then(res => res.json())
    .then(() => cargarUsuarios());
}

// Guardar cambios (actualizar)
function guardarUsuario() {
    const id = document.getElementById('usuarioId').value;
    if(!id) { alert('Selecciona un usuario para actualizar'); return; }
    const usuario = {
        nombre: document.getElementById('nombre').value,
        email: document.getElementById('email').value,
        password: document.getElementById('password').value,
        rol: document.getElementById('rol').value
    };
    fetch(`http://localhost:8080/usuario/${id}`, {
        method: 'PUT',
        headers: {'Content-Type':'application/json'},
        body: JSON.stringify(usuario)
    })
    .then(res => res.json())
    .then(() => cargarUsuarios());
}

// Buscar por email
function buscarUsuario() {
    const email = document.getElementById('buscarEmail').value;
    fetch(`http://localhost:8080/usuario/buscar?email=${email}`)
    .then(res => {
        if(res.status===404) { alert('Usuario no encontrado'); return; }
        return res.json();
    })
    .then(u => {
        if(u){
            document.getElementById('usuarioId').value = u.id;
            document.getElementById('nombre').value = u.nombre;
            document.getElementById('email').value = u.email;
            document.getElementById('password').value = u.password;
            document.getElementById('rol').value = u.rol;
        }
    });
}

// Asignar botones
document.getElementById('btnInsertar').onclick = insertarUsuario;
document.getElementById('btnGuardar').onclick = guardarUsuario;
document.getElementById('btnBuscar').onclick = buscarUsuario;

// Inicializar
cargarUsuarios();
