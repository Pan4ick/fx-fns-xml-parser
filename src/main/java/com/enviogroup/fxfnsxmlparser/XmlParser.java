package com.enviogroup.fxfnsxmlparser;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class XmlParser implements Tags {

    private String orgName;
    private String inn;
    private String kpp;
    private String countryCode;
    private String address;
    private List<Agreement> agreements = new ArrayList<>();
    private final Document document;
    private final XPath xPath;
    private File file;

    public XmlParser(File file) throws Exception {
        document = buildDocument(file);
        xPath = XPathFactory.newInstance().newXPath();
        Element element = setElementByPath(GRUZ_SV_YUL_UCH_PATH, xPath, document);
        orgName = element.getAttribute(NAIM_ORG);
        inn = element.getAttribute(INN);
        kpp = element.getAttribute(KPP);
        element = setElementByPath(GRUZ_ADDRESS_PATH, xPath, document);
        countryCode = element.getAttribute(COD_STR);
        address = element.getAttribute(ADR_TEXT);
        for (Element e : setElementsByPath(OSN_PER_PATH, xPath, document)) {
            Agreement agreement = new Agreement();
            agreement.setDocumentName(e.getAttribute(NAIM_OSN));
            agreement.setDocumentNumber(e.getAttribute(NOM_OSN));
            agreement.setDocumentDate(e.getAttribute(DATE_OSN));
            agreements.add(agreement);
        }
    }

    private Element setElementByPath(String path, XPath xPath, Document document) throws XPathExpressionException {
        NodeList nodes = (NodeList) xPath.evaluate(path, document, XPathConstants.NODESET);
        return (Element) nodes.item(0);
    }

    private List<Element> setElementsByPath(String path, XPath xPath, Document document) throws XPathExpressionException {
        NodeList nodes = (NodeList) xPath.evaluate(path, document, XPathConstants.NODESET);
        List<Element> elementList = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            elementList.add((Element) nodes.item(i));
        }
        return elementList;
    }

    private Document buildDocument(File file) throws Exception {
        setFile(file);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//        Node node = document.getFirstChild();
//        System.out.println(node.getNodeName());
        return dbf.newDocumentBuilder().parse(file);
    }

    public void saveChanges() throws XPathExpressionException, TransformerException {
        saveChanges(file);
    }

    public void saveChanges(File file) throws XPathExpressionException, TransformerException {
        Element elementInformation = setElementByPath(GRUZ_SV_YUL_UCH_PATH, xPath, document);
        elementInformation.setAttribute(NAIM_ORG, orgName);
        elementInformation.setAttribute(INN, inn);
        elementInformation.setAttribute(KPP, kpp);
        Element elementAddress = setElementByPath(GRUZ_ADDRESS_PATH, xPath, document);
        elementAddress.setAttribute(COD_STR, countryCode);
        elementAddress.setAttribute(ADR_TEXT, address);
        List<Element> elementList = setElementsByPath(OSN_PER_PATH, xPath, document);
        Element elementSvPer = setElementByPath(SV_PER_PATH, xPath, document);
        while (!(elementList.size() == agreements.size())) {
            if (elementList.size() > agreements.size()) {
                elementSvPer.removeChild(elementList.get(elementList.size() - 1));
                elementList.remove(elementList.size() - 1);
            } else {
                Element newElementOsnPer = document.createElement(OSN_PER);
                elementSvPer.insertBefore(newElementOsnPer, elementSvPer.getFirstChild());
                elementSvPer.normalize();
                elementList.add(newElementOsnPer);
            }
        }
        for (int i = 0; i < agreements.size(); i++) {
            elementList.get(i).setAttribute(NAIM_OSN, agreements.get(i).getDocumentName());
            elementList.get(i).setAttribute(NOM_OSN, agreements.get(i).getDocumentNumber());
            elementList.get(i).setAttribute(DATE_OSN, agreements.get(i).getDocumentDate());
        }
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "no");
        Result output = new StreamResult(file);
        Source input = new DOMSource(document);
        transformer.transform(input, output);
    }

//    public void clear() {
//        orgName = "";
//        inn = "";
//        kpp = "";
//        countryCode = "";
//        address = "";
//        agreements.clear();
//        file = null;
//    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }

    public String getKpp() {
        return kpp;
    }

    public void setKpp(String kpp) {
        this.kpp = kpp;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void addAgreement(Agreement agreement) {
        agreements.add(agreement);
    }

    public List<Agreement> getAgreements() {
        return agreements;
    }

    public void setAgreements(List<Agreement> agreements) {
        this.agreements = agreements;
    }
}
