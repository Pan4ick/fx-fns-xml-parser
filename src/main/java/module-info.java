module com.enviogroup.fxfnsxmlparser {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires java.xml;

    opens com.enviogroup.fxfnsxmlparser to javafx.fxml;
    exports com.enviogroup.fxfnsxmlparser;
}