package com.igarciamen.scraperengine.dto.boe;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BoeDiario {
    private List<BoeSeccion> seccion;

    public List<BoeSeccion> getSeccion() { return seccion; }
    public void setSeccion(List<BoeSeccion> seccion) { this.seccion = seccion; }
}