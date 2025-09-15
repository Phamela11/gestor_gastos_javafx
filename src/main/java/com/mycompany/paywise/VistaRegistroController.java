/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.paywise;

import java.net.URL;
import java.util.ResourceBundle;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.regex.Pattern;

import Clases.CConexion;

import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
/**
 * FXML Controller class
 *
 * @author Phamela
 */
public class VistaRegistroController implements Initializable {

    @javafx.fxml.FXML
    private TextField txtEmail;
    
    @javafx.fxml.FXML
    private TextField txtUsuario;
    
    @javafx.fxml.FXML
    private PasswordField txtPassword;
    
    @javafx.fxml.FXML
    private PasswordField txtConfirmarPassword;
    
    @javafx.fxml.FXML
    private Label lblMensaje;
    
    @javafx.fxml.FXML
    private Button btnRegistrar;
    
    @javafx.fxml.FXML
    private Button btnLimpiar;
    
    private CConexion conexion;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        conexion = new CConexion();
        limpiarMensaje();
    }
    
    @javafx.fxml.FXML
    private void handleRegistrar() {
        if (validarCampos()) {
            if (registrarUsuario()) {
                mostrarMensaje("¡Cuenta creada exitosamente!", false);
                limpiarCampos();
                // Esperar un momento y volver al login
                new Thread(() -> {
                    try {
                        Thread.sleep(2000);
                        volverAlLogin();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }).start();
            }
        }
    }
    
    @javafx.fxml.FXML
    private void handleLimpiar() {
        limpiarCampos();
        limpiarMensaje();
    }
    
    @javafx.fxml.FXML
    private void volverAlLogin() {
        try {
            // Usar el método setRoot de App.java para mantener la misma Scene maximizada
            App.setRoot("VistaPrincipal");
            System.out.println("✅ Login cargado exitosamente manteniendo ventana maximizada");
            
        } catch (Exception e) {
            System.err.println("Error al volver al login: " + e.getMessage());
            mostrarMensaje("Error al cambiar de vista", true);
        }
    }
    
    private boolean validarCampos() {
        String email = txtEmail.getText().trim();
        String usuario = txtUsuario.getText().trim();
        String password = txtPassword.getText();
        String confirmarPassword = txtConfirmarPassword.getText();
        
        // Validar campos vacíos
        if (email.isEmpty() || usuario.isEmpty() || password.isEmpty() || confirmarPassword.isEmpty()) {
            mostrarMensaje("Por favor, complete todos los campos", true);
            return false;
        }
        
        // Validar formato de email
        if (!validarEmail(email)) {
            mostrarMensaje("Formato de email inválido", true);
            return false;
        }
        
        // Validar usuario (mínimo 4 caracteres)
        if (usuario.length() < 4) {
            mostrarMensaje("El usuario debe tener al menos 4 caracteres", true);
            return false;
        }
        
        // Validar contraseña (mínimo 6 caracteres)
        if (password.length() < 6) {
            mostrarMensaje("La contraseña debe tener al menos 6 caracteres", true);
            return false;
        }
        
        // Validar que las contraseñas coincidan
        if (!password.equals(confirmarPassword)) {
            mostrarMensaje("Las contraseñas no coinciden", true);
            txtConfirmarPassword.clear();
            txtConfirmarPassword.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private boolean validarEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }
    
    private boolean registrarUsuario() {
        try {
            Connection conn = conexion.EstablecerConexion();
            if (conn != null && !conn.isClosed()) {
                
                // Verificar si el usuario ya existe
                if (usuarioExiste(txtUsuario.getText().trim())) {
                    mostrarMensaje("El nombre de usuario ya existe", true);
                    return false;
                }
                
                // Verificar si el email ya existe
                if (emailExiste(txtEmail.getText().trim())) {
                    mostrarMensaje("El email ya está registrado", true);
                    return false;
                }
                
                // Insertar nuevo usuario usando la estructura de tu tabla
                String sql = "INSERT INTO usuario (nombre_usuario, contrasena, correo) VALUES (?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, txtUsuario.getText().trim());
                ps.setString(2, txtPassword.getText());
                ps.setString(3, txtEmail.getText().trim());
                
                int resultado = ps.executeUpdate();
                ps.close();
                
                if (resultado > 0) {
                    System.out.println("✅ Usuario registrado exitosamente: " + txtUsuario.getText().trim());
                    return true;
                } else {
                    mostrarMensaje("Error al registrar usuario", true);
                    return false;
                }
                
            } else {
                mostrarMensaje("Error de conexión a la base de datos", true);
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("Error en registro: " + e.getMessage());
            mostrarMensaje("Error de conexión: " + e.getMessage(), true);
            return false;
        }
    }
    
    private boolean usuarioExiste(String usuario) {
        try {
            Connection conn = conexion.EstablecerConexion();
            String sql = "SELECT COUNT(*) FROM usuario WHERE nombre_usuario = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, usuario);
            
            ResultSet rs = ps.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            
            rs.close();
            ps.close();
            
            return count > 0;
            
        } catch (Exception e) {
            System.err.println("Error al verificar usuario: " + e.getMessage());
            return false;
        }
    }
    
    private boolean emailExiste(String email) {
        try {
            Connection conn = conexion.EstablecerConexion();
            String sql = "SELECT COUNT(*) FROM usuario WHERE correo = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            
            ResultSet rs = ps.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            
            rs.close();
            ps.close();
            
            return count > 0;
            
        } catch (Exception e) {
            System.err.println("Error al verificar email: " + e.getMessage());
            return false;
        }
    }
    
    private void limpiarCampos() {
        txtEmail.clear();
        txtUsuario.clear();
        txtPassword.clear();
        txtConfirmarPassword.clear();
        txtUsuario.requestFocus();
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

    
}
