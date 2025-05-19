module uniquindio.com.academix {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop; // para java.awt.Desktop

    opens uniquindio.com.academix to javafx.fxml;
    opens uniquindio.com.academix.Controller to javafx.fxml;

    // Añadido para permitir acceso reflexivo a las clases Modelo (para XMLEncoder/XMLDecoder)
    opens uniquindio.com.academix.Model;

    exports uniquindio.com.academix;
    exports uniquindio.com.academix.Controller;
}
