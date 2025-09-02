package com.mycompany.paywise;

import Clases.CConexion;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ModalAgregarCategoriaController implements Initializable {
    
    @FXML
    private TextField txtNombreCategoria;
    
    @FXML
    private ComboBox<String> cmbTipoCategoria;
    
    @FXML
    private Button btnAgregar;
    
    @FXML
    private Button btnCancelar;
    
    private VistaCategoriaController controladorPrincipal;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicializar el ComboBox con las opciones de tipo
        cmbTipoCategoria.getItems().addAll("ingreso", "egreso");
    }
    
    public void setControladorPrincipal(VistaCategoriaController controlador) {
        this.controladorPrincipal = controlador;
    }
    
    @FXML
    private void agregarCategoria() {
        String nombreCategoria = txtNombreCategoria.getText().trim();
        String tipoCategoria = cmbTipoCategoria.getValue();
        
        // Validar campos vacíos
        if (nombreCategoria.isEmpty()) {
            mostrarAlerta("Error", "Por favor ingrese un nombre para la categoría", Alert.AlertType.ERROR);
            return;
        }
        
        if (tipoCategoria == null || tipoCategoria.isEmpty()) {
            mostrarAlerta("Error", "Por favor seleccione un tipo de categoría", Alert.AlertType.ERROR);
            return;
        }
        
        // Obtener el usuario logueado
        UsuarioLogueado usuarioLogueado = UsuarioLogueado.getInstancia();
        if (!usuarioLogueado.hayUsuarioLogueado()) {
            mostrarAlerta("Error", "No hay usuario logueado", Alert.AlertType.ERROR);
            return;
        }
        
        try {
            // Conectar a la base de datos
            CConexion conexion = new CConexion();
            Connection con = conexion.EstablecerConexion();
            
            if (con != null) {
                // Preparar la consulta SQL para insertar la categoría con el campo tipo
                String sql = "INSERT INTO categoria (id_usuario, nombre, tipo) VALUES (?, ?, ?)";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, usuarioLogueado.getIdUsuario());
                ps.setString(2, nombreCategoria);
                ps.setString(3, tipoCategoria);
                
                // Ejecutar la consulta
                int filasAfectadas = ps.executeUpdate();
                
                if (filasAfectadas > 0) {
                    mostrarAlerta("Éxito", "Categoría '" + nombreCategoria + "' de tipo '" + tipoCategoria + "' agregada correctamente", Alert.AlertType.INFORMATION);
                    
                    // Notificar al controlador principal para actualizar la vista
                    if (controladorPrincipal != null) {
                        controladorPrincipal.actualizarListaCategorias();
                        System.out.println("Categoría agregada exitosamente: " + nombreCategoria + " (" + tipoCategoria + ")");
                    }
                    
                    // Cerrar el modal
                    cerrarModal();
                } else {
                    mostrarAlerta("Error", "No se pudo agregar la categoría", Alert.AlertType.ERROR);
                }
                
                ps.close();
                con.close();
            } else {
                mostrarAlerta("Error", "No se pudo conectar a la base de datos", Alert.AlertType.ERROR);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Error al agregar la categoría: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    private void cancelar() {
        cerrarModal();
    }
    
    private void cerrarModal() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
    
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
