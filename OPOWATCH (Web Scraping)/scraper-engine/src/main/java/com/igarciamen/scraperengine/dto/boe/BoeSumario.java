package com.igarciamen.scraperengine.dto.boe;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BoeSumario {
    private List<BoeDiario> diario;

    public List<BoeDiario> getDiario() { return diario; }
    public void setDiario(List<BoeDiario> diario) { this.diario = diario; }
}