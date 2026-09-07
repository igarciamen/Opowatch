package com.igarciamen.scraperengine.service;

import com.igarciamen.scraperengine.client.BoeApiClient;
import com.igarciamen.scraperengine.dto.boe.*;
import com.igarciamen.scraperengine.payloads.response.BoePostingResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class BoeScraperService {

    private static final String TARGET_SECTION_KEYWORD = "oposiciones y concursos";

    private final BoeApiClient boeApiClient;

    public BoeScraperService(BoeApiClient boeApiClient) {
        this.boeApiClient = boeApiClient;
    }

    public List<BoePostingResponse> getPostings(LocalDate date) {
        List<BoePostingResponse> result = new ArrayList<>();

        boeApiClient.fetchSummary(date).ifPresent(response -> {
            BoeSumario sumario = response.getData() != null ? response.getData().getSumario() : null;
            if (sumario == null || sumario.getDiario() == null) {
                return;
            }

            for (BoeDiario diario : sumario.getDiario()) {
                collectFromDiario(diario, result);
            }
        });

        return result;
    }

    private void collectFromDiario(BoeDiario diario, List<BoePostingResponse> result) {
        if (diario.getSeccion() == null) {
            return;
        }

        for (BoeSeccion seccion : diario.getSeccion()) {
            if (!isOposicionesSection(seccion)) {
                continue;
            }
            if (seccion.getDepartamento() == null) {
                continue;
            }
            for (BoeDepartamento departamento : seccion.getDepartamento()) {
                collectFromDepartamento(departamento, result);
            }
        }
    }

    private boolean isOposicionesSection(BoeSeccion seccion) {
        return seccion.getNombre() != null
                && seccion.getNombre().toLowerCase().contains(TARGET_SECTION_KEYWORD);
    }

    private void collectFromDepartamento(BoeDepartamento departamento, List<BoePostingResponse> result) {
        String organization = departamento.getNombre();

        if (departamento.getItem() != null) {
            for (BoeItem item : departamento.getItem()) {
                result.add(toPosting(item, organization));
            }
        }

        if (departamento.getEpigrafe() != null) {
            for (BoeEpigrafe epigrafe : departamento.getEpigrafe()) {
                if (epigrafe.getItem() == null) {
                    continue;
                }
                for (BoeItem item : epigrafe.getItem()) {
                    result.add(toPosting(item, organization));
                }
            }
        }
    }

    private BoePostingResponse toPosting(BoeItem item, String organization) {
        String pdfUrl = item.getUrl_pdf() != null ? item.getUrl_pdf().getTexto() : null;
        return new BoePostingResponse(
                item.getIdentificador(),
                item.getTitulo(),
                organization,
                item.getUrl_html(),
                pdfUrl
        );
    }
}