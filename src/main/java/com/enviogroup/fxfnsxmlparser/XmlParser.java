package com.enviogroup.fxfnsxmlparser;

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

    private String orgNameConsignee;
    private String innConsignee;
    private String kppConsignee;
    private String countryCodeConsignee;
    private String addressConsignee;
    private List<Agreement> agreements = new ArrayList<>();
    private String orgNameSender;
    private String innSender;
    private String kppSender;
    private String countryCodeSender;
    private String addressSender;
    private String cargo;
    private final Document document;
    private final XPath xPath;
    private File file;

    public XmlParser(File file) throws Exception {
        document = buildDocument(file);
        xPath = XPathFactory.newInstance().newXPath();
        Element elementSenderId = setElementByPath(GRUZ_OTPR_ID_PATH, xPath, document);
        orgNameSender = elementSenderId.getAttribute(NAIM_ORG);
        innSender = elementSenderId.getAttribute(INN);
        kppSender = elementSenderId.getAttribute(KPP);
        Element elementSenderAddress = setElementByPath(GRUZ_OTPR_ADDRESS_PATH, xPath, document);
        countryCodeSender = elementSenderAddress.getAttribute(COD_STR);
        addressSender = elementSenderAddress.getAttribute(ADR_TEXT);
        Element elementCargo = setElementByPath(TRAN_GRUZ_PATH, xPath, document);
        cargo = elementCargo.getAttribute(SV_TRAN_GRUZ);
        Element elementConsigneeId = setElementByPath(GRUZ_SV_YUL_UCH_PATH, xPath, document);
        orgNameConsignee = elementConsigneeId.getAttribute(NAIM_ORG);
        innConsignee = elementConsigneeId.getAttribute(INN);
        kppConsignee = elementConsigneeId.getAttribute(KPP);
        Element elementConsigneeAddress = setElementByPath(GRUZ_ADDRESS_PATH, xPath, document);
        countryCodeConsignee = elementConsigneeAddress.getAttribute(COD_STR);
        addressConsignee = elementConsigneeAddress.getAttribute(ADR_TEXT);
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
        setCompanyInformation(GRUZ_OTPR_ID_PATH, orgNameSender, innSender, kppSender, GRUZ_OTPR_ADDRESS_PATH, countryCodeSender, addressSender);
        setCompanyInformation(GRUZ_SV_YUL_UCH_PATH, orgNameConsignee, innConsignee, kppConsignee, GRUZ_ADDRESS_PATH, countryCodeConsignee, addressConsignee);
        Element elementCargo = setElementByPath(TRAN_GRUZ_PATH, xPath, document);
        if (!cargo.isEmpty()) {
            elementCargo.setAttribute(SV_TRAN_GRUZ, cargo);
        }
        List<Element> elementList = setElementsByPath(OSN_PER_PATH, xPath, document);
        Element elementSvPer = setElementByPath(SV_PER_PATH, xPath, document);
        while (!(elementList.size() == agreements.size())) {
            if (elementList.size() > agreements.size()) {
                elementSvPer.removeChild(elementList.get(elementList.size() - 1));
                elementList.remove(elementList.size() - 1);
            } else {
                Element newElementOsnPer = document.createElement(OSN_PER);
                elementSvPer.insertBefore(newElementOsnPer, elementSvPer.getElementsByTagName(SV_LIC_PER).item(0));
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

    private void setCompanyInformation(String gruzOtprIdPath, String orgNameSender, String innSender, String kppSender, String gruzOtprAddressPath, String countryCodeSender, String addressSender) throws XPathExpressionException {
        Element element = setElementByPath(gruzOtprIdPath, xPath, document);
        element.setAttribute(NAIM_ORG, orgNameSender);
        element.setAttribute(INN, innSender);
        element.setAttribute(KPP, kppSender);
        Element elementAddress = setElementByPath(gruzOtprAddressPath, xPath, document);
        elementAddress.setAttribute(COD_STR, countryCodeSender);
        elementAddress.setAttribute(ADR_TEXT, addressSender);
    }

    public String getOrgNameConsignee() {
        return orgNameConsignee;
    }

    public void setOrgNameConsignee(String orgNameConsignee) {
        this.orgNameConsignee = orgNameConsignee;
    }

    public String getInnConsignee() {
        return innConsignee;
    }

    public void setInnConsignee(String innConsignee) {
        this.innConsignee = innConsignee;
    }

    public String getKppConsignee() {
        return kppConsignee;
    }

    public void setKppConsignee(String kppConsignee) {
        this.kppConsignee = kppConsignee;
    }

    public String getCountryCodeConsignee() {
        return countryCodeConsignee;
    }

    public void setCountryCodeConsignee(String countryCodeConsignee) {
        this.countryCodeConsignee = countryCodeConsignee;
    }

    public String getAddressConsignee() {
        return addressConsignee;
    }

    public void setAddressConsignee(String addressConsignee) {
        this.addressConsignee = addressConsignee;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public List<Agreement> getAgreements() {
        return agreements;
    }

    public void setAgreements(List<Agreement> agreements) {
        this.agreements = agreements;
    }

    public String getOrgNameSender() {
        return orgNameSender;
    }

    public void setOrgNameSender(String orgNameSender) {
        this.orgNameSender = orgNameSender;
    }

    public String getInnSender() {
        return innSender;
    }

    public void setInnSender(String innSender) {
        this.innSender = innSender;
    }

    public String getKppSender() {
        return kppSender;
    }

    public void setKppSender(String kppSender) {
        this.kppSender = kppSender;
    }

    public String getCountryCodeSender() {
        return countryCodeSender;
    }

    public void setCountryCodeSender(String countryCodeSender) {
        this.countryCodeSender = countryCodeSender;
    }

    public String getAddressSender() {
        return addressSender;
    }

    public void setAddressSender(String addressSender) {
        this.addressSender = addressSender;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public File getFile() {
        return file;
    }
}
