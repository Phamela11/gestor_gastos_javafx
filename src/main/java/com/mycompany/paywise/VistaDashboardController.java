/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.paywise;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.Initializable;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Phamela
 */
public class VistaDashboardController implements Initializable {

    @FXML
    private BorderPane bp;
    @FXML
    private Pane ap;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    

    @FXML
    private void Dashboard_click(MouseEvent event) {
        bp.setCenter(ap);
    }

    @FXML
    private void Categoria_click(MouseEvent event) {
        loadPage("categoria");
    }

    @FXML
    private void Transaccion_click(MouseEvent event) {
        loadPage("transaccion");
    }
 
    @FXML
    private void Reporte_click(MouseEvent event) {
        loadPage("reporte");
    }
    
    private void perfil_click(MouseEvent event){
        loadPage("vistaPerfil");
    }
    
    private void loadPage(String page){
        Parent root = null;
        
        try {
            root = FXMLLoader.load(getClass().getResource(page+".fxml"));
        } catch (IOException ex) {
            Logger.getLogger(VistaDashboardController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        bp.setCenter(root);
    }
}
