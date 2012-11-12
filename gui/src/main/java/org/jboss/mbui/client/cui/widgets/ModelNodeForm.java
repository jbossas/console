package org.jboss.mbui.client.cui.widgets;

import org.jboss.ballroom.client.widgets.forms.AbstractForm;
import org.jboss.ballroom.client.widgets.forms.EditListener;
import org.jboss.ballroom.client.widgets.forms.FormItem;
import org.jboss.dmr.client.ModelNode;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Heiko Braun
 * @date 11/12/12
 */
public class ModelNodeForm extends AbstractForm<ModelNode> {

    private ModelNode editedEntity = null;

    @Override
    public void edit(ModelNode bean) {

        // Needs to be declared (i.e. when creating new instances)
        if(null==bean)
            throw new IllegalArgumentException("Invalid entity: null");

        this.editedEntity = bean;

        final Map<String, String> exprMap = getExpressions(editedEntity);

        // visit form
        ModelNodeInspector inspector = new ModelNodeInspector(bean);
        inspector.accept(new ModelNodeVisitor()
        {

            private boolean isComplex = false;

            @Override
            public boolean visitValueProperty(
                    final String propertyName, final ModelNode value, final PropertyContext ctx) {

                if(isComplex ) return true; // skip complex types

                visitItem(propertyName, new FormItemVisitor() {

                    public void visit(FormItem item) {

                        item.resetMetaData();

                        // expressions
                        String exprValue = exprMap.get(propertyName);
                        if(exprValue!=null)
                        {
                            item.setUndefined(false);
                            item.setExpressionValue(exprValue);
                        }

                        // values
                        else if(value!=null)
                        {
                            item.setUndefined(false);
                            item.setValue(value);
                        }
                        else
                        {
                            item.setUndefined(true);
                            item.setModified(true); // don't escape validation
                        }
                    }
                });

                return true;
            }


            @Override
            public boolean visitReferenceProperty(String propertyName, ModelNode value, PropertyContext ctx) {
                isComplex = true;
                return true;
            }

            @Override
            public void endVisitReferenceProperty(String propertyName, ModelNode value, PropertyContext ctx) {
                isComplex = false;
            }

            @Override
            public boolean visitCollectionProperty(String propertyName, final ModelNode value, PropertyContext ctx) {
                visitItem(propertyName, new FormItemVisitor() {

                    public void visit(FormItem item) {

                        item.resetMetaData();

                        if(value!=null)
                        {
                            item.setUndefined(false);
                            //TODO: item.setValue(value.asList());
                            item.setValue(Collections.EMPTY_LIST);
                        }
                        else
                        {
                            item.setUndefined(true);
                            item.setModified(true); // don't escape validation
                        }
                    }
                });

                return true;
            }
        });

        // plain views
        refreshPlainView();
    }

    void visitItem(final String name, FormItemVisitor visitor) {
        String namePrefix = name + "_";
        for(Map<String, FormItem> groupItems : formItems.values())
        {
            for(String key : groupItems.keySet())
            {
                if(key.equals(name) || key.startsWith(namePrefix))
                {
                    visitor.visit(groupItems.get(key));
                }
            }
        }
    }

    // TODO: Fix me...
    private Map<String, String> getExpressions(ModelNode bean) {
        return new HashMap<String,String>();
    }

    @Override
    public void cancel() {
        clearValues();
        if(editedEntity!=null) edit(editedEntity);
    }

    @Override
    public Map<String, Object> getChangedValues() {
        return null;
    }

    @Override
    public ModelNode getUpdatedEntity() {
        return null;
    }

    @Override
    public ModelNode getEditedEntity() {
        return editedEntity;
    }

    @Override
    public void clearValues() {

    }

    interface FormItemVisitor {
        void visit(FormItem item);
    }


    // ---- deprecated, blow up -----

    @Override
    public Class<?> getConversionType() {
        throw new RuntimeException("API Incompatible: getConversionType() not supported on "+getClass().getName());
    }

    @Override
    public void addEditListener(EditListener listener) {
        throw new RuntimeException("API Incompatible: addEditListener() not supported on "+getClass().getName());
    }

    @Override
    public void removeEditListener(EditListener listener) {
        throw new RuntimeException("API Incompatible: removeEditListener() not supported on "+getClass().getName());
    }
}
