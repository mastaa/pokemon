package pl.org.opi.pokemon.poke.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.org.opi.pokemon.poke.dto.PokemonDto;
import pl.org.opi.pokemon.poke.dto.PokemonListDto;
import pl.org.opi.pokemon.poke.service.PokemonService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/pokemon")
@Tag(name = "Pokemon", description = "Retrieve Pokémon data from PokéAPI")
public class PokemonController {

    private final PokemonService service;

    public PokemonController(PokemonService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(
        summary = "List Pokémon",
        description = "Returns a paginated list of Pokémon with names and URLs",
        responses = {
            @ApiResponse(responseCode = "200", description = "Paginated list of Pokémon",
                content = @Content(schema = @Schema(implementation = PokemonListDto.class)))
        }
    )
    public Mono<PokemonListDto> listPokemon(
        @Parameter(description = "Number of results per page", example = "20")
        @RequestParam(defaultValue = "20") int limit,
        @Parameter(description = "Number of results to skip", example = "0")
        @RequestParam(defaultValue = "0") int offset
    ) {
        return service.listPokemon(limit, offset);
    }

    @GetMapping("/{nameOrId}")
    @Operation(
        summary = "Get Pokémon by name or ID",
        description = "Returns full details for a single Pokémon including stats, abilities, types and sprite",
        responses = {
            @ApiResponse(responseCode = "200", description = "Pokémon details",
                content = @Content(schema = @Schema(implementation = PokemonDto.class))),
            @ApiResponse(responseCode = "404", description = "Pokémon not found")
        }
    )
    public Mono<PokemonDto> getPokemon(
        @Parameter(description = "Pokémon name (e.g. pikachu) or numeric ID (e.g. 25)", example = "pikachu")
        @PathVariable String nameOrId
    ) {
        return service.getPokemon(nameOrId);
    }
}
