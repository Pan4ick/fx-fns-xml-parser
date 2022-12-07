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
import java.util.*;

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
    private Map<Integer, List<TextField>> textFieldsInTable = new HashMap<>();
    private static final int ELEMENTS_IN_ROW_AMOUNT = 3;
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
                if (textFieldsInTable.size() > listBox.getValue()) {
                    deleteRows(textFieldsInTable.size(),textFieldsInTable.size() - listBox.getValue());
                } else {
                    createRows(documentTable.getRowCount(),listBox.getValue() - textFieldsInTable.size());
                }
                documentTable.setVisible(listBox.getValue() > 0);
            }
        });
    }

    private XmlParser setXmlParser(File file) throws Exception {
        XmlParser xmlParser = new XmlParser(file);
        nameOrgInput.setText(xmlParser.getOrgName());
        innInput.setText(xmlParser.getInn());
        kppInput.setText(xmlParser.getKpp());
        countryCodeInput.setText(xmlParser.getCountryCode());
        addressInput.setText(xmlParser.getAddress());
        listBox.setValue(xmlParser.getAgreements().size());
        addValuesToTable(xmlParser);
        return xmlParser;
    }

    private static void configureFileChooser(final FileChooser fileChooser) {
        fileChooser.setTitle("Открыть...");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Downloads/"));
    }

    private void addValuesToTable(XmlParser xmlParser) {
        for (int i = 0; i < textFieldsInTable.size(); i++) {
            textFieldsInTable.get(i + 1).get(0).setText(xmlParser.getAgreements().get(i).getDocumentName());
            textFieldsInTable.get(i + 1).get(1).setText(xmlParser.getAgreements().get(i).getDocumentNumber());
            textFieldsInTable.get(i + 1).get(2).setText(xmlParser.getAgreements().get(i).getDocumentDate());
        }
    }

    private void createRows(int startIndex, int rowsAmount) {
        for (int i = startIndex; i < startIndex + rowsAmount; i++) {
            List<TextField> textFields = new ArrayList<>();
            for (int j = 0; j < ELEMENTS_IN_ROW_AMOUNT; j++) {
                textFields.add(new TextField());
            }
            textFieldsInTable.put(i, textFields);
            documentTable.addRow(i, textFields.toArray(new TextField[textFields.size()]));
        }
    }

    private void deleteRows(int startIndex, int rowsAmount) {
        for (int i = startIndex; i > (startIndex - rowsAmount); i--) {
            GridPaneUtils.removeRow(documentTable, GridPane.getRowIndex((textFieldsInTable.get(i)).get(1)));
            textFieldsInTable.remove(i);
        }
    }

    public void clear() {
        nameOrgInput.setText("");
        innInput.setText("");
        kppInput.setText("");
        countryCodeInput.setText("");
        addressInput.setText("");
        deleteRows(documentTable.getRowCount(), documentTable.getRowCount()-1);
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
