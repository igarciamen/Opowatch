package com.igarciamen.scraperengine.dto.boe;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BoeSeccion {
    private String codigo;
    private String nombre;
    private List<BoeDepartamento> departamento;

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public List<BoeDepartamento> getDepartamento() { return departamento; }
    public void setDepartamento(List<BoeDepartamento> departamento) { this.departamento = departamento; }
}