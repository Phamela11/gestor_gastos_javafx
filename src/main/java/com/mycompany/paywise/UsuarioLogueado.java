package com.mycompany.paywise;

/**
 * Clase para almacenar la información del usuario logueado
 * Implementa el patrón Singleton para acceso global
 */
public class UsuarioLogueado {
    
    private static UsuarioLogueado instancia;
    
    private int idUsuario;
    private String nombreUsuario;
    private String email;
    private String nombreCompleto;
    
    // Constructor privado para el patrón Singleton
    private UsuarioLogueado() {
        // Constructor vacío
    }
    
    /**
     * Obtiene la instancia única de UsuarioLogueado
     * @return La instancia única
     */
    public static UsuarioLogueado getInstancia() {
        if (instancia == null) {
            instancia = new UsuarioLogueado();
        }
        return instancia;
    }
    
    /**
     * Establece la información del usuario logueado
     * @param idUsuario ID del usuario
     * @param nombreUsuario Nombre de usuario
     * @param email Email del usuario
     * @param nombreCompleto Nombre completo del usuario
     */
    public void establecerUsuario(int idUsuario, String nombreUsuario, String email, String nombreCompleto) {
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.email = email;
        this.nombreCompleto = nombreCompleto;
    }
    
    /**
     * Limpia la información del usuario (para logout)
     */
    public void limpiarUsuario() {
        this.idUsuario = 0;
        this.nombreUsuario = null;
        this.email = null;
        this.nombreCompleto = null;
    }
    
    // Getters
    public int getIdUsuario() {
        return idUsuario;
    }
    
    public String getNombreUsuario() {
        return nombreUsuario;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getNombreCompleto() {
        return nombreCompleto;
    }
    
    /**
     * Verifica si hay un usuario logueado
     * @return true si hay un usuario logueado, false en caso contrario
     */
    public boolean hayUsuarioLogueado() {
        return idUsuario > 0 && nombreUsuario != null;
    }
}

