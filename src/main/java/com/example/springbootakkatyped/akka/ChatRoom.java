package com.example.springbootakkatyped.akka;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ChatRoom {

  interface RoomCommand {};

  public static final class GetSession implements RoomCommand {
    public final String screenName;
    public final ActorRef<SessionEvent> replyTo;

    public GetSession(String screenName, ActorRef<SessionEvent> replyTo) {
      this.screenName = screenName;
      this.replyTo = replyTo;
    }
  }

  public static final class PublishSessionMessage implements RoomCommand {
    public final String screenName;
    public final String message;

    public PublishSessionMessage(String screenName, String message) {
      this.screenName = screenName;
      this.message = message;
    }
  }

  interface SessionEvent {}

  public static final class SessionGranted implements SessionEvent {
    public final ActorRef<PostMessage> handle;

    public SessionGranted(ActorRef<PostMessage> handle) {
      this.handle = handle;
    }
  }

  public static final class SessionDenied implements SessionEvent {
    public final String reason;

    public SessionDenied(String reason) {
      this.reason = reason;
    }
  }

  public static final class MessagePosted implements SessionEvent {
    public final String screenName;
    public final String message;

    public MessagePosted(String screenName, String message) {
      this.screenName = screenName;
      this.message = message;
    }
  }

  interface SessionCommand {}

  public static final class PostMessage implements SessionCommand {
    public final String message;

    public PostMessage(String message) {
      this.message = message;
    }
  }

  private static final class NotifyClient implements SessionCommand {
    final MessagePosted message;

    public NotifyClient(MessagePosted message) {
      this.message = message;
    }
  }

  static class Session {
    static Behavior<ChatRoom.SessionCommand> create(
        ActorRef<RoomCommand> room, String screenName, ActorRef<SessionEvent> client) {
      return Behaviors.receive(ChatRoom.SessionCommand.class)
          .onMessage(PostMessage.class, post -> onPostMessage(room, screenName, post))
          .onMessage(NotifyClient.class, notification -> onNotifyClient(client, notification))
          .build();
    }

    private static Behavior<SessionCommand> onPostMessage(
        ActorRef<RoomCommand> room, String screenName, PostMessage post) {
      // from client, publish to others via the room
      room.tell(new PublishSessionMessage(screenName, post.message));
      return Behaviors.same();
    }

    private static Behavior<SessionCommand> onNotifyClient(
        ActorRef<SessionEvent> client, NotifyClient notification) {
      // published from the room
      client.tell(notification.message);
      return Behaviors.same();
    }
  }

  public static Behavior<RoomCommand> create() {
    return Behaviors.setup(ctx -> new ChatRoom(ctx).chatRoom(new ArrayList<>()));
  }

  private final ActorContext<RoomCommand> context;

  ChatRoom(ActorContext<RoomCommand> context) {
    this.context = context;
  }

  Behavior<RoomCommand> chatRoom(List<ActorRef<SessionCommand>> sessions) {
    return Behaviors.receive(RoomCommand.class)
        .onMessage(GetSession.class, getSession -> onGetSession(sessions, getSession))
        .onMessage(PublishSessionMessage.class, pub -> onPublishSessionMessage(sessions, pub))
        .build();
  }

  private Behavior<RoomCommand> onGetSession(
      List<ActorRef<SessionCommand>> sessions, GetSession getSession)
      throws UnsupportedEncodingException {
    ActorRef<SessionEvent> client = getSession.replyTo;
    ActorRef<SessionCommand> ses =
        context.spawn(
            Session.create(context.getSelf(), getSession.screenName, client),
            URLEncoder.encode(getSession.screenName, StandardCharsets.UTF_8.name()));

    client.tell(new SessionGranted(ses.narrow()));

    List<ActorRef<SessionCommand>> newSessions = new ArrayList<>(sessions);
    newSessions.add(ses);
    return chatRoom(newSessions);
  }

  private Behavior<RoomCommand> onPublishSessionMessage(
      List<ActorRef<SessionCommand>> sessions, PublishSessionMessage pub) {
    NotifyClient notification = new NotifyClient(new MessagePosted(pub.screenName, pub.message));
    sessions.forEach(s -> s.tell(notification));
    return Behaviors.same();
  }
}
