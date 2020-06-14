package com.example.springbootakkatyped;

import reactor.core.publisher.Flux;

public class ReactorMain {
  public static void main(String[] args) {
      ReactorMain reactorMain = new ReactorMain();
      reactorMain.execute();
  }

  private int getDataToBePublished() {
    System.out.println(Thread.currentThread().getName() + " getDataToBePublished called");
      return 1;
  }

  private void execute() {
    Flux.fromArray(new Integer[] { getDataToBePublished() } )
        .subscribe(i -> System.out.println(Thread.currentThread().getName() + " Observer-1: " + i));
  }
}
