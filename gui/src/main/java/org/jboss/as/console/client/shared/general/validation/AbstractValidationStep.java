package org.jboss.as.console.client.shared.general.validation;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import org.jboss.as.console.client.shared.general.model.Interface;
import org.jboss.ballroom.client.widgets.forms.FormItem;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Heiko Braun
 * @date 11/16/11
 */
abstract class AbstractValidationStep<T> implements ValidationStep<T> {

    static Decision SUCCESS = new Decision<Interface>() {
        @Override
        public boolean evaluate(Interface entity) {
            return true;
        }
    };
    static Decision FAILURE = new Decision<Interface>() {
        @Override
        public boolean evaluate(Interface entity) {
            return false;
        }
    };

    private DecisionTree.DecisionLog decisionLog = null;

    public void setLog(DecisionTree.DecisionLog decisionLog) {
        this.decisionLog = decisionLog;
    }

    @Override
    public ValidationResult validate(T entity, Map<String, Object> changedValues) {

        DecisionTree<T> tree = buildDecisionTree(entity, changedValues);
        tree.setDecisionLog(decisionLog);

        //tree.outputBinTree();
        tree.queryBinTree();

        // create result
        ValidationResult result = new ValidationResult(tree.getFinalOutcome());
        result.addMessage(tree.getLastNode().getQuestOrAns());
        return result;
    }

    protected abstract DecisionTree<T> buildDecisionTree(T entity, Map<String,Object> changedValues);

    public static boolean isSet(String value)
    {
        return value!=null && !value.isEmpty();
    }

    protected static Map<String, Object> clearChangeset(Map<String, Object> changedValues) {

        Map<String,Object> clean = new HashMap<String, Object>();
        Set<String> keys = changedValues.keySet();
        for(String key : keys)
        {
            Object value = changedValues.get(key);
            if(! (value == FormItem.VALUE_SEMANTICS.UNDEFINED))
                clean.put(key, value);
        }
        return clean;
    }

    protected Map<String,Object> asProperties(T entity) {
        AutoBean<T> autoBean = AutoBeanUtils.getAutoBean(entity);
        if(null==autoBean)
            throw new RuntimeException("Not an auto bean: "+entity.getClass());

        return AutoBeanUtils.getAllProperties(autoBean);
    }

    protected static boolean isEmpty(Map<String, Object> changedValues) {

        changedValues.remove("name"); // default

        boolean empty = changedValues.isEmpty();

        if(!empty)
        {
            // treat any boolean=false as empty too
            // it will written as undefined
            boolean conflictingItem = false;
            Set<String> keys = changedValues.keySet();
            for(String key : keys)
            {
                Object value = changedValues.get(key);
                if(value instanceof Boolean)
                {
                    conflictingItem  = (Boolean)value; // any boolean=true values are considered changes
                    if(conflictingItem)
                    {
                        //System.out.println(key + " is conflicting");
                        break;
                    }
                }
                else if(value instanceof String)
                {
                    conflictingItem  = !((String) value).isEmpty(); // any non empty values are considered changes
                    if(conflictingItem)
                    {
                        //System.out.println(key + " is conflicting");
                        break;
                    }
                }
            }

            empty = !conflictingItem;
        }

        return empty;
    }
}
