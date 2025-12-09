document.addEventListener('DOMContentLoaded', function () {
    const chatbotToggle = document.getElementById('chatbot-toggle');
    const chatbotContainer = document.getElementById('chatbot-container');
    const closeChatbot = document.getElementById('close-chatbot');
    const chatbotMessages = document.getElementById('chatbot-messages');
    const userInput = document.getElementById('chatbot-user-input');
    const sendButton = document.getElementById('chatbot-send');

    // Estado inicial: Agregar mensaje de bienvenida de Luci
    function initialMessage() {
        if (chatbotMessages.children.length === 0) {
            addMessage("Luci 👷‍♀️: ¡Hola! Soy Luci, tu asistente virtual estrella de Grupo Antonio. Soy carismática y eficiente, experta en bandejas portacables (escala, lisa, perforada) y lista para ayudarte a cotizar o resolver tus dudas. ¿En qué puedo ayudarte hoy? ✨", false);
        }
    }

    // Función para añadir mensajes al historial del chat
    function addMessage(message, isUser) {
        const messageDiv = document.createElement('div');
        messageDiv.classList.add('message');
        messageDiv.classList.add(isUser ? 'user-message' : 'bot-message');
        
        // Sanear el mensaje para evitar inyección de HTML
        messageDiv.textContent = message; 

        chatbotMessages.appendChild(messageDiv);
        chatbotMessages.scrollTop = chatbotMessages.scrollHeight; // Scroll al final
    }

    // Función principal para enviar y recibir mensajes AJAX
    async function sendMessage() {
        const message = userInput.value.trim();
        if (message === '') return;

        // 1. Mostrar mensaje del usuario
        addMessage(`Tú: ${message}`, true);
        userInput.value = '';
        
        // 2. Deshabilitar input y mostrar indicador de escritura
        userInput.disabled = true;
        sendButton.disabled = true;
        addMessage("Luci está escribiendo...", false);
        
        // 3. Petición AJAX a tu endpoint de Spring Boot
        try {
            const response = await fetch('/api/chat/responder', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                // El body envía el mensaje del usuario al controlador
                body: JSON.stringify({ mensajeUsuario: message })
            });

            // 4. Procesar respuesta
            if (response.ok) {
                const data = await response.json();
                
                // Remover el indicador de escritura
                chatbotMessages.removeChild(chatbotMessages.lastChild); 
                
                // Mostrar la respuesta de Luci
                addMessage(data.respuesta, false);
            } else {
                chatbotMessages.removeChild(chatbotMessages.lastChild); 
                addMessage("¡Rayos! Se me cruzaron los cables. No pude conectar con el servidor. 😥", false);
            }
        } catch (error) {
            console.error('Error en la comunicación con el chat:', error);
            if (chatbotMessages.lastChild) { // Si todavía está el indicador de escritura
                chatbotMessages.removeChild(chatbotMessages.lastChild); 
            }
            addMessage("Error de red. Intenta más tarde. 🔌⚡", false);
        } finally {
            // 5. Reactivar input
            userInput.disabled = false;
            sendButton.disabled = false;
            userInput.focus();
            chatbotMessages.scrollTop = chatbotMessages.scrollHeight;
        }
    }

    // --- Event Listeners ---

    // Toggle (Abrir/Cerrar) el contenedor del chat
    chatbotToggle.addEventListener('click', () => {
        chatbotContainer.classList.toggle('chatbot-hidden');
        if (!chatbotContainer.classList.contains('chatbot-hidden')) {
            initialMessage();
            userInput.focus();
        }
    });

    // Cerrar el chat
    closeChatbot.addEventListener('click', () => {
        chatbotContainer.classList.add('chatbot-hidden');
    });

    // Enviar mensaje al hacer clic
    sendButton.addEventListener('click', sendMessage);

    // Enviar mensaje al presionar Enter
    userInput.addEventListener('keydown', (e) => {
        if (e.key === 'Enter') {
            e.preventDefault(); // Evita salto de línea
            sendMessage();
        }
    });
    
    // Cargar mensaje inicial al cargar la página
    initialMessage();
});