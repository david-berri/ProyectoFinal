package com.mycompany.proyectofinal;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

@Route("medical")
public class MedicalStaffView extends VerticalLayout {

    public MedicalStaffView() {
        // Crear un layout para el encabezado
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setJustifyContentMode(JustifyContentMode.END); // Alinear a la derecha

        // Obtener el nombre de usuario de la sesión
        String username = (String) VaadinSession.getCurrent().getAttribute("username");
        Div userDiv = new Div();
        userDiv.setText("Usuario: " + (username != null ? username : "Invitado")); // Mostrar el nombre de usuario

        // Agregar el nombre de usuario al encabezado
        header.add(userDiv);

        // Agregar el encabezado a la vista
        add(header);

        // Título de la vista
        H1 title = new H1("Bienvenido, Personal Médico");
        add(title);
    }
}