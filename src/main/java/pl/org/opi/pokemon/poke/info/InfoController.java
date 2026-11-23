package pl.org.opi.pokemon.poke.info;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Endpoint warsztatowy: pokazuje, z jaką konfiguracją wystartowała aplikacja
 * i na którym podzie (hostname) obsłużono żądanie.
 */
@RestController
@RequestMapping("/api/info")
@Tag(name = "Info", description = "Informacje o instancji aplikacji (warsztat Kubernetes)")
public class InfoController {

    private final String environment;
    private final String welcomeMessage;
    private final String apiToken;
    private final String pokeApiBaseUrl;

    public InfoController(
        @Value("${app.environment}") String environment,
        @Value("${app.welcome-message}") String welcomeMessage,
        @Value("${app.api-token}") String apiToken,
        @Value("${pokeapi.base-url}") String pokeApiBaseUrl
    ) {
        this.environment = environment;
        this.welcomeMessage = welcomeMessage;
        this.apiToken = apiToken;
        this.pokeApiBaseUrl = pokeApiBaseUrl;
    }

    @GetMapping
    @Operation(summary = "Informacje o instancji", description = "Hostname poda, środowisko i konfiguracja")
    public Map<String, Object> info() {
        var info = new LinkedHashMap<String, Object>();
        info.put("hostname", hostname());
        info.put("environment", environment);
        info.put("welcomeMessage", welcomeMessage);
        info.put("pokeApiBaseUrl", pokeApiBaseUrl);
        info.put("apiTokenConfigured", !apiToken.isBlank());
        info.put("apiTokenMasked", mask(apiToken));
        return info;
    }

    private static String hostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return System.getenv().getOrDefault("HOSTNAME", "unknown");
        }
    }

    /** Nigdy nie zwracamy sekretu w całości – tylko pierwsze 2 znaki. */
    private static String mask(String secret) {
        if (secret.isBlank()) {
            return "";
        }
        return secret.substring(0, Math.min(2, secret.length())) + "****";
    }
}
