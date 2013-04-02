package org.jboss.as.console.client.tools.mbui.workbench;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class ActivateEvent extends GwtEvent<ActivateEvent.ActivateHandler> { 

  org.jboss.as.console.client.tools.mbui.workbench.repository.Sample sample;

  protected ActivateEvent() {
    // Possibly for serialization.
  }

  public ActivateEvent(org.jboss.as.console.client.tools.mbui.workbench.repository.Sample sample) {
    this.sample = sample;
  }

  public static void fire(HasHandlers source, org.jboss.as.console.client.tools.mbui.workbench.repository.Sample sample) {
    ActivateEvent eventInstance = new ActivateEvent(sample);
    source.fireEvent(eventInstance);
  }

  public static void fire(HasHandlers source, ActivateEvent eventInstance) {
    source.fireEvent(eventInstance);
  }

  public interface HasActivateHandlers extends HasHandlers {
    HandlerRegistration addActivateHandler(ActivateHandler handler);
  }

  public interface ActivateHandler extends EventHandler {
    public void onActivate(ActivateEvent event);
  }

  private static final Type<ActivateHandler> TYPE = new Type<ActivateHandler>();

  public static Type<ActivateHandler> getType() {
    return TYPE;
  }

  @Override
  public Type<ActivateHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(ActivateHandler handler) {
    handler.onActivate(this);
  }

  public org.jboss.as.console.client.tools.mbui.workbench.repository.Sample getSample(){
    return sample;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
        return true;
    if (obj == null)
        return false;
    if (getClass() != obj.getClass())
        return false;
    ActivateEvent other = (ActivateEvent) obj;
    if (sample == null) {
      if (other.sample != null)
        return false;
    } else if (!sample.equals(other.sample))
      return false;
    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 23;
    hashCode = (hashCode * 37) + (sample == null ? 1 : sample.hashCode());
    return hashCode;
  }

  @Override
  public String toString() {
    return "ActivateEvent["
                 + sample
    + "]";
  }
}
