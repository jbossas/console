package org.jboss.mbui.gui.behaviour;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Heiko Braun
 * @date 3/20/13
 */
class ParentDelegationContextImpl implements StatementScope.MutableContext{

    Map<String,String> delegate = new HashMap<String,String>();
    private final List<Integer> parentScopeIds;
    private final StatementContext externalContext;
    private final StatementContext localContext;
    private final StatementScope.Scopes availableScopes;

    public ParentDelegationContextImpl(StatementContext externalContext, List<Integer> parentScopeIds, StatementScope.Scopes scopes) {
        this.externalContext = externalContext;
        this.parentScopeIds = parentScopeIds;
        this.availableScopes = scopes;

        this.localContext = new StatementContext() {
            @Override
            public String resolve(String key) {
                return delegate.get(key);
            }

            @Override
            public String[] resolveTuple(String key) {
                return null; // doesn't support tuples
            }
        } ;
    }

    @Override
    public void setStatement(String key, String value) {
        delegate.put(key, value);
    }

    @Override
    public void clearStatement(String key) {
        delegate.remove(key);
    }

    @Override
    public String resolve(String key) {
        String resolvedValue = null;

        // local
        resolvedValue = localContext.resolve(key);

        // iterate delegates
        Iterator<Integer> delegateIds = parentScopeIds.iterator();
        while(null==resolvedValue && delegateIds.hasNext())
        {
            StatementContext delegationContext = availableScopes.get(delegateIds.next());
            if(delegationContext!=null) // may not be created yet, aka unused
            {
                resolvedValue = delegationContext.resolve(key);
            }
        }

        // last but not least: external context
        return resolvedValue == null ? externalContext.resolve(key) : resolvedValue;
    }

    @Override
    public String[] resolveTuple(String key) {
        String[] resolvedTuple = null;

        // local
        resolvedTuple = localContext.resolveTuple(key);

        // iterate delegates
        Iterator<Integer> delegateIds = parentScopeIds.iterator();
        while(null==resolvedTuple && delegateIds.hasNext())
        {
            StatementContext delegationContext = availableScopes.get(delegateIds.next());
            if(delegationContext!=null) // may not be created yet, aka unused
            {
                resolvedTuple = delegationContext.resolveTuple(key);
            }
        }

        // last but not least: external context
        return resolvedTuple == null ? externalContext.resolveTuple(key) : resolvedTuple;
    }
}
