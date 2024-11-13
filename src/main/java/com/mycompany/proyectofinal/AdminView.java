package com.mycompany.proyectofinal;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route("admin")
public class AdminView extends VerticalLayout {

    private Grid<Room> roomsGrid;
    private Grid<UserData> secondaryGrid;  // Tabla para mostrar datos de usuario

    public AdminView() {
        // Encabezado de bienvenida
        H2 bienvenida = new H2("Bienvenido Administrador");
        Paragraph nombreUsuario = new Paragraph("Nombre del usuario");

        // Layout para el encabezado en la parte superior izquierda
        VerticalLayout headerLayout = new VerticalLayout(bienvenida, nombreUsuario);
        headerLayout.setSpacing(false);
        headerLayout.setPadding(false);

        // ComboBox para selección de tipo de habitación (izquierda)
        ComboBox<String> tipoHabitacion = new ComboBox<>("Selecciona Tipo de Habitación");
        tipoHabitacion.setItems("Tipo A", "Tipo B");

        // Campo de búsqueda de documento y botón (derecha)
        TextField searchField = new TextField();
        searchField.setPlaceholder("Número de documento");
        searchField.setWidth("200px");

        Button searchButton = new Button("Buscar");
        searchButton.addClickListener(event -> {
            String documento = searchField.getValue();
            if (!documento.isEmpty()) {
                List<UserData> userDataList = searchFilesByPartialName(documento);
                if (!userDataList.isEmpty()) {
                    secondaryGrid.setItems(userDataList);  // Mostrar datos en la tabla secundaria
                } else {
                    Notification.show("No se encontraron archivos con coincidencias para: " + documento);
                }
            } else {
                Notification.show("Por favor, ingresa un número de documento.");
            }
        });

        // Layout de búsqueda de documento
        HorizontalLayout documentSearchLayout = new HorizontalLayout(searchField, searchButton);
        documentSearchLayout.setSpacing(true);

        // Primera tabla (roomsGrid) con su ComboBox en un VerticalLayout
        roomsGrid = new Grid<>(Room.class);
        roomsGrid.setWidth("100%");
        roomsGrid.setHeight("300px");
        roomsGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        roomsGrid.setColumnReorderingAllowed(true);

        // Configurar columnas para roomsGrid
        roomsGrid.setColumns("id", "status");
        roomsGrid.getColumnByKey("id").setHeader("Habitación").setWidth("50%").setFlexGrow(0);
        roomsGrid.getColumnByKey("status").setHeader("Estado").setWidth("50%").setFlexGrow(0);

        VerticalLayout leftLayout = new VerticalLayout(tipoHabitacion, roomsGrid);
        leftLayout.setSpacing(true);
        leftLayout.setWidth("50%");

        // Segunda tabla (secondaryGrid) para mostrar los datos de usuario
        secondaryGrid = new Grid<>(UserData.class);
        secondaryGrid.setWidth("100%");
        secondaryGrid.setHeight("300px");
        secondaryGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        secondaryGrid.setColumnReorderingAllowed(true);

        // Configurar columnas para secondaryGrid solo si aún no existen
        secondaryGrid.removeAllColumns();
        secondaryGrid.addColumn(UserData::getCedula).setHeader("Cédula").setWidth("25%");
        secondaryGrid.addColumn(UserData::getNombre).setHeader("Nombre").setWidth("25%");
        secondaryGrid.addColumn(UserData::getFechaNacimiento).setHeader("Fecha de Nacimiento").setWidth("25%");
        secondaryGrid.addColumn(UserData::getSexo).setHeader("Sexo").setWidth("25%");

        VerticalLayout rightLayout = new VerticalLayout(documentSearchLayout, secondaryGrid);
        rightLayout.setSpacing(true);
        rightLayout.setWidth("50%");

        // Layout principal con ambas tablas alineadas
        HorizontalLayout mainLayout = new HorizontalLayout(leftLayout, rightLayout);
        mainLayout.setWidthFull();
        mainLayout.setSpacing(true);

        // Agregar el encabezado y el layout principal a la vista
        add(headerLayout, mainLayout);

        // Formulario de entrada de datos de usuario en la parte inferior izquierda
        TextField cedulaField = new TextField("Cédula");
        TextField nombreField = new TextField("Nombre");
        TextField fechaNacimientoField = new TextField("Fecha de Nacimiento");
        ComboBox<String> sexoField = new ComboBox<>("Sexo");
        sexoField.setItems("Hombre", "Mujer");

        Button addUserButton = new Button("Agregar Usuario");
        addUserButton.addClickListener(event -> {
            String cedula = cedulaField.getValue();
            String nombre = nombreField.getValue();
            String fechaNacimiento = fechaNacimientoField.getValue();
            String sexo = sexoField.getValue();

            if (!cedula.isEmpty() && !nombre.isEmpty() && !fechaNacimiento.isEmpty() && sexo != null) {
                boolean success = saveUserDataToFile(cedula, nombre, fechaNacimiento, sexo);
                if (success) {
                    Notification.show("Usuario agregado con éxito");
                    cedulaField.clear();
                    nombreField.clear();
                    fechaNacimientoField.clear();
                    sexoField.clear();
                } else {
                    Notification.show("Error al agregar usuario");
                }
            } else {
                Notification.show("Por favor, completa todos los campos.");
            }
        });

        // Layout del formulario de entrada de usuario
        VerticalLayout userFormLayout = new VerticalLayout(cedulaField, nombreField, fechaNacimientoField, sexoField, addUserButton);
        userFormLayout.setSpacing(true);
        userFormLayout.setWidth("50%");

        // Agregar el formulario de usuario en la parte inferior izquierda junto a las tablas
        add(userFormLayout, mainLayout);

        // Evento para cargar habitaciones según el tipo seleccionado
        tipoHabitacion.addValueChangeListener(event -> {
            String tipoSeleccionado = event.getValue();
            if (tipoSeleccionado != null) {
                List<Room> rooms = loadRoomsFromFile(tipoSeleccionado);
                roomsGrid.setItems(rooms);
            }
        });
    }

    private List<UserData> searchFilesByPartialName(String partialName) {
        List<UserData> userDataList = new ArrayList<>();
        File resourceFolder = new File("src/main/resources");

        // Buscar archivos que contengan el texto parcial en el nombre
        File[] matchingFiles = resourceFolder.listFiles((dir, name) -> name.contains(partialName) && name.endsWith(".txt"));
        if (matchingFiles != null) {
            for (File file : matchingFiles) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line = reader.readLine();
                    if (line != null) {
                        String[] parts = line.split(",");
                        if (parts.length == 4) {
                            userDataList.add(new UserData(parts[0], parts[1], parts[2], parts[3]));
                        }
                    }
                } catch (IOException e) {
                    Notification.show("Error al leer el archivo: " + e.getMessage());
                }
            }
        }
        return userDataList;
    }

    private boolean saveUserDataToFile(String cedula, String nombre, String fechaNacimiento, String sexo) {
        File userFile = new File("src/main/resources/" + cedula + ".txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(userFile))) {
            writer.write(cedula + "," + nombre + "," + fechaNacimiento + "," + sexo);
            return true;
        } catch (IOException e) {
            Notification.show("Error al guardar usuario: " + e.getMessage());
            return false;
        }
    }

    private List<Room> loadRoomsFromFile(String roomType) {
        List<Room> rooms = new ArrayList<>();
        String filePath = roomType.equals("Tipo A") ? "habitacionesA.txt" : "habitacionesB.txt";

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/" + filePath)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    rooms.add(new Room(parts[0], parts[1], parts[2]));
                }
            }
        } catch (IOException e) {
            Notification.show("Error al cargar las habitaciones: " + e.getMessage());
        }
        return rooms;
    }

    public static class Room {
        private String id;
        private String type;
        private String status;

        public Room(String id, String type, String status) {
            this.id = id;
            this.type = type;
            this.status = status;
        }

        public String getId() {
            return id;
        }

        public String getType() {
            return type;
        }

        public String getStatus() {
            return status;
        }
    }

    public static class UserData {
        private String cedula;
        private String nombre;
        private String fechaNacimiento;
        private String sexo;

        public UserData(String cedula, String nombre, String fechaNacimiento, String sexo) {
            this.cedula = cedula;
            this.nombre = nombre;
            this.fechaNacimiento = fechaNacimiento;
            this.sexo = sexo;
        }

        public String getCedula() {
            return cedula;
        }

        public String getNombre() {
            return nombre;
        }

        public String getFechaNacimiento() {
            return fechaNacimiento;
        }

        public String getSexo() {
            return sexo;
        }
    }
}