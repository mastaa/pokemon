package pl.org.opi.pokemon.poke.client;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import pl.org.opi.pokemon.poke.dto.PokemonDto;
import pl.org.opi.pokemon.poke.dto.PokemonListDto;
import pl.org.opi.pokemon.poke.exception.PokemonNotFoundException;
import reactor.core.publisher.Mono;

@Component
public class PokeApiClient {

    private final WebClient webClient;

    public PokeApiClient(WebClient pokeApiWebClient) {
        this.webClient = pokeApiWebClient;
    }

    public Mono<PokemonDto> getPokemon(String nameOrId) {
        return webClient.get()
            .uri("/api/v2/pokemon/{nameOrId}", nameOrId)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError,
                _ -> Mono.error(new PokemonNotFoundException(nameOrId)))
            .bodyToMono(PokemonDto.class);
    }

    public Mono<PokemonListDto> listPokemon(int limit, int offset) {
        return webClient.get()
            .uri(uri -> uri.path("/api/v2/pokemon")
                .queryParam("limit", limit)
                .queryParam("offset", offset)
                .build())
            .retrieve()
            .bodyToMono(PokemonListDto.class);
    }
}
