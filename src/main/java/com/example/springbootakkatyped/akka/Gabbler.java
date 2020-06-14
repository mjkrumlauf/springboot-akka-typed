package com.example.springbootakkatyped.akka;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import com.example.springbootakkatyped.akka.ChatRoom.SessionEvent;

public class Gabbler {

  public static Behavior<SessionEvent> create() {
    return Behaviors.setup(ctx -> new Gabbler(ctx).behavior());
  }


  private final ActorContext<SessionEvent> context;

  Gabbler(ActorContext<SessionEvent> context) {
    this.context = context;
  }

  Behavior<SessionEvent> behavior() {
    return Behaviors.receive(SessionEvent.class)
        .onMessage(ChatRoom.SessionDenied.class, this::onSessionDenied)
        .onMessage(ChatRoom.SessionGranted.class, this::onSessionGranted)
        .onMessage(ChatRoom.MessagePosted.class, this::onMessagePosted)
        .build();
  }

  private Behavior<SessionEvent> onSessionDenied(ChatRoom.SessionDenied message) {
    context.getLog().info("cannot start chat room session: {}", message.reason);
    return Behaviors.stopped();
  }

  private Behavior<SessionEvent> onSessionGranted(ChatRoom.SessionGranted message) {
    message.handle.tell(new ChatRoom.PostMessage("Hello World!"));
    return Behaviors.same();
  }

  private Behavior<SessionEvent> onMessagePosted(ChatRoom.MessagePosted message) {
    context.getLog().info("message posted by '{}': {}", message.screenName, message.message);
    return Behaviors.stopped();
  }
}

