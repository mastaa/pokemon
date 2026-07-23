package pl.org.opi.pokemon.poke.controller;

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
public class PokemonController {

    private final PokemonService service;

    public PokemonController(PokemonService service) {
        this.service = service;
    }

    @GetMapping
    public Mono<PokemonListDto> listPokemon(
        @RequestParam(defaultValue = "20") int limit,
        @RequestParam(defaultValue = "0") int offset
    ) {
        return service.listPokemon(limit, offset);
    }

    @GetMapping("/{nameOrId}")
    public Mono<PokemonDto> getPokemon(@PathVariable String nameOrId) {
        return service.getPokemon(nameOrId);
    }
}
