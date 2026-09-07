package com.igarciamen.scraperengine.dto.boe;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BoeItem {
    private String identificador;
    private String titulo;
    private String url_html;
    private BoeUrlPdf url_pdf;

    public String getIdentificador() { return identificador; }
    public void setIdentificador(String identificador) { this.identificador = identificador; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getUrl_html() { return url_html; }
    public void setUrl_html(String url_html) { this.url_html = url_html; }

    public BoeUrlPdf getUrl_pdf() { return url_pdf; }
    public void setUrl_pdf(BoeUrlPdf url_pdf) { this.url_pdf = url_pdf; }
}