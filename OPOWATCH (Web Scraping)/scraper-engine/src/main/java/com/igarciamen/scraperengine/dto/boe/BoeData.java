package com.igarciamen.scraperengine.dto.boe;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BoeData {
    private BoeSumario sumario;

    public BoeSumario getSumario() { return sumario; }
    public void setSumario(BoeSumario sumario) { this.sumario = sumario; }
}