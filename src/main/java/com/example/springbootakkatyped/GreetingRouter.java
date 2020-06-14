package com.example.springbootakkatyped;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class GreetingRouter {

  @Bean
  public RouterFunction<ServerResponse> route(GreetingHandler greetingHandler) {
    return RouterFunctions.route(
            RequestPredicates.GET("/hello").and(RequestPredicates.accept(MediaType.TEXT_PLAIN)),
            greetingHandler::hello)
        .andRoute(
            RequestPredicates.GET("/akkaHello").and(RequestPredicates.accept(MediaType.TEXT_PLAIN)),
            greetingHandler::akkaHello)
        .andRoute(
            RequestPredicates.GET("/akkaActor").and(RequestPredicates.accept(MediaType.TEXT_PLAIN)),
            greetingHandler::akkaActorAsk)
        .andRoute(
            RequestPredicates.GET("/question").and(RequestPredicates.accept(MediaType.TEXT_PLAIN)),
            greetingHandler::hello);
  }
}
