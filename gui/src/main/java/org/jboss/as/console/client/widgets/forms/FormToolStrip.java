package org.jboss.as.console.client.widgets.forms;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.ballroom.client.widgets.InlineLink;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormAdapter;
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

    private FormAdapter<T> form = null;
    private FormCallback<T> callback;
    private String deleteOpName = null;
    private boolean providesDeleteOp = false;
    private boolean providesEditSaveOp = true;
    private List<ToolButton> additionalButtons = new LinkedList<ToolButton>();

    private InlineLink cancelBtn = null;
    private ToolButton editBtn = null;

    private PreValidation preValidation = null;

    public interface PreValidation {
        boolean isValid();
    }


    public FormToolStrip(FormAdapter<T> form, FormCallback<T> callback) {
        this.form = form;
        this.callback = callback;
    }

    public FormToolStrip(Form<T> form, FormCallback<T> callback, String deleteOpName) {
        this.form = form;
        this.callback = callback;
        this.deleteOpName = deleteOpName;
    }

    public void setPreValidation(PreValidation preValidation) {
        this.preValidation = preValidation;
    }

    public void providesDeleteOp(boolean b) {
        this.providesDeleteOp = b;
    }

    public void providesEditSaveOp(boolean b) {
        this.providesEditSaveOp = b;
    }

    public Widget asWidget() {

        ToolStrip toolStrip = new ToolStrip();
        if (providesEditSaveOp) {
            editBtn = new ToolButton(Console.CONSTANTS.common_label_edit());
            ClickHandler editHandler = new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {

                    if(null == form.getEditedEntity())
                    {
                        Log.warn("Empty form!");
                        return;
                    }

                    if(editBtn.getText().equals(Console.CONSTANTS.common_label_edit()))
                    {
                        editBtn.setText(Console.CONSTANTS.common_label_save());
                        form.setEnabled(true);
                        cancelBtn.setVisible(true);
                    }
                    else
                    {

                        if(!form.validate().hasErrors())
                        {
                            boolean preValidationIsSuccess = preValidation != null && preValidation.isValid();
                            if(preValidation==null || preValidationIsSuccess)
                            {
                                cancelBtn.setVisible(false);
                                editBtn.setText(Console.CONSTANTS.common_label_edit());
                                form.setEnabled(false);
                                Map<String, Object> changedValues = form.getChangedValues();
                                if(!changedValues.isEmpty())
                                    callback.onSave(changedValues);
                                else
                                    Log.warn("Empty changeset!");
                            }
                        }
                    }

                }
            };
            editBtn.addClickHandler(editHandler);
            toolStrip.addToolButton(editBtn);
        }
        editBtn.ensureDebugId(Console.DEBUG_CONSTANTS.debug_label_edit_formToolStrip());

        for(ToolButton btn : additionalButtons)
            toolStrip.addToolButtonRight(btn);

        if(providesDeleteOp)
        {
            ClickHandler clickHandler = new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {

                    if(null == form.getEditedEntity()) return;


                    Feedback.confirm(
                            Console.MESSAGES.deleteTitle(Console.CONSTANTS.common_label_item()),
                            Console.MESSAGES.deleteConfirm(Console.CONSTANTS.common_label_item()),
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
            deleteBtn.ensureDebugId(Console.DEBUG_CONSTANTS.debug_label_delete_formToolStrip());
            deleteBtn.addClickHandler(clickHandler);
            toolStrip.addToolButtonRight(deleteBtn);

        }

        final ClickHandler cancelHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
               doCancel();
            }
        };

        cancelBtn = new InlineLink(Console.CONSTANTS.common_label_cancel());
        cancelBtn.addClickHandler(cancelHandler);
        toolStrip.addToolWidget(cancelBtn);

        cancelBtn.ensureDebugId(Console.DEBUG_CONSTANTS.debug_label_cancel_formToolStrip());
        cancelBtn.setVisible(false);
        return toolStrip;
    }

    public void addToolButtonRight(ToolButton btn) {
        additionalButtons.add(btn);
    }

    public interface FormCallback<T> {
        void onSave(Map<String, Object> changeset);
        void onDelete(T entity);
    }

    public HasClickHandlers getCancelButton() {
        return cancelBtn;
    }

    public void doCancel() {
        form.cancel();
        if (editBtn != null)
            editBtn.setText(Console.CONSTANTS.common_label_edit());
        form.setEnabled(false);
        cancelBtn.setVisible(false);
    }
}
