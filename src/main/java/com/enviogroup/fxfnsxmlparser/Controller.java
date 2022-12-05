package com.enviogroup.fxfnsxmlparser;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField addressInput;

    @FXML
    private TextField countryCodeInput;

    @FXML
    private MenuItem exit;

    @FXML
    private TextField innInput;

    @FXML
    private TextField kppInput;

    @FXML
    private TextField nameOrgInput;

    @FXML
    private MenuItem openXml;

    @FXML
    private MenuItem saveAsXml;

    @FXML
    private MenuItem saveXml;

    @FXML
    private ChoiceBox<Integer> listBox;
    @FXML
    private GridPane documentTable;

    private static XmlParser xmlParser;
    private static final ObservableList<Integer> osnPerAmount = FXCollections.observableArrayList(0, 1, 2, 3);
    @FXML
    void initialize() {
        final FileChooser fileChooser = new FileChooser();
        openXml.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                configureFileChooser(fileChooser);
                File file = fileChooser.showOpenDialog(new Stage());
                if (file != null) {
                    try {
                        xmlParser = setXmlParser(file);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        saveXml.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                xmlParser.setOrgName(nameOrgInput.getText());
                xmlParser.setInn(innInput.getText());
                xmlParser.setKpp(kppInput.getText());
                xmlParser.setCountryCode(countryCodeInput.getText());
                xmlParser.setAddress(addressInput.getText());
                try {
                    xmlParser.saveChanges();
                } catch (XPathExpressionException | TransformerException ex) {
                    throw new RuntimeException(ex);
                }
                clear();
            }
        });
        listBox.setItems(osnPerAmount);
        listBox.setValue(0);
        listBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                //documentTable.setVisible(false);
                if (listBox.getValue() > 0) {
                    deleteRows();
                    createRows(listBox.getValue());
                    documentTable.setVisible(true);
                }
            }
        });
    }

    private void createRows(int rowsAmount) {
        for (int i = 1; i <= rowsAmount; i++) {
            documentTable.addRow(i, new TextField(), new TextField(), new TextField());
        }
    }

    private void deleteRows() {
            documentTable.getChildren().removeIf(node -> GridPane.getRowIndex(node) == 1);
//        while(documentTable.getRowConstraints().size() > 1){
//            documentTable.getRowConstraints().remove(1);
//        }
    }

    private XmlParser setXmlParser(File file) throws Exception {
        XmlParser xmlParser = new XmlParser(file);
        nameOrgInput.setText(xmlParser.getOrgName());
        innInput.setText(xmlParser.getInn());
        kppInput.setText(xmlParser.getKpp());
        countryCodeInput.setText(xmlParser.getCountryCode());
        addressInput.setText(xmlParser.getAddress());
        listBox.setValue(xmlParser.getAgreements().size());
        return xmlParser;
    }

    private static void configureFileChooser(final FileChooser fileChooser) {
        fileChooser.setTitle("Открыть...");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Downloads/"));
    }

    public void clear() {
        nameOrgInput.setText("");
        innInput.setText("");
        kppInput.setText("");
        countryCodeInput.setText("");
        addressInput.setText("");
    }

    public TextField getAddressInput() {
        return addressInput;
    }

    public void setAddressInput(TextField addressInput) {
        this.addressInput = addressInput;
    }

    public TextField getCountryCodeInput() {
        return countryCodeInput;
    }

    public void setCountryCodeInput(TextField countryCodeInput) {
        this.countryCodeInput = countryCodeInput;
    }

    public TextField getInnInput() {
        return innInput;
    }

    public void setInnInput(TextField innInput) {
        this.innInput = innInput;
    }

    public TextField getKppInput() {
        return kppInput;
    }

    public void setKppInput(TextField kppInput) {
        this.kppInput = kppInput;
    }

    public TextField getNameOrgInput() {
        return nameOrgInput;
    }

    public void setNameOrgInput(TextField nameOrgInput) {
        this.nameOrgInput = nameOrgInput;
    }

    public MenuItem getExit() {
        return exit;
    }

    public MenuItem getOpenXml() {
        return openXml;
    }

    public MenuItem getSaveAsXml() {
        return saveAsXml;
    }

    public MenuItem getSaveXml() {
        return saveXml;
    }

}
