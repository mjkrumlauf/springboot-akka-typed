package com.example.springbootakkatyped.akka;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

@Configuration
public class SpringConfig {

  @PrototypeBehaviorBean
  public Behavior<ChatRoom.SessionEvent> gabbler() {
    return Behaviors.setup(ctx -> new Gabbler(ctx).behavior());
  }

  @PrototypeBehaviorBean
  public Behavior<ChatRoom.RoomCommand> chatRoom() {
    return Behaviors.setup(ctx -> new ChatRoom(ctx).chatRoom(new ArrayList<>()));
  }
}
