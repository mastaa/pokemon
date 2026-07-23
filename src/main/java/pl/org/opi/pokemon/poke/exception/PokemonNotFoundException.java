package pl.org.opi.pokemon.poke.exception;

public class PokemonNotFoundException extends RuntimeException {

    public PokemonNotFoundException(String nameOrId) {
        super("Pokemon not found: " + nameOrId);
    }
}
