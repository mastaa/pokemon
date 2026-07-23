package pl.org.opi.pokemon.poke.service;

import org.springframework.stereotype.Service;
import pl.org.opi.pokemon.poke.client.PokeApiClient;
import pl.org.opi.pokemon.poke.dto.PokemonDto;
import pl.org.opi.pokemon.poke.dto.PokemonListDto;
import reactor.core.publisher.Mono;

@Service
public class PokemonService {

    private final PokeApiClient client;

    public PokemonService(PokeApiClient client) {
        this.client = client;
    }

    public Mono<PokemonDto> getPokemon(String nameOrId) {
        return client.getPokemon(nameOrId);
    }

    public Mono<PokemonListDto> listPokemon(int limit, int offset) {
        return client.listPokemon(limit, offset);
    }
}
