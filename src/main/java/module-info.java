module com.mycompany.paywise {
    requires java.sql;
    requires java.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires javafx.graphics;



    opens com.mycompany.paywise to javafx.fxml;
    exports com.mycompany.paywise;
}
