package com.igarciamen.scraperengine.service;

import com.igarciamen.scraperengine.client.BoeApiClient;
import com.igarciamen.scraperengine.dto.boe.*;
import com.igarciamen.scraperengine.payloads.response.BoePostingResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BoeScraperServiceTest {

    @Mock
    private BoeApiClient boeApiClient;

    @InjectMocks
    private BoeScraperService boeScraperService;

    private BoeItem item(String id, String title, String url) {
        BoeItem item = new BoeItem();
        item.setIdentificador(id);
        item.setTitulo(title);
        item.setUrl_html(url);
        return item;
    }

    @Test
    void getPostings_onlyKeepsOposicionesSection() {
        // Section with items directly under departamento, no epigrafe.
        BoeDepartamento deptDirect = new BoeDepartamento();
        deptDirect.setNombre("MINISTERIO DE HACIENDA");
        deptDirect.setItem(List.of(item("BOE-1", "Analista programador", "https://boe.es/1")));

        BoeSeccion oposiciones = new BoeSeccion();
        oposiciones.setNombre("II. Autoridades y personal - B. Oposiciones y concursos");
        oposiciones.setDepartamento(List.of(deptDirect));

        // Section that must be ignored.
        BoeSeccion disposicionesGenerales = new BoeSeccion();
        disposicionesGenerales.setNombre("I. Disposiciones generales");
        BoeDepartamento otherDept = new BoeDepartamento();
        otherDept.setNombre("MINISTERIO DE JUSTICIA");
        otherDept.setItem(List.of(item("BOE-2", "Real decreto de otra cosa", "https://boe.es/2")));
        disposicionesGenerales.setDepartamento(List.of(otherDept));

        BoeDiario diario = new BoeDiario();
        diario.setSeccion(List.of(oposiciones, disposicionesGenerales));

        BoeSumario sumario = new BoeSumario();
        sumario.setDiario(List.of(diario));

        BoeData data = new BoeData();
        data.setSumario(sumario);

        BoeSumarioResponse response = new BoeSumarioResponse();
        response.setData(data);

        LocalDate date = LocalDate.of(2026, 8, 14);
        when(boeApiClient.fetchSummary(date)).thenReturn(Optional.of(response));

        List<BoePostingResponse> result = boeScraperService.getPostings(date);

        assertEquals(1, result.size());
        assertEquals("BOE-1", result.get(0).getIdentifier());
        assertEquals("MINISTERIO DE HACIENDA", result.get(0).getOrganization());

        System.out.println("=== getPostings_onlyKeepsOposicionesSection ===");
        System.out.println("Postings encontrados: " + result.size() + " (solo de la seccion correcta)");
    }

    @Test
    void getPostings_flattensItemsNestedUnderEpigrafe() {
        BoeEpigrafe epigrafe = new BoeEpigrafe();
        epigrafe.setNombre("Cuerpo de Sistemas y Tecnologias");
        epigrafe.setItem(List.of(item("BOE-3", "Tecnico de sistemas", "https://boe.es/3")));

        BoeDepartamento departamento = new BoeDepartamento();
        departamento.setNombre("MINISTERIO DE DEFENSA");
        departamento.setEpigrafe(List.of(epigrafe));

        BoeSeccion seccion = new BoeSeccion();
        seccion.setNombre("Oposiciones y concursos");
        seccion.setDepartamento(List.of(departamento));

        BoeDiario diario = new BoeDiario();
        diario.setSeccion(List.of(seccion));

        BoeSumario sumario = new BoeSumario();
        sumario.setDiario(List.of(diario));

        BoeData data = new BoeData();
        data.setSumario(sumario);

        BoeSumarioResponse response = new BoeSumarioResponse();
        response.setData(data);

        LocalDate date = LocalDate.of(2026, 8, 14);
        when(boeApiClient.fetchSummary(date)).thenReturn(Optional.of(response));

        List<BoePostingResponse> result = boeScraperService.getPostings(date);

        assertEquals(1, result.size());
        assertEquals("Tecnico de sistemas", result.get(0).getTitle());

        System.out.println("=== getPostings_flattensItemsNestedUnderEpigrafe ===");
        System.out.println("Titulo encontrado bajo epigrafe: " + result.get(0).getTitle());
    }

    @Test
    void getPostings_returnsEmptyWhenNoSummaryAvailable() {
        LocalDate sunday = LocalDate.of(2026, 8, 16);
        when(boeApiClient.fetchSummary(sunday)).thenReturn(Optional.empty());

        List<BoePostingResponse> result = boeScraperService.getPostings(sunday);

        assertTrue(result.isEmpty());

        System.out.println("=== getPostings_returnsEmptyWhenNoSummaryAvailable ===");
        System.out.println("Resultado para un domingo: " + result.isEmpty());
    }
}