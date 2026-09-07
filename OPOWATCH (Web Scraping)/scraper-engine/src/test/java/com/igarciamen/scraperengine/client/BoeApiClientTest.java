package com.igarciamen.scraperengine.client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BoeApiClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private BoeApiClient boeApiClient;

    private static final String SAMPLE_JSON = """
        {
          "data": {
            "sumario": {
              "diario": [
                {
                  "seccion": [
                    {
                      "codigo": "2B",
                      "nombre": "II. Autoridades y personal - B. Oposiciones y concursos",
                      "departamento": [
                        {
                          "nombre": "MINISTERIO DE HACIENDA",
                          "epigrafe": {
                            "nombre": "Cuerpo de Sistemas y Tecnologias",
                            "item": {
                              "identificador": "BOE-A-2026-1001",
                              "titulo": "Convocatoria de plazas de analista programador",
                              "url_html": "https://www.boe.es/diario_boe/txt.php?id=BOE-A-2026-1001"
                            }
                          }
                        }
                      ]
                    }
                  ]
                }
              ]
            }
          }
        }
        """;

    @Test
    void fetchSummary_parsesSingleObjectsAsOneElementLists() {
        // baseUrl is normally injected via @Value; set it manually since this is a plain unit test.
        ReflectionTestUtils.setField(boeApiClient, "baseUrl", "https://www.boe.es/datosabiertos/api/boe/sumario");

        LocalDate date = LocalDate.of(2026, 8, 14);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>(SAMPLE_JSON, HttpStatus.OK));

        Optional<com.igarciamen.scraperengine.dto.boe.BoeSumarioResponse> result = boeApiClient.fetchSummary(date);

        assertTrue(result.isPresent());
        var seccion = result.get().getData().getSumario().getDiario().get(0).getSeccion().get(0);
        var item = seccion.getDepartamento().get(0).getEpigrafe().get(0).getItem().get(0);

        assertEquals("BOE-A-2026-1001", item.getIdentificador());

        System.out.println("=== fetchSummary_parsesSingleObjectsAsOneElementLists ===");
        System.out.println("Titulo parseado: " + item.getTitulo());
    }

    @Test
    void fetchSummary_returnsEmptyWhenBoeHasNoIssueThatDay() {
        ReflectionTestUtils.setField(boeApiClient, "baseUrl", "https://www.boe.es/datosabiertos/api/boe/sumario");

        LocalDate sunday = LocalDate.of(2026, 8, 16);
        HttpClientErrorException notFound = HttpClientErrorException.create(
                HttpStatus.NOT_FOUND, "Not Found", HttpHeaders.EMPTY, new byte[0], null);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenThrow(notFound);

        Optional<com.igarciamen.scraperengine.dto.boe.BoeSumarioResponse> result = boeApiClient.fetchSummary(sunday);

        assertTrue(result.isEmpty());

        System.out.println("=== fetchSummary_returnsEmptyWhenBoeHasNoIssueThatDay ===");
        System.out.println("Resultado para un domingo: " + result.isEmpty());
    }
}