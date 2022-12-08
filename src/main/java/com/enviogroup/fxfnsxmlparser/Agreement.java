package com.enviogroup.fxfnsxmlparser;

public class Agreement {
    private String documentName;
    private String documentNumber;
    private String documentDate;

    public Agreement() {

    }

    public Agreement(String documentName, String documentNumber, String documentDate) {
        this.documentName = documentName;
        this.documentNumber = documentNumber;
        this.documentDate = documentDate;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getDocumentDate() {
        return documentDate;
    }

    public void setDocumentDate(String documentDate) {
        this.documentDate = documentDate;
    }
}
