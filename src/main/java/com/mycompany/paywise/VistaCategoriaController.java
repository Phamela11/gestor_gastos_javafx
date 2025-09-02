package com.mycompany.paywise;

import Clases.CConexion;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class VistaCategoriaController implements Initializable {
    
    @FXML
    private Button btnAgregarCategoria;
    
    @FXML
    private ScrollPane scrollPaneIngresos;
    
    @FXML
    private ScrollPane scrollPaneGastos;
    
    @FXML
    private VBox vboxCategoriasIngresos;
    
    @FXML
    private VBox vboxCategoriasGastos;
    
    @FXML
    private Label lblTotalCategorias;
    
    @FXML
    private Label lblCategoriasIngresos;
    
    @FXML
    private Label lblCategoriasGastos;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicialización del controlador
        verificarUsuarioLogueado();
        cargarCategorias();
    }
    
    private void verificarUsuarioLogueado() {
        UsuarioLogueado usuarioLogueado = UsuarioLogueado.getInstancia();
        if (usuarioLogueado.hayUsuarioLogueado()) {
            System.out.println("✅ Usuario logueado: " + usuarioLogueado.getNombreUsuario());
            System.out.println("✅ ID Usuario: " + usuarioLogueado.getIdUsuario());
        } else {
            System.out.println("❌ No hay usuario logueado");
        }
    }
    
    @FXML
    private void abrirModalAgregarCategoria() {
        try {
            // Verificar que hay un usuario logueado
            UsuarioLogueado usuarioLogueado = UsuarioLogueado.getInstancia();
            if (!usuarioLogueado.hayUsuarioLogueado()) {
                System.err.println("❌ No hay usuario logueado");
                return;
            }
            
            // Cargar el FXML del modal
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/paywise/ModalAgregarCategoria.fxml"));
            Scene scene = new Scene(loader.load());
            
            // Obtener el controlador del modal
            ModalAgregarCategoriaController modalController = loader.getController();
            modalController.setControladorPrincipal(this);
            
            // Crear y configurar la ventana del modal
            Stage modalStage = new Stage();
            modalStage.setTitle("Agregar Categoría - " + usuarioLogueado.getNombreUsuario());
            modalStage.setScene(scene);
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setResizable(false);
            modalStage.centerOnScreen();
            
            // Mostrar el modal
            modalStage.showAndWait();
            
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al cargar el modal de agregar categoría: " + e.getMessage());
        }
    }
    
    private void cargarCategorias() {
        UsuarioLogueado usuarioLogueado = UsuarioLogueado.getInstancia();
        if (!usuarioLogueado.hayUsuarioLogueado()) {
            System.err.println("❌ No hay usuario logueado para cargar categorías");
            return;
        }
        
        try {
            // Conectar a la base de datos
            CConexion conexion = new CConexion();
            Connection con = conexion.EstablecerConexion();
            
            if (con != null) {
                // Consulta para obtener las categorías del usuario logueado
                String sql = "SELECT id_categoria, nombre, tipo FROM categoria WHERE id_usuario = ? ORDER BY tipo, nombre";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, usuarioLogueado.getIdUsuario());
                
                ResultSet rs = ps.executeQuery();
                
                // Limpiar las listas existentes
                vboxCategoriasIngresos.getChildren().clear();
                vboxCategoriasGastos.getChildren().clear();
                
                // Procesar cada categoría
                while (rs.next()) {
                    int idCategoria = rs.getInt("id_categoria");
                    String nombre = rs.getString("nombre");
                    String tipo = rs.getString("tipo");
                    
                    // Crear el elemento de categoría
                    Pane elementoCategoria = crearElementoCategoria(idCategoria, nombre, tipo);
                    
                    // Agregar al VBox correspondiente según el tipo
                    if ("ingreso".equals(tipo)) {
                        vboxCategoriasIngresos.getChildren().add(elementoCategoria);
                    } else if ("egreso".equals(tipo)) {
                        vboxCategoriasGastos.getChildren().add(elementoCategoria);
                    }
                }
                
                rs.close();
                ps.close();
                con.close();
                
                System.out.println("✅ Categorías cargadas exitosamente");
                
                // Actualizar el resumen después de cargar las categorías
                actualizarResumenCategorias();
                
            } else {
                System.err.println("❌ No se pudo conectar a la base de datos");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("❌ Error al cargar categorías: " + e.getMessage());
        }
    }
    
    private Pane crearElementoCategoria(int idCategoria, String nombre, String tipo) {
        Pane elemento = new Pane();
        elemento.setPrefHeight(54.0);
        elemento.setPrefWidth(292.0);
        
        // Estilos según el tipo
        String colorFondo, colorBorde, colorPunto;
        if ("ingreso".equals(tipo)) {
            colorFondo = "#e9f5db";
            colorBorde = "#56c969";
            colorPunto = "punto_verde.png";
        } else {
            colorFondo = "#ffe5d9";
            colorBorde = "#d53f3f";
            colorPunto = "punto_rojo.png";
        }
        
        elemento.setStyle("-fx-background-color: " + colorFondo + "; -fx-border-radius: 8; -fx-background-radius: 8; -fx-border-color: " + colorBorde + ";");
        
        // Label con el nombre de la categoría
        Label lblNombre = new Label(nombre);
        lblNombre.setLayoutX(33.0);
        lblNombre.setLayoutY(18.0);
        lblNombre.setStyle("-fx-font-size: 13px; -fx-text-fill: black;");
        
        // Imagen del punto de color
        ImageView imgPunto = new ImageView();
        imgPunto.setFitHeight(17.0);
        imgPunto.setFitWidth(10.0);
        imgPunto.setLayoutX(14.0);
        imgPunto.setLayoutY(23.0);
        imgPunto.setPreserveRatio(true);
        try {
            imgPunto.setImage(new Image(getClass().getResourceAsStream("/img/" + colorPunto)));
        } catch (Exception e) {
            System.err.println("Error al cargar imagen: " + colorPunto);
        }
        
        // Botón de editar
        ImageView imgEditar = new ImageView();
        imgEditar.setFitHeight(17.0);
        imgEditar.setFitWidth(21.0);
        imgEditar.setLayoutX(250.0);
        imgEditar.setLayoutY(19.0);
        imgEditar.setPreserveRatio(true);
        imgEditar.setStyle("-fx-cursor: hand;");
        try {
            imgEditar.setImage(new Image(getClass().getResourceAsStream("/img/editar.png")));
        } catch (Exception e) {
            System.err.println("Error al cargar imagen: editar.png");
        }
        
        // Agregar evento de clic para editar
        imgEditar.setOnMouseClicked(event -> editarCategoria(idCategoria, nombre, tipo));
        
        // Botón de eliminar
        ImageView imgEliminar = new ImageView();
        imgEliminar.setFitHeight(17.0);
        imgEliminar.setFitWidth(21.0);
        imgEliminar.setLayoutX(275.0);
        imgEliminar.setLayoutY(19.0);
        imgEliminar.setPreserveRatio(true);
        imgEliminar.setStyle("-fx-cursor: hand;");
        try {
            imgEliminar.setImage(new Image(getClass().getResourceAsStream("/img/bote-de-basura.png")));
        } catch (Exception e) {
            System.err.println("Error al cargar imagen: bote-de-basura.png");
        }
        
        // Agregar evento de clic para eliminar
        imgEliminar.setOnMouseClicked(event -> eliminarCategoria(idCategoria, nombre, tipo));
        
        // Agregar elementos al pane
        elemento.getChildren().addAll(lblNombre, imgPunto, imgEditar, imgEliminar);
        
        return elemento;
    }
    
    private void editarCategoria(int idCategoria, String nombre, String tipo) {
        try {
            // Verificar que hay un usuario logueado
            UsuarioLogueado usuarioLogueado = UsuarioLogueado.getInstancia();
            if (!usuarioLogueado.hayUsuarioLogueado()) {
                System.err.println("❌ No hay usuario logueado");
                return;
            }
            
            // Cargar el FXML del modal de editar
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/paywise/ModalEditarCategoria.fxml"));
            Scene scene = new Scene(loader.load());
            
            // Obtener el controlador del modal
            ModalEditarCategoriaController modalController = loader.getController();
            modalController.setControladorPrincipal(this);
            modalController.setDatosCategoria(idCategoria, nombre, tipo);
            
            // Crear y configurar la ventana del modal
            Stage modalStage = new Stage();
            modalStage.setTitle("Editar Categoría - " + usuarioLogueado.getNombreUsuario());
            modalStage.setScene(scene);
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setResizable(false);
            modalStage.centerOnScreen();
            
            // Mostrar el modal
            modalStage.showAndWait();
            
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al cargar el modal de editar categoría: " + e.getMessage());
        }
    }
    
    private void eliminarCategoria(int idCategoria, String nombre, String tipo) {
        try {
            // Verificar que hay un usuario logueado
            UsuarioLogueado usuarioLogueado = UsuarioLogueado.getInstancia();
            if (!usuarioLogueado.hayUsuarioLogueado()) {
                System.err.println("❌ No hay usuario logueado");
                return;
            }
            
            // Cargar el FXML del modal de confirmación
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/paywise/ModalConfirmarEliminacion.fxml"));
            Scene scene = new Scene(loader.load());
            
            // Obtener el controlador del modal
            ModalConfirmarEliminacionController modalController = loader.getController();
            modalController.setControladorPrincipal(this);
            modalController.setDatosCategoria(idCategoria, nombre, tipo);
            
            // Crear y configurar la ventana del modal
            Stage modalStage = new Stage();
            modalStage.setTitle("Confirmar Eliminación - " + usuarioLogueado.getNombreUsuario());
            modalStage.setScene(scene);
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setResizable(false);
            modalStage.centerOnScreen();
            
            // Mostrar el modal
            modalStage.showAndWait();
            
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al cargar el modal de confirmación de eliminación: " + e.getMessage());
        }
    }
    
    public void actualizarListaCategorias() {
        // Recargar las categorías desde la base de datos
        cargarCategorias();
        // Actualizar el resumen después de cargar las categorías
        actualizarResumenCategorias();
        System.out.println("✅ Lista de categorías actualizada");
    }
    
    /**
     * Función para contar las categorías de ingresos
     * @return número de categorías de ingresos
     */
    private int contarCategoriasIngresos() {
        UsuarioLogueado usuarioLogueado = UsuarioLogueado.getInstancia();
        if (!usuarioLogueado.hayUsuarioLogueado()) {
            return 0;
        }
        
        try {
            CConexion conexion = new CConexion();
            Connection con = conexion.EstablecerConexion();
            
            if (con != null) {
                String sql = "SELECT COUNT(*) as total FROM categoria WHERE id_usuario = ? AND tipo = 'ingreso'";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, usuarioLogueado.getIdUsuario());
                
                ResultSet rs = ps.executeQuery();
                int total = 0;
                if (rs.next()) {
                    total = rs.getInt("total");
                }
                
                rs.close();
                ps.close();
                con.close();
                
                return total;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("❌ Error al contar categorías de ingresos: " + e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * Función para contar las categorías de gastos
     * @return número de categorías de gastos
     */
    private int contarCategoriasGastos() {
        UsuarioLogueado usuarioLogueado = UsuarioLogueado.getInstancia();
        if (!usuarioLogueado.hayUsuarioLogueado()) {
            return 0;
        }
        
        try {
            CConexion conexion = new CConexion();
            Connection con = conexion.EstablecerConexion();
            
            if (con != null) {
                String sql = "SELECT COUNT(*) as total FROM categoria WHERE id_usuario = ? AND tipo = 'egreso'";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, usuarioLogueado.getIdUsuario());
                
                ResultSet rs = ps.executeQuery();
                int total = 0;
                if (rs.next()) {
                    total = rs.getInt("total");
                }
                
                rs.close();
                ps.close();
                con.close();
                
                return total;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("❌ Error al contar categorías de gastos: " + e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * Función para calcular el total de categorías
     * @return número total de categorías
     */
    private int calcularTotalCategorias() {
        int ingresos = contarCategoriasIngresos();
        int gastos = contarCategoriasGastos();
        return ingresos + gastos;
    }
    
    /**
     * Función para actualizar el resumen de categorías en la interfaz
     */
    private void actualizarResumenCategorias() {
        int totalIngresos = contarCategoriasIngresos();
        int totalGastos = contarCategoriasGastos();
        int totalCategorias = calcularTotalCategorias();
        
        // Actualizar los labels
        lblCategoriasIngresos.setText(String.valueOf(totalIngresos));
        lblCategoriasGastos.setText(String.valueOf(totalGastos));
        lblTotalCategorias.setText(String.valueOf(totalCategorias));
        
        System.out.println("✅ Resumen actualizado - Total: " + totalCategorias + 
                          ", Ingresos: " + totalIngresos + ", Gastos: " + totalGastos);
    }
}
