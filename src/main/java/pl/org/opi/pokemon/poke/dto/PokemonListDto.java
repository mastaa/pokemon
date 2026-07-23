package pl.org.opi.pokemon.poke.dto;

import java.util.List;

public record PokemonListDto(
    int count,
    String next,
    String previous,
    List<NamedResource> results
) {}
