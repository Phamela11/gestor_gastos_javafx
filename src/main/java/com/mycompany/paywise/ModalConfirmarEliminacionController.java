package com.mycompany.paywise;

import Clases.CConexion;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ModalConfirmarEliminacionController implements Initializable {
    
    @FXML
    private Label lblNombreCategoria;
    
    @FXML
    private Label lblTipoCategoria;
    
    @FXML
    private Button btnEliminar;
    
    @FXML
    private Button btnCancelar;
    
    private VistaCategoriaController controladorPrincipal;
    private int idCategoria;
    private String nombre;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicialización del controlador
    }
    
    public void setControladorPrincipal(VistaCategoriaController controlador) {
        this.controladorPrincipal = controlador;
    }
    
    public void setDatosCategoria(int idCategoria, String nombre, String tipo) {
        this.idCategoria = idCategoria;
        this.nombre = nombre;
        
        // Actualizar las etiquetas con los datos
        lblNombreCategoria.setText("📋 Categoría: " + nombre);
        lblTipoCategoria.setText("🏷️ Tipo: " + (tipo.equals("ingreso") ? "Ingreso" : "Gasto"));
    }
    
    @FXML
    private void confirmarEliminacion() {
        // Obtener el usuario logueado
        UsuarioLogueado usuarioLogueado = UsuarioLogueado.getInstancia();
        if (!usuarioLogueado.hayUsuarioLogueado()) {
            mostrarAlerta("❌ Error", "No hay usuario logueado", Alert.AlertType.ERROR);
            return;
        }
        
        try {
            // Conectar a la base de datos
            CConexion conexion = new CConexion();
            Connection con = conexion.EstablecerConexion();
            
            if (con != null) {
                // Preparar la consulta SQL para eliminar la categoría
                String sql = "DELETE FROM categoria WHERE id_categoria = ? AND id_usuario = ?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, idCategoria);
                ps.setInt(2, usuarioLogueado.getIdUsuario());
                
                // Ejecutar la consulta
                int filasAfectadas = ps.executeUpdate();
                
                if (filasAfectadas > 0) {
                    // Cerrar el modal primero
                    cerrarModal();
                    
                    // Mostrar mensaje de éxito
                    mostrarAlerta("✅ Eliminación Exitosa", 
                                "🗑️ La categoría '" + nombre + "' ha sido eliminada correctamente.\n\n" +
                                "📋 La lista de categorías se ha actualizado automáticamente.", 
                                Alert.AlertType.INFORMATION);
                    
                    // Actualizar la vista
                    if (controladorPrincipal != null) {
                        controladorPrincipal.actualizarListaCategorias();
                        System.out.println("✅ Categoría eliminada exitosamente: " + nombre);
                    }
                } else {
                    mostrarAlerta("❌ Error de Eliminación", 
                                "⚠️ No se pudo eliminar la categoría '" + nombre + "'.\n\n" +
                                "🔍 Verifique que la categoría existe y que tiene permisos para eliminarla.", 
                                Alert.AlertType.ERROR);
                }
                
                ps.close();
                con.close();
            } else {
                mostrarAlerta("❌ Error de Conexión", 
                            "🔌 No se pudo conectar a la base de datos.\n\n" +
                            "🔍 Verifique su conexión a internet y que el servidor esté funcionando.", 
                            Alert.AlertType.ERROR);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("❌ Error de Base de Datos", 
                        "💾 Error al eliminar la categoría '" + nombre + "'.\n\n" +
                        "🔍 Detalles: " + e.getMessage() + "\n\n" +
                        "📞 Contacte al administrador si el problema persiste.", 
                        Alert.AlertType.ERROR);
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
