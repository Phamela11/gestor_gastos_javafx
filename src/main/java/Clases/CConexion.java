/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Clases;

import java.sql.Connection;
import java.sql.DriverManager;

import javafx.scene.control.Alert;

/**
 *
 * @author Phamela
 */
public class CConexion {
    
    Connection conectar = null;

    String usuario = "root";
    String contraseña = "root";
    String db = "gestordegasto";
    String ip = "localhost";
    String puerto = "3306";

    String url = "jdbc:mysql://"+ip+":"+puerto+"/"+db;

    public Connection EstablecerConexion(){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            conectar = DriverManager.getConnection(url, usuario, contraseña);
            //Alerta("Conexión exitosa", "Conexión exitosa",Alert.AlertType.INFORMATION);
        }catch(Exception e){
            Alerta("Error de conexión", "Error de conexión: " + e.getMessage(),Alert.AlertType.ERROR);
        }
        return conectar;
    }

    private void Alerta(String titulo, String mensaje,Alert.AlertType tipo){
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.setHeaderText(null);
        alert.showAndWait();
    }


    public void CerrarConexion(){
        try{
            if(conectar != null && !conectar.isClosed()){
                conectar.close();
                Alerta("Conexión cerrada", "Conexión cerrada",Alert.AlertType.INFORMATION);
            }
        }catch(Exception e){
            Alerta("Error al cerrar la conexión", "Error al cerrar la conexión: " + e.getMessage(),Alert.AlertType.ERROR);
        }
    }

}
