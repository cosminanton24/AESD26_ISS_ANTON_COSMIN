package ro.uaic.info.vros.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.uaic.info.vros.dto.ScenarioRequest;
import ro.uaic.info.vros.dto.ScenarioResponse;
import ro.uaic.info.vros.service.ScenarioService;
import java.net.URI;
import java.util.List;

// Endpoint-uri CRUD pentru scenarii; Accept selecteaza raspunsul JSON sau XML.
@RestController
@RequestMapping(value = "/api/scenarios",
        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
public class ScenarioController {
    private final ScenarioService service;

    // Controller-ul delega operatiile catre serviciul primit de la Spring.
    public ScenarioController(ScenarioService service) {
        this.service = service;
    }

    // GET: listeaza scenariile; Vary arata ca raspunsul depinde de antetul Accept.
    @GetMapping
    public ResponseEntity<List<ScenarioResponse>> findAll() {
        return ResponseEntity.ok().varyBy(HttpHeaders.ACCEPT).body(service.getAll());
    }

    // Documentatia OpenAPI descrie citirea si antetul conditional pentru Swagger UI.
    // EtagFilter gestioneaza If-None-Match si poate transforma raspunsul 200 in 304.
    @Operation(summary = "Read a scenario",
            description = "Accept selects JSON or XML. Send the returned ETag in If-None-Match to revalidate.")
    @Parameter(name = "If-None-Match", in = ParameterIn.HEADER,
            description = "ETag from a previous GET response")
    @ApiResponse(responseCode = "200", description = "Scenario returned with an ETag")
    @ApiResponse(responseCode = "304", description = "Representation has not changed", content = @Content)
    @ApiResponse(responseCode = "404", description = "Scenario not found", content = @Content)
    @GetMapping("/{id}")
    public ResponseEntity<ScenarioResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok().varyBy(HttpHeaders.ACCEPT).body(service.getById(id));
    }

    // POST: valideaza DTO-ul, creeaza scenariul si returneaza 201 cu adresa sa in Location.
    @Operation(summary = "Create a routing scenario",
            description = "Select existing vehicle and delivery IDs. This phase stores the scenario without solving it.")
    @ApiResponse(responseCode = "201", description = "Scenario created")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ScenarioResponse> create(@Valid @RequestBody ScenarioRequest request) {
        ScenarioResponse saved = service.create(request);
        return ResponseEntity.created(URI.create("/api/scenarios/" + saved.id()))
                .varyBy(HttpHeaders.ACCEPT).body(saved);
    }

    // PUT: actualizeaza scenariul identificat in URL, folosind un corp JSON valid.
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ScenarioResponse> update(@PathVariable Long id,
                                                  @Valid @RequestBody ScenarioRequest request) {
        return ResponseEntity.ok().varyBy(HttpHeaders.ACCEPT).body(service.update(id, request));
    }

    // DELETE: sterge scenariul si returneaza 204, fara corp de raspuns.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
