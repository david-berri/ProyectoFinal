package com.mycompany.proyectofinal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

@Route("")
public class indexview extends VerticalLayout {

    public indexview() {
        // Establecer la imagen de fondo con opacidad
        getElement().getStyle().set("background-image", "url('https://wallpapers.com/images/hd/medical-background-cjge7e89adg6ub8x.jpg')");
        getElement().getStyle().set("background-size", "cover");
        getElement().getStyle().set("background-repeat", "no-repeat");
        getElement().getStyle().set("opacity", "0.7"); // Establecer opacidad al 70%
        
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setSizeFull(); // Asegurarse de que el layout ocupe toda la pantalla

        // Agregar el logo desde una URL
        Image logo = new Image("https://pbs.twimg.com/media/GcN19FTXYAA0Kkz?format=jpg&name=4096x4096", "Logo");
        logo.setWidth("400px"); // Ajustar el tamaño del logo a 400px

        // Crear componentes de inicio de sesión
        H1 titulo = new H1("Login");
        
        // Crear campos de texto con estilo
        TextField usernameField = new TextField("Usuario");
        usernameField.getElement().getStyle().set("background-color", "rgba(255, 255, 255, 0.8)"); // Fondo blanco semi-transparente
        usernameField.getElement().getStyle().set("border-radius", "5px"); // Esquinas redondeadas
        usernameField.getElement().getStyle().set("padding", "10px"); // Espaciado interno

        PasswordField passwordField = new PasswordField("Contraseña");
        passwordField.getElement().getStyle().set("background-color", "rgba(255, 255, 255, 0.8)"); // Fondo blanco semi-transparente
        passwordField.getElement().getStyle().set("border-radius", "5px"); // Esquinas redondeadas
        passwordField.getElement().getStyle().set("padding", "10px"); // Espaciado interno

        Button loginButton = new Button("Iniciar Sesión");
        loginButton.getElement().getStyle().set("background-color", "rgba(0, 123, 255, 0.8)"); // Botón con fondo azul semi-transparente
        loginButton.getElement().getStyle().set("color", "white"); // Color de texto blanco
        loginButton.getElement().getStyle().set("border-radius", "5px"); // Esquinas redondeadas
        loginButton.getElement().getStyle().set("padding", "10px 20px"); // Espaciado interno

        Div messageDiv = new Div();

        // Manejo del clic del botón
        loginButton.addClickListener(event -> {
            String username = usernameField.getValue();
            String password = passwordField.getValue();
            String userType = validateUser (username, password);
            if (userType != null) {
                VaadinSession.getCurrent().setAttribute("username", username); // Almacenar el nombre de usuario en la sesión
                messageDiv.setText("Bienvenido " + userType);
                // Redirigir a la vista correspondiente
                if (userType.equals("administrativo")) {
                    getUI().ifPresent(ui -> ui.navigate("admin")); // Redirigir a la vista de administrador
                } else if (userType.equals("médico") || userType.equals("enfermera")) {
                    getUI().ifPresent(ui -> ui.navigate("medical")); // Redirigir a la vista del personal médico
                }
            } else {
                messageDiv.setText("Usuario o contraseña incorrectos.");
            }
        });

        // Agregar componentes al layout
        add(logo, titulo, usernameField, passwordField, loginButton, messageDiv);
    }

    private String validateUser (String username, String password) {
        // Lógica para validar el usuario contra el archivo usuarios.txt
        // Retornar el tipo de usuario si es válido, o null si no lo es.
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/usuarios.txt")))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String user = parts[0];
                    String pass = parts[1];
                    String userType = parts[2];
                    if (user.equals(username) && pass.equals(password)) {
                        return userType; // Retornar el tipo de usuario
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null; // Usuario no encontrado
    }
}
