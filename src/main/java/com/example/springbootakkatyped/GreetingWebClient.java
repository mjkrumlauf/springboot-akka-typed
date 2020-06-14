package com.example.springbootakkatyped;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class GreetingWebClient {

  private final WebClient client = WebClient.create("http://localhost:8080");

  private Mono<ClientResponse> result =
      client.get()
        .uri("/akkaActor?name=Bob")
        .accept(MediaType.TEXT_PLAIN)
        .exchange();

  public String getResult() {
    return result.flatMap(res -> res.bodyToMono(String.class)).block();
  }
}
