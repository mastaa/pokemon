package pl.org.opi.pokemon.poke.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.json.JacksonJsonDecoder;
import org.springframework.http.codec.json.JacksonJsonEncoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.json.JsonMapper;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient pokeApiWebClient(@Value("${pokeapi.base-url}") String baseUrl) {
        var mapper = JsonMapper.builder()
            .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
            .build();

        return WebClient.builder()
            .baseUrl(baseUrl)
            .exchangeStrategies(ExchangeStrategies.builder()
                .codecs(c -> {
                    c.defaultCodecs().jacksonJsonDecoder(new JacksonJsonDecoder(mapper));
                    c.defaultCodecs().jacksonJsonEncoder(new JacksonJsonEncoder(mapper));
                    c.defaultCodecs().maxInMemorySize(2 * 1024 * 1024);
                })
                .build())
            .build();
    }
}
