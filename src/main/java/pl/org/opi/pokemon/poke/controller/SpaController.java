package pl.org.opi.pokemon.poke.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class SpaController {

    private final Resource indexHtml = new ClassPathResource("static/index.html");

    @GetMapping(value = { "/", "/pokemon", "/pokemon/**" }, produces = MediaType.TEXT_HTML_VALUE)
    public Mono<Resource> spa() {
        return Mono.just(indexHtml);
    }
}
