package com.example.springbootakkatyped;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class Streamer extends AbstractBehavior<Streamer.Protocol> {

  static class Ack {}

  interface Protocol {}

  static class Init implements Protocol {
    private final ActorRef<Ack> ack;

    public Init(ActorRef<Ack> ack) {
      this.ack = ack;
    }
  }

  static class Message implements Protocol {
    private final ActorRef<Ack> ackTo;
    private final String msg;

    public Message(ActorRef<Ack> ackTo, String msg) {
      this.ackTo = ackTo;
      this.msg = msg;
    }
  }

  static class MessageWithoutAck implements Protocol {
    private final String msg;

    public MessageWithoutAck(String msg) {
      this.msg = msg;
    }
  }

  static class Complete implements Protocol {}

  static class Fail implements Protocol {
    private final Throwable t;

    public Fail(Throwable t) {
      this.t = t;
    }
  }

  public static Behavior<Protocol> create() {
    return Behaviors.setup(Streamer::new);
  }

  private Streamer(ActorContext<Protocol> context) {
    super(context);
  }

  @Override
  public Receive<Protocol> createReceive() {
    return newReceiveBuilder().onMessage(Protocol.class, this::onProtocol).build();
  }

  private Behavior<Protocol> onProtocol(Protocol msg) {
    System.out.println(msg.getClass().getName());
    return this;
  }
}
