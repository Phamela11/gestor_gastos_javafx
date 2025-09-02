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

public class ModalEditarCategoriaController implements Initializable {
    
    @FXML
    private TextField txtNombreCategoria;
    
    @FXML
    private ComboBox<String> cmbTipoCategoria;
    
    @FXML
    private Button btnGuardar;
    
    @FXML
    private Button btnCancelar;
    
    private VistaCategoriaController controladorPrincipal;
    private int idCategoria;
    private String nombreOriginal;
    private String tipoOriginal;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicializar el ComboBox con las opciones de tipo
        cmbTipoCategoria.getItems().addAll("ingreso", "egreso");
    }
    
    public void setControladorPrincipal(VistaCategoriaController controlador) {
        this.controladorPrincipal = controlador;
    }
    
    public void setDatosCategoria(int idCategoria, String nombre, String tipo) {
        this.idCategoria = idCategoria;
        this.nombreOriginal = nombre;
        this.tipoOriginal = tipo;
        
        // Cargar los datos en los campos
        txtNombreCategoria.setText(nombre);
        cmbTipoCategoria.setValue(tipo);
    }
    
    @FXML
    private void guardarCambios() {
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
        
        // Verificar si hay cambios
        if (nombreCategoria.equals(nombreOriginal) && tipoCategoria.equals(tipoOriginal)) {
            mostrarAlerta("Información", "No se han realizado cambios", Alert.AlertType.INFORMATION);
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
                // Preparar la consulta SQL para actualizar la categoría
                String sql = "UPDATE categoria SET nombre = ?, tipo = ? WHERE id_categoria = ? AND id_usuario = ?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, nombreCategoria);
                ps.setString(2, tipoCategoria);
                ps.setInt(3, idCategoria);
                ps.setInt(4, usuarioLogueado.getIdUsuario());
                
                // Ejecutar la consulta
                int filasAfectadas = ps.executeUpdate();
                
                if (filasAfectadas > 0) {
                    mostrarAlerta("Éxito", "Categoría actualizada correctamente", Alert.AlertType.INFORMATION);
                    
                    // Notificar al controlador principal para actualizar la vista
                    if (controladorPrincipal != null) {
                        controladorPrincipal.actualizarListaCategorias();
                        System.out.println("Categoría actualizada exitosamente: " + nombreCategoria + " (" + tipoCategoria + ")");
                    }
                    
                    // Cerrar el modal
                    cerrarModal();
                } else {
                    mostrarAlerta("Error", "No se pudo actualizar la categoría", Alert.AlertType.ERROR);
                }
                
                ps.close();
                con.close();
            } else {
                mostrarAlerta("Error", "No se pudo conectar a la base de datos", Alert.AlertType.ERROR);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Error al actualizar la categoría: " + e.getMessage(), Alert.AlertType.ERROR);
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
