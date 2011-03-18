package org.jboss.as.console.client.shared.dispatch.impl;

import org.jboss.as.console.client.shared.dispatch.Result;

public abstract class AbstractSimpleResult<T> implements Result {
  private T value;

  public AbstractSimpleResult(T value) {
    this.value = value;
  }

  protected AbstractSimpleResult() {
  }

  public T get() {
    return value;
  }

}
