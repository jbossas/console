package org.jboss.as.console.client.shared.expr;

import org.jboss.as.console.client.widgets.forms.Binding;

/**
 * @author Heiko Braun
 * @date 10/4/11
 */
public interface HasExpression {

    @Binding(skip = true)
    Expression getExpression();
    void setExpression(Expression expr);
}
