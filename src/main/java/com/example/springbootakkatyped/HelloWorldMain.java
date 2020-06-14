//package com.example.springwebflux;
//
//import akka.actor.typed.ActorRef;
//import akka.actor.typed.Behavior;
//import akka.actor.typed.javadsl.AbstractBehavior;
//import akka.actor.typed.javadsl.ActorContext;
//import akka.actor.typed.javadsl.Behaviors;
//import akka.actor.typed.javadsl.Receive;
//
//public class HelloWorldMain extends AbstractBehavior<HelloWorldMain.SayHello> {
//
//  public static class SayHello {
//    public final String name;
//
//    public SayHello(String name) {
//      this.name = name;
//    }
//  }
//
//  public static Behavior<SayHello> create() {
//    return Behaviors.setup(HelloWorldMain::new);
//  }
//
//  private final ActorRef<HelloWorld.Greet> greeter;
//
//  public HelloWorldMain(ActorContext<SayHello> context) {
//    super(context);
//    greeter = context.spawn(HelloWorld.create(), "greeter");
//  }
//
//  @Override
//  public Receive<SayHello> createReceive() {
//    return newReceiveBuilder().onMessage(SayHello.class, this::onStart).build();
//  }
//
//  private Behavior<SayHello> onStart(SayHello command) {
//    ActorRef<HelloWorld.Greeted> replyTo =
//        getContext().spawn()
//  }
//}
