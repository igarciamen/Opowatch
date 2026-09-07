package com.igarciamen.scraperengine.dto.boe;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BoeSumarioResponse {
    private BoeData data;

    public BoeData getData() { return data; }
    public void setData(BoeData data) { this.data = data; }
}