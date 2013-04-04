package org.jboss.as.console.client.tools.mbui.workbench;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class ResetEvent extends GwtEvent<ResetEvent.ResetHandler> { 

  org.jboss.as.console.client.tools.mbui.workbench.repository.Sample sample;

  protected ResetEvent() {
    // Possibly for serialization.
  }

  public ResetEvent(org.jboss.as.console.client.tools.mbui.workbench.repository.Sample sample) {
    this.sample = sample;
  }

  public static void fire(HasHandlers source, org.jboss.as.console.client.tools.mbui.workbench.repository.Sample sample) {
    ResetEvent eventInstance = new ResetEvent(sample);
    source.fireEvent(eventInstance);
  }

  public static void fire(HasHandlers source, ResetEvent eventInstance) {
    source.fireEvent(eventInstance);
  }

  public interface HasResetHandlers extends HasHandlers {
    HandlerRegistration addResetHandler(ResetHandler handler);
  }

  public interface ResetHandler extends EventHandler {
    public void onReset(ResetEvent event);
  }

  private static final Type<ResetHandler> TYPE = new Type<ResetHandler>();

  public static Type<ResetHandler> getType() {
    return TYPE;
  }

  @Override
  public Type<ResetHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(ResetHandler handler) {
    handler.onReset(this);
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
    ResetEvent other = (ResetEvent) obj;
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
    return "ResetEvent["
                 + sample
    + "]";
  }
}
