package com.enviogroup.fxfnsxmlparser;

import javafx.application.Platform;
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
import org.apache.commons.io.FileUtils;

import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.enviogroup.fxfnsxmlparser.Tags.*;

public class Controller {

    @FXML
    private TextField addressInputConsignee;

    @FXML
    private TextField cargoInput;

    @FXML
    private TextField addressInputSender;

    @FXML
    private TextField countryCodeInputConsignee;

    @FXML
    private TextField countryCodeInputSender;

    @FXML
    private GridPane documentTable;

    @FXML
    private Label errorLabel;

    @FXML
    private MenuItem exit;

    @FXML
    private TextField innInputConsignee;

    @FXML
    private TextField innInputSender;

    @FXML
    private TextField kppInputConsignee;

    @FXML
    private TextField kppInputSender;

    @FXML
    private ChoiceBox<Integer> listBox;

    @FXML
    private TextField nameOrgInputConsignee;

    @FXML
    private TextField nameOrgInputSender;

    @FXML
    private MenuItem openXml;

    @FXML
    private MenuItem saveAsXml;

    @FXML
    private MenuItem saveXml;

    private static XmlParser xmlParser;
    private Map<Integer, List<TextField>> textFieldsInTable = new HashMap<>();
    private static final int ELEMENTS_IN_ROW_AMOUNT = 3;
    private static final ObservableList<Integer> osnPerAmount = FXCollections.observableArrayList(0, 1, 2, 3);

    @FXML
    void initialize() {
        final FileChooser fileChooser = new FileChooser();
        exit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Platform.exit();
            }
        });
        saveAsXml.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                configureSaveFileChooser(fileChooser);
                File file = fileChooser.showSaveDialog(new Stage());
                getValuesFromForm();
                if (file != null) {
                    try {
                        if (file.getCanonicalPath().equals(xmlParser.getFile().getCanonicalPath())) {
                            xmlParser.saveChanges();
                        } else {
                            FileUtils.copyFile(xmlParser.getFile(), file);
                        }
                        clear();
                    } catch (Exception e) {
                        errorLabel.setText("???? ???????? ?????????????????? ???????????? ????????.");
                        errorLabel.setVisible(true);
                    }
                }
            }
        });
        openXml.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                configureOpenFileChooser(fileChooser);
                File file = fileChooser.showOpenDialog(new Stage());
                if (file != null) {
                    try {
                        xmlParser = setFormValues(file);
                        errorLabel.setVisible(false);
                        addListeners();
                    } catch (Exception e) {
                        errorLabel.setText("????????????????! ?????????????????????? ?????? ???????? ???? ???????????????????????? ?????????????? ??????.");
                        errorLabel.setVisible(true);
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        saveXml.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                getValuesFromForm();
                try {
                    xmlParser.saveChanges();
                    clear();
                } catch (XPathExpressionException | TransformerException ex) {
                    errorLabel.setText("????????????.");
                    errorLabel.setVisible(true);
                    throw new RuntimeException(ex);
                }

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
                addListeners();
                documentTable.setVisible(listBox.getValue() > 0);
            }
        });
    }

    private void getValuesFromForm() {
        xmlParser.setCargo(cargoInput.getText());
        xmlParser.setOrgNameSender(nameOrgInputSender.getText());
        xmlParser.setInnSender(innInputSender.getText());
        xmlParser.setKppSender(kppInputSender.getText());
        xmlParser.setCountryCodeSender(countryCodeInputSender.getText());
        xmlParser.setAddressSender(addressInputSender.getText());
        xmlParser.setOrgNameConsignee(nameOrgInputConsignee.getText());
        xmlParser.setInnConsignee(innInputConsignee.getText());
        xmlParser.setKppConsignee(kppInputConsignee.getText());
        xmlParser.setCountryCodeConsignee(countryCodeInputConsignee.getText());
        xmlParser.setAddressConsignee(addressInputConsignee.getText());
        xmlParser.setAgreements(createAgreementsList());
    }

    private XmlParser setFormValues(File file) throws Exception {
        XmlParser xmlParser = new XmlParser(file);
        cargoInput.setText(xmlParser.getCargo());
        nameOrgInputSender.setText(xmlParser.getOrgNameSender());
        innInputSender.setText(xmlParser.getInnSender());
        kppInputSender.setText(xmlParser.getKppSender());
        countryCodeInputSender.setText(xmlParser.getCountryCodeSender());
        addressInputSender.setText(xmlParser.getAddressSender());
        nameOrgInputConsignee.setText(xmlParser.getOrgNameConsignee());
        innInputConsignee.setText(xmlParser.getInnConsignee());
        kppInputConsignee.setText(xmlParser.getKppConsignee());
        countryCodeInputConsignee.setText(xmlParser.getCountryCodeConsignee());
        addressInputConsignee.setText(xmlParser.getAddressConsignee());
        listBox.setValue(xmlParser.getAgreements().size());
        addValuesToTable(xmlParser);
        return xmlParser;
    }

    private static void configureOpenFileChooser(final FileChooser fileChooser) {
        fileChooser.setTitle("??????????????...");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Downloads/"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML", "*.xml"));
    }

    private static void configureSaveFileChooser(final FileChooser fileChooser) {
        fileChooser.setTitle("??????????????????...");
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
        cargoInput.clear();
        nameOrgInputSender.clear();
        innInputSender.clear();
        kppInputSender.clear();
        countryCodeInputSender.clear();
        addressInputSender.clear();
        nameOrgInputConsignee.clear();
        innInputConsignee.clear();
        kppInputConsignee.clear();
        countryCodeInputConsignee.clear();
        addressInputConsignee.clear();
        deleteRows(textFieldsInTable.size(), textFieldsInTable.size());
        listBox.setValue(0);
        documentTable.setVisible(false);
        errorLabel.setVisible(false);
        xmlParser = null;
    }

    private void addListeners() {
        innInputConsignee.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                if (!t1.matches(INN_REGEX) || t1.isEmpty()) {
                    errorLabel.setText("????????????????! ?????? ?????????????????????? ???????????? ???????????????? ???? 10 ????????");
                    errorLabel.setVisible(true);
                } else {
                    innInputConsignee.setText(t1);
                    errorLabel.setVisible(false);
                }
            }
        });

        kppInputConsignee.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                if (!t1.matches(KPP_REGEX) || t1.isEmpty()) {
                    errorLabel.setText("????????????????! ?????? ?????????????????????? ???????????? ???????????????? ???? 9 ????????");
                    errorLabel.setVisible(true);
                } else {
                    kppInputConsignee.setText(t1);
                    errorLabel.setVisible(false);
                }
            }
        });

        for (int i = 1; i <= textFieldsInTable.size(); i++) {
            TextField textField = textFieldsInTable.get(i).get(2);
            textField.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                    if (!t1.matches(DATE_REGEX) || t1.isEmpty()) {
                        errorLabel.setText("????????????????! ?????????????? ???????? ?? ?????????????? ????.????.????????");
                        errorLabel.setVisible(true);
                    } else {
                        textField.setText(t1);
                        errorLabel.setVisible(false);
                    }
                }
            });
        }
    }
}
