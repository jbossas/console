package org.jboss.as.console.client.widgets.forms;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.ballroom.client.widgets.window.Feedback;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Heiko Braun
 * @date 9/19/11
 */
public class FormToolStrip<T> {

    private Form<T> form = null;
    private FormCallback<T> callback;
    private String deleteOpName = null;
    private boolean providesDeleteOp = true;
    private List<ToolButton> additionalButtons = new LinkedList<ToolButton>();

    public FormToolStrip(Form<T> form, FormCallback<T> callback) {
        this.form = form;
        this.callback = callback;
    }

    public FormToolStrip(Form<T> form, FormCallback<T> callback, String deleteOpName) {
        this.form = form;
        this.callback = callback;
        this.deleteOpName = deleteOpName;
    }

    public void providesDeleteOp(boolean b) {
        this.providesDeleteOp = b;
    }
    public Widget asWidget() {

        ToolStrip toolStrip = new ToolStrip();
        final ToolButton editBtn = new ToolButton(Console.CONSTANTS.common_label_edit());
        ClickHandler editHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

                if(null == form.getEditedEntity())
                    return;

                if(editBtn.getText().equals(Console.CONSTANTS.common_label_edit()))
                {
                    editBtn.setText(Console.CONSTANTS.common_label_save());
                    form.setEnabled(true);
                }
                else
                {
                    editBtn.setText(Console.CONSTANTS.common_label_edit());
                    form.setEnabled(false);
                    Map<String, Object> changedValues = form.getChangedValues();
                    if(!changedValues.isEmpty())
                        callback.onSave(changedValues);
                }

            }
        };
        editBtn.addClickHandler(editHandler);
        toolStrip.addToolButton(editBtn);

        if(providesDeleteOp)
        {
            ClickHandler clickHandler = new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {

                    String action = deleteOpName != null ? deleteOpName : Console.CONSTANTS.common_label_delete();

                    Feedback.confirm(
                            action +" Item",
                            "Really "+action+" this item?",
                            new Feedback.ConfirmationHandler() {
                                @Override
                                public void onConfirmation(boolean isConfirmed) {
                                    if (isConfirmed) {
                                        callback.onDelete(form.getEditedEntity());
                                    }
                                }
                            });
                }
            };

            String title = deleteOpName!=null ? deleteOpName : Console.CONSTANTS.common_label_delete();
            ToolButton deleteBtn = new ToolButton(title);
            deleteBtn.addClickHandler(clickHandler);
            toolStrip.addToolButton(deleteBtn);

        }

        for(ToolButton btn : additionalButtons)
            toolStrip.addToolButtonRight(btn);

        return toolStrip;
    }

    public void addToolButtonRight(ToolButton btn) {
        additionalButtons.add(btn);
    }

    public interface FormCallback<T> {
        void onSave(Map<String, Object> changeset);
        void onDelete(T entity);
    }
}
