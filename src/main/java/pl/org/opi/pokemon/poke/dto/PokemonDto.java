package pl.org.opi.pokemon.poke.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record PokemonDto(
    int id,
    String name,
    int height,
    int weight,
    Integer baseExperience,
    List<TypeSlot> types,
    List<AbilitySlot> abilities,
    List<StatSlot> stats,
    Sprites sprites
) {
    public record TypeSlot(int slot, NamedResource type) {}

    public record AbilitySlot(
        NamedResource ability,
        @JsonProperty("is_hidden") boolean isHidden,
        int slot
    ) {}

    public record StatSlot(int baseStat, int effort, NamedResource stat) {}

    public record Sprites(String frontDefault) {}
}
