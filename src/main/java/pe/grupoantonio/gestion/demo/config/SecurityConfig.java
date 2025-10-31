package pe.grupoantonio.gestion.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 🔒 Rutas públicas y protegidas
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/css/**", "/js/**", "/images/**").permitAll() // públicas
                .requestMatchers("/dashboard/**").authenticated() // protegidas
                .anyRequest().permitAll()
            )
            // 🔑 Configuración del login
            .formLogin(form -> form
            .loginPage("/usuario/login")              // tu vista de login
            .loginProcessingUrl("/usuario/login")     // aquí procesa el formulario
            .usernameParameter("email")               // le dices que el campo del formulario es 'email'
            .passwordParameter("password")            // nombre del campo de contraseña
            .defaultSuccessUrl("/usuario/dashboard", true)    // redirige al dashboard si es correcto
            .failureUrl("/usuario/login?error=true")  // si falla, vuelve al login con error
            .permitAll()
        )
            // 🔓 Logout funcional
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .permitAll()
            )
            // ⚙️ Seguridad adicional
             .csrf(csrf -> csrf.disable());

        return http.build();
    }

    // 🔐 Encriptación de contraseñas
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ⚙️ Gestor de autenticación
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}