package com.example.springbootakkatyped;

import akka.actor.typed.javadsl.AbstractBehavior;
import org.springframework.context.ApplicationContext;

public class SpringActorProducer /*implements IndirectActorProducer*/ {

  private ApplicationContext applicationContext;

  private String beanActorName;

  public SpringActorProducer(ApplicationContext applicationContext,
                             String beanActorName) {
    this.applicationContext = applicationContext;
    this.beanActorName = beanActorName;
  }

  public AbstractBehavior<?> produce() {
    return (AbstractBehavior<?>) applicationContext.getBean(beanActorName);
  }

  public Class<? extends AbstractBehavior<?>> actorClass() {
    return (Class<? extends AbstractBehavior<?>>) applicationContext
        .getType(beanActorName);
  }
}

