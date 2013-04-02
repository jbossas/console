package org.jboss.as.console.client.tools.mbui.workbench;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class ReifyEvent extends GwtEvent<ReifyEvent.ReifyHandler> { 

  org.jboss.as.console.client.tools.mbui.workbench.repository.Sample sample;

  protected ReifyEvent() {
    // Possibly for serialization.
  }

  public ReifyEvent(org.jboss.as.console.client.tools.mbui.workbench.repository.Sample sample) {
    this.sample = sample;
  }

  public static void fire(HasHandlers source, org.jboss.as.console.client.tools.mbui.workbench.repository.Sample sample) {
    ReifyEvent eventInstance = new ReifyEvent(sample);
    source.fireEvent(eventInstance);
  }

  public static void fire(HasHandlers source, ReifyEvent eventInstance) {
    source.fireEvent(eventInstance);
  }

  public interface HasReifyHandlers extends HasHandlers {
    HandlerRegistration addReifyHandler(ReifyHandler handler);
  }

  public interface ReifyHandler extends EventHandler {
    public void onReify(ReifyEvent event);
  }

  private static final Type<ReifyHandler> TYPE = new Type<ReifyHandler>();

  public static Type<ReifyHandler> getType() {
    return TYPE;
  }

  @Override
  public Type<ReifyHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(ReifyHandler handler) {
    handler.onReify(this);
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
    ReifyEvent other = (ReifyEvent) obj;
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
    return "ReifyEvent["
                 + sample
    + "]";
  }
}
