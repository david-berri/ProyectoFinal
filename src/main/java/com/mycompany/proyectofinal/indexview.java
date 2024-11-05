// IndexView

package com.mycompany.proyectofinal;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("")
public class indexview extends VerticalLayout{
    
    public indexview(){
    
    H1 titulo = new H1("Hola mundo");
    
    add(titulo);
    }
    
}
