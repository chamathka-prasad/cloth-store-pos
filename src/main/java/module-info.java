module com.chamathka.clothstore {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens com.chamathka.clothstore to javafx.fxml;
    exports com.chamathka.clothstore;
}