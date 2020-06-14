package com.example.springbootakkatyped;

import akka.NotUsed;
import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Source;
import akka.stream.scaladsl.Sink;
import akka.stream.typed.javadsl.ActorFlow;
import org.reactivestreams.Publisher;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Component
public class GreetingHandler {

  private static final boolean NO_FANOUT = false;

  private final ActorSystem<Message> system;

  public GreetingHandler() {
    system = ActorSystem.create(Questioner.create(), "questioner");
  }

  private Duration timeout = Duration.of(1, ChronoUnit.SECONDS);

  public Mono<ServerResponse> hello(ServerRequest request) {
    return ServerResponse.ok()
        .contentType(MediaType.TEXT_PLAIN)
        .body(BodyInserters.fromValue("Hello, Reactive Spring!"));
  }

  public Mono<ServerResponse> akkaHello(ServerRequest request) {
    Publisher<String> helloPublisher =
        Source.repeat(request.queryParam("name").get())
            .take(1000)
//            .map(s -> s + "\n")
            .via(echoFlow())
            .async()
            .runWith(Sink.asPublisher(NO_FANOUT), system);

    return ServerResponse.ok().contentType(MediaType.TEXT_PLAIN).body(helloPublisher, String.class);
  }

  private Flow<String, String, NotUsed> echoFlow() {
    return Flow.fromFunction(s -> String.format("[%s] Hi %s\n", Thread.currentThread().getName(), s));
  }

  public Mono<ServerResponse> akkaActorAsk(ServerRequest request) {
    Publisher<String> publisher =
        Source.repeat(request.queryParam("name").get())
            .take(10000)
            .map(s -> s + "\n")
            .via(ActorFlow.ask(Runtime.getRuntime().availableProcessors(), system, timeout, Message::new))
//            .async()
            .runWith(Sink.asPublisher(NO_FANOUT), system);

    return ServerResponse.ok().contentType(MediaType.TEXT_PLAIN).body(publisher, String.class);
  }

  static class Message {
    final String payload;
    final ActorRef<String> replyTo;

    public Message(String payload, ActorRef<String> replyTo) {
      this.payload = payload;
      this.replyTo = replyTo;
    }
  }

  static class Questioner extends AbstractBehavior<Message> {
    private int count = 0;

    public static Behavior<Message> create() {
      return Behaviors.setup(Questioner::new);
    }

    private Questioner(ActorContext<Message> context) {
      super(context);
    }

    @Override
    public Receive<Message> createReceive() {
      return newReceiveBuilder().onMessage(Message.class, this::onAsk).build();
    }

    private Behavior<Message> onAsk(Message question) {
      question.replyTo.tell(
          String.format(
              "%s Answer %d, questioner %s",
              Thread.currentThread().getName(), ++count, question.payload));
//      question.replyTo.tell(" " + ++count);
      return this;
    }
  }
}
