package com.igarciamen.scraperengine.payloads.response;

public class DocumentTextResponse {
    private boolean textFound;
    private String text;

    public DocumentTextResponse(boolean textFound, String text) {
        this.textFound = textFound;
        this.text = text;
    }

    public boolean isTextFound() { return textFound; }
    public String getText() { return text; }
}