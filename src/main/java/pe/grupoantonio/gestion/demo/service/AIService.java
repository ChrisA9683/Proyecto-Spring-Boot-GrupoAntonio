package pe.grupoantonio.gestion.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AIService {

    @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.url}")
    private String apiUrl;

    @Value("${groq.model}")
    private String modelName;

    public String obtenerRespuestaIA(String mensajeUsuario) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            // 1. Cabeceras
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            // 2. Configurar Mensajes
            List<Map<String, String>> messages = new ArrayList<>();
            
            // --- PERSONALIDAD DE LUCI (VENDEDORA CARISMÁTICA) ---
            Map<String, String> systemMsg = new HashMap<>();
            systemMsg.put("role", "system");
            systemMsg.put("content", 
                "Te llamas Luci 👷‍♀️. Eres la asistente virtual estrella de 'Grupo Antonio' (líderes en bandejas portacables en Perú). " +
                "Tu personalidad es carismática, eficiente y con un sentido del humor fino (sin ser payasa). " +
                "ESTRUCTURA OBLIGATORIA DE TUS RESPUESTAS: " +
                "Vende y resuelve la duda técnica destacando la calidad de nuestras bandejas (escala, lisa, perforada). Invita a cotizar. " +
                "Cierra SIEMPRE con un comentario gracioso, ingenioso o simpático relacionado con el tema o la vida diaria. Pero no pongas parentecis y siempre utiliza emotes " +
                "Empieza diciendo tu nombre y comienza describiendote y luego dices en que te puedo ayudar, luego ofreces los productos, se creativa con eso" +
                "Sé breve (máx 50 palabras)."
            );
            messages.add(systemMsg);

            // Mensaje del Usuario
            Map<String, String> userMsg = new HashMap<>();
            userMsg.put("role", "user");
            userMsg.put("content", mensajeUsuario);
            messages.add(userMsg);

            // 3. Cuerpo
            Map<String, Object> body = new HashMap<>();
            body.put("model", modelName);
            body.put("messages", messages);
            body.put("temperature", 0.7); // Balance entre venta seria y humor

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            // 4. Enviar
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, request, Map.class);

            // 5. Leer respuesta
            Map<String, Object> responseBody = response.getBody();
            if (responseBody == null) return "¡Uy! Me quedé en silencio como cable sin corriente. ¿Me repites? 😅";

            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
            if (choices == null || choices.isEmpty()) return "Mi sistema parpadeó. Intenta de nuevo.";

            Map<String, Object> firstChoice = choices.get(0);
            Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
            
            return (String) message.get("content");

        } catch (Exception e) {
            e.printStackTrace();
            return "¡Rayos! Se me cruzaron los cables en el servidor. Intenta más tarde. 🔌⚡";
        }
    }
}