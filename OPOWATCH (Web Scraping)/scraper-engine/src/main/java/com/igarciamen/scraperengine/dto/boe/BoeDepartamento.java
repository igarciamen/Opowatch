package com.igarciamen.scraperengine.dto.boe;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BoeDepartamento {
    private String nombre;
    private List<BoeEpigrafe> epigrafe;
    // Some departments list their items directly, without grouping them under an epigrafe.
    private List<BoeItem> item;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public List<BoeEpigrafe> getEpigrafe() { return epigrafe; }
    public void setEpigrafe(List<BoeEpigrafe> epigrafe) { this.epigrafe = epigrafe; }

    public List<BoeItem> getItem() { return item; }
    public void setItem(List<BoeItem> item) { this.item = item; }
}