module uniquindio.com.academix {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop; // ✅ Añadir esto para permitir java.awt.Desktop

    opens uniquindio.com.academix to javafx.fxml;
    opens uniquindio.com.academix.Controller to javafx.fxml;

    exports uniquindio.com.academix;
    exports uniquindio.com.academix.Controller;
}
