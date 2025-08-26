/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.paywise;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


import Clases.CConexion;

public class VistaPrincipalController implements Initializable {

    @javafx.fxml.FXML
    private TextField txtUsuario;
    
    @javafx.fxml.FXML
    private PasswordField txtPassword;
    
    @javafx.fxml.FXML
    private Label lblMensaje;
    
    @javafx.fxml.FXML
    private Button btnLogin;
    
    @javafx.fxml.FXML
    private Button btnLimpiar;
    
    private CConexion conexion;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inicializar conexión
        conexion = new CConexion();
        
        // Configurar eventos de teclado
        configurarEventosTeclado();
        
        // Limpiar mensajes al inicio
        limpiarMensaje();
        
        // Probar conexión al inicializar
        probarConexion();
    }
    
    private void probarConexion() {
        try {
            Connection conn = conexion.EstablecerConexion();
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ Conexión exitosa a la base de datos");
                mostrarMensaje("Conectado a la base de datos", false);
            } else {
                System.out.println("❌ No se pudo establecer la conexión");
                mostrarMensaje("Error de conexión a la base de datos", true);
            }
        } catch (Exception e) {
            System.err.println("Error al probar conexión: " + e.getMessage());
            mostrarMensaje("Error de conexión: " + e.getMessage(), true);
        }
    }
    
    private void configurarEventosTeclado() {
        // Permitir login con Enter
        txtPassword.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER")) {
                handleLogin();
            }
        });
        
        // Permitir login con Enter en el campo usuario
        txtUsuario.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER")) {
                txtPassword.requestFocus();
            }
        });
    }
    
    @javafx.fxml.FXML
    private void handleLogin() {
        String usuario = txtUsuario.getText().trim();
        String password = txtPassword.getText().trim();
        
        // Validar campos vacíos
        if (usuario.isEmpty() || password.isEmpty()) {
            mostrarMensaje("Por favor, complete todos los campos", true);
            return;
        }
        
        // Intentar autenticar con la base de datos
        if (autenticarUsuario(usuario, password)) {
            mostrarMensaje("¡Bienvenido " + usuario + "!", false);
            abrirVentanaPrincipal();
        } else {
            mostrarMensaje("Usuario o contraseña incorrectos", true);
            txtPassword.clear();
            txtPassword.requestFocus();
        }
    }
    
    @javafx.fxml.FXML
    private void handleLimpiar() {
        txtUsuario.clear();
        txtPassword.clear();
        limpiarMensaje();
        txtUsuario.requestFocus();
    }
    
    @javafx.fxml.FXML
    private void abrirRegistro() {
        try {
            // Cargar la vista de registro
            FXMLLoader loader = new FXMLLoader(getClass().getResource("VistaRegistro.fxml"));
            Parent root = loader.load();
            
            // Obtener la escena actual
            Stage stage = (Stage) btnLogin.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            
        } catch (Exception e) {
            System.err.println("Error al abrir registro: " + e.getMessage());
            mostrarMensaje("Error al cambiar de vista", true);
        }
    }
    
    private boolean autenticarUsuario(String usuario, String password) {
        try {
            // Verificar que la conexión esté inicializada
            if (conexion == null) {
                System.err.println("❌ La conexión no está inicializada");
                mostrarMensaje("Error: Conexión no inicializada", true);
                return false;
            }
            
            Connection conn = conexion.EstablecerConexion();
            if (conn != null && !conn.isClosed()) {
                // Usar la estructura correcta de tu tabla
                String sql = "SELECT * FROM usuario WHERE nombre_usuario = ? AND contrasena = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, usuario);
                ps.setString(2, password);
                
                System.out.println("🔍 Ejecutando consulta para usuario: " + usuario);
                System.out.println("🔍 SQL: " + sql);
                
                ResultSet rs = ps.executeQuery();
                boolean autenticado = rs.next();
                
                rs.close();
                ps.close();
                
                System.out.println("🔍 Resultado de autenticación: " + autenticado);
                
                if (autenticado) {
                    System.out.println("✅ Usuario autenticado: " + usuario);
                } else {
                    System.out.println("❌ Autenticación fallida para: " + usuario);
                }
                
                return autenticado;
            } else {
                System.err.println("❌ No hay conexión activa a la base de datos");
                mostrarMensaje("Error: No se pudo conectar a la base de datos", true);
                return false;
            }
        } catch (Exception e) {
            System.err.println("❌ Error en autenticación: " + e.getMessage());
            e.printStackTrace(); // Imprimir stack trace completo para debugging
            mostrarMensaje("Error de conexión: " + e.getMessage(), true);
            return false;
        }
    }
    
    private void mostrarMensaje(String mensaje, boolean esError) {
        lblMensaje.setText(mensaje);
        if (esError) {
            lblMensaje.setTextFill(Color.RED);
        } else {
            lblMensaje.setTextFill(Color.GREEN);
        }
    }
    
    private void limpiarMensaje() {
        lblMensaje.setText("");
    }
    
    private void abrirVentanaPrincipal() {
        try {
            System.out.println("🔍 Iniciando apertura de ventana principal...");
            
            // Verificar que el recurso existe
            URL resourceUrl = getClass().getResource("VistaDashboard.fxml");
            
            if (resourceUrl == null) {
                System.err.println("❌ No se pudo encontrar VistaDashboard.fxml");
                mostrarMensaje("Error: No se encontró la vista principal", true);
                return;
            }
            System.out.println("✅ Recurso FXML encontrado: " + resourceUrl);
            
            // Cargar la vista principal de la aplicación
            System.out.println("🔍 Cargando FXML...");
            FXMLLoader loader = new FXMLLoader(resourceUrl);
            System.out.println("✅ FXMLLoader creado");
            
            Parent root = loader.load();
            System.out.println("✅ FXML cargado exitosamente");
            
            // Obtener la escena actual
            Stage stage = (Stage) btnLogin.getScene().getWindow();
            System.out.println("✅ Stage obtenido: " + stage);
            
            Scene scene = new Scene(root);
            System.out.println("✅ Scene creada");
            
            // Configurar pantalla completa
            stage.setFullScreen(true);
            System.out.println("✅ Pantalla completa configurada");
            
            stage.setScene(scene);
            System.out.println("✅ Scene asignada al stage");
            
            stage.show();
            System.out.println("✅ Stage mostrado exitosamente");
            
        } catch (Exception e) {
            System.err.println("❌ Error al abrir ventana principal: " + e.getMessage());
            System.err.println("❌ Tipo de error: " + e.getClass().getSimpleName());
            e.printStackTrace();
            mostrarMensaje("Error al abrir la aplicación principal: " + e.getMessage(), true);
        }
    }

}
