package com.example.springbootakkatyped.akka;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.Terminated;
import akka.actor.typed.javadsl.Behaviors;
import com.example.springbootakkatyped.akka.ChatRoom.GetSession;
import com.example.springbootakkatyped.akka.ChatRoom.PublishSessionMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class SpringBootAkkaMain {

  public static void main(String[] args) {
    SpringApplication app = new SpringApplication();
    app.run(SpringBootAkkaMain.class, args);
  }

  private ActorSystem<Void> actorSystem;

  private ActorRef<ChatRoom.RoomCommand> chatRoom;

  @Autowired private Behavior<ChatRoom.RoomCommand> chatRoomBehavior;

  @Autowired private Behavior<ChatRoom.SessionEvent> gabblerBehavior;

  @Autowired private Behavior<ChatRoom.SessionEvent> gabblerBehavior2;

  @Autowired private Behavior<ChatRoom.SessionEvent> gabblerBehavior3;

  @EventListener(ApplicationReadyEvent.class)
  void onApplicationReady() {
    actorSystem = ActorSystem.create(execute(), "ChatRoomSystem");

    Runtime.getRuntime().addShutdownHook(new Thread(() -> actorSystem.terminate()));
  }

  private Behavior<Void> execute() {

    return Behaviors.setup(
        context -> {
          chatRoom = context.spawn(chatRoomBehavior, ChatRoom.class.getName());

          ActorRef<ChatRoom.SessionEvent> gabbler = context.spawn(gabblerBehavior, "Gabbler");
          context.watch(gabbler);
          chatRoom.tell(new GetSession("Gabbler", gabbler));
          chatRoom.tell(new PublishSessionMessage("Gabbler", "Hello to Gabbler"));

          ActorRef<ChatRoom.SessionEvent> gabbler2 = context.spawn(gabblerBehavior2, "Gabbler2");
          context.watch(gabbler2);
          chatRoom.tell(new GetSession("Gabbler2", gabbler2));
          chatRoom.tell(new PublishSessionMessage("Gabbler2", "Hello to Gabbler2"));

          ActorRef<ChatRoom.SessionEvent> gabbler3 = context.spawn(gabblerBehavior3, "Gabbler3");
          context.watch(gabbler3);

          chatRoom.tell(new GetSession("Gabbler3", gabbler3));
          chatRoom.tell(new PublishSessionMessage("Gabbler3", "Hello to Gabbler3"));

          return Behaviors.receive(Void.class)
              .onSignal(Terminated.class, sig -> Behaviors.stopped())
              .build();
        });
  }
}
