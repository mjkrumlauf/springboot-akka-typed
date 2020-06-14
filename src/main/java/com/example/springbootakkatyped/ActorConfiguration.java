package com.example.springbootakkatyped;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;
import akka.stream.Materializer;
import com.example.springbootakkatyped.HelloWorld.Greet;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ActorConfiguration {

  @Autowired
  private ApplicationContext applicationContext;

  private Materializer materializer;

  @Bean
  public ActorSystem<Greet> actorSystem(Behavior<Greet> actor) {
    ActorSystem<Greet> system = ActorSystem.create(actor, "hello");
    return system;
  }

  @Bean
  public Materializer materializer(ActorSystem<Greet> actorSystem) {
    return Materializer.createMaterializer(actorSystem);
  }

  @Bean
  public Behavior<Greet> helloWorld() {
    return Behaviors.setup(HelloWorld::new);
  }

  @Bean
  public Config akkaConfiguration() {
    return ConfigFactory.load();
  }
}