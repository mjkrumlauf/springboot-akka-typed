package com.example.springbootakkatyped;

import akka.NotUsed;
import akka.actor.typed.ActorSystem;
import akka.event.LoggingAdapter;
import akka.stream.javadsl.Source;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

@RestController
public class GreetingController {

  @Autowired private ActorSystem<?> system;

  @PostConstruct
  public void setup() {
    LoggingAdapter log = system.classicSystem().log();
    log.info("Injected ActorSystem Name -> {}", system.name());
  }

  @GetMapping(value = "/alpakka", produces = MediaType.TEXT_PLAIN_VALUE)
  public Source<String, NotUsed> alpakka(@RequestParam("name") String name) {
    return Source.repeat(name)
                 .intersperse("\n")
                 .take(10);
  }

  @GetMapping("/index")
  @ResponseBody
  public String index() {
    return Thread.currentThread().getName() +  " Hello, Reactive Spring!";
  }
}
