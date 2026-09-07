package com.igarciamen.scraperengine.dto.boe;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BoeEpigrafe {
    private String nombre;
    private List<BoeItem> item;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public List<BoeItem> getItem() { return item; }
    public void setItem(List<BoeItem> item) { this.item = item; }
}