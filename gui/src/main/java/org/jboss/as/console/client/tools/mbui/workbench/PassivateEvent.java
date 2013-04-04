package org.jboss.as.console.client.tools.mbui.workbench;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class PassivateEvent extends GwtEvent<PassivateEvent.PassivateHandler> { 

  org.jboss.as.console.client.tools.mbui.workbench.repository.Sample sample;

  protected PassivateEvent() {
    // Possibly for serialization.
  }

  public PassivateEvent(org.jboss.as.console.client.tools.mbui.workbench.repository.Sample sample) {
    this.sample = sample;
  }

  public static void fire(HasHandlers source, org.jboss.as.console.client.tools.mbui.workbench.repository.Sample sample) {
    PassivateEvent eventInstance = new PassivateEvent(sample);
    source.fireEvent(eventInstance);
  }

  public static void fire(HasHandlers source, PassivateEvent eventInstance) {
    source.fireEvent(eventInstance);
  }

  public interface HasPassivateHandlers extends HasHandlers {
    HandlerRegistration addPassivateHandler(PassivateHandler handler);
  }

  public interface PassivateHandler extends EventHandler {
    public void onPassivate(PassivateEvent event);
  }

  private static final Type<PassivateHandler> TYPE = new Type<PassivateHandler>();

  public static Type<PassivateHandler> getType() {
    return TYPE;
  }

  @Override
  public Type<PassivateHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(PassivateHandler handler) {
    handler.onPassivate(this);
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
    PassivateEvent other = (PassivateEvent) obj;
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
    return "PassivateEvent["
                 + sample
    + "]";
  }
}
