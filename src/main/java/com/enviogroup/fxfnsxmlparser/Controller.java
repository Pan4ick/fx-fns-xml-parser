package com.enviogroup.fxfnsxmlparser;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
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

import static com.enviogroup.fxfnsxmlparser.Tags.*;

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
    private Label errorLabel;

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
                        xmlParser = setFormValues(file);
                        errorLabel.setVisible(false);
                        addListeners();
                    } catch (Exception e) {
                        errorLabel.setText("Внимание! Открываемый вам файл не соотвествует формату ФНС.");
                        errorLabel.setVisible(true);
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
                xmlParser.setAgreements(createAgreementsList());
                try {
                    xmlParser.saveChanges();
                } catch (XPathExpressionException | TransformerException ex) {
                    errorLabel.setText("Хуйня какая-то, этого не должно быть.");
                    errorLabel.setVisible(true);
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
                    deleteRows(textFieldsInTable.size(), textFieldsInTable.size() - listBox.getValue());
                } else {
                    createRows(documentTable.getRowCount(), listBox.getValue() - textFieldsInTable.size());
                }
                documentTable.setVisible(listBox.getValue() > 0);
            }
        });
    }

    private XmlParser setFormValues(File file) throws Exception {
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
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML", "*.xml"));
    }

    private void addValuesToTable(XmlParser xmlParser) {
        for (int i = 0; i < textFieldsInTable.size(); i++) {
            textFieldsInTable.get(i + 1).get(0).setText(xmlParser.getAgreements().get(i).getDocumentName());
            textFieldsInTable.get(i + 1).get(1).setText(xmlParser.getAgreements().get(i).getDocumentNumber());
            textFieldsInTable.get(i + 1).get(2).setText(xmlParser.getAgreements().get(i).getDocumentDate());
        }
    }

    private List<Agreement> createAgreementsList() {
        List<Agreement> agreements = new ArrayList<Agreement>();
        for (int i = 0; i < textFieldsInTable.size(); i++) {
            List<TextField> tfs = textFieldsInTable.get(i + 1);
            Agreement agreement = new Agreement(tfs.get(0).getText(), tfs.get(1).getText(), tfs.get(2).getText());
            agreements.add(agreement);
        }
        return agreements;
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

    private void clear() {
        nameOrgInput.setText("");
        innInput.setText("");
        kppInput.setText("");
        countryCodeInput.setText("");
        addressInput.setText("");
        deleteRows(textFieldsInTable.size(), textFieldsInTable.size());
        listBox.setValue(0);
        documentTable.setVisible(false);
        xmlParser = null;
    }

    private void addListeners() {
        innInput.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                if (!t1.matches(INN_REGEX) || t1.isEmpty()) {
                    errorLabel.setText("Внимание! ИНН организации должен состоять из 10 цифр");
                    //errorLabel.setVisible(true);
                } else {
                    innInput.setText(t1);
                    errorLabel.setVisible(false);
                }
            }
        });

        kppInput.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                if (!t1.matches(KPP_REGEX) || t1.isEmpty()) {
                    errorLabel.setText("Внимание! КПП организации должен состоять из 9 цифр");
                    //errorLabel.setVisible(true);
                } else {
                    kppInput.setText(s);
                    errorLabel.setVisible(false);
                }
            }
        });

        for (int i = 1; i < textFieldsInTable.size(); i++) {
            TextField textField = textFieldsInTable.get(i).get(2);
            textField.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                    if (!t1.matches(DATE_REGEX) || t1.isEmpty()) {
                        errorLabel.setText("Внимание! Введите дату в формате дд.мм.гггг");
                        //errorLabel.setVisible(true);
                    } else {
                        textField.setText(s);
                        errorLabel.setVisible(false);
                    }
                }
            });
        }
    }
}
