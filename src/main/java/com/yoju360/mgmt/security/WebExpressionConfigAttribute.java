/**
 * 
 */
package com.yoju360.mgmt.security;

import org.springframework.expression.Expression;
import org.springframework.security.access.ConfigAttribute;


/**
 * 
 * @author Evan Wu
 *
 */
public class WebExpressionConfigAttribute implements ConfigAttribute {
    /**
     * 
     */
    private static final long serialVersionUID = -5552648290515572605L;
    private final Expression authorizeExpression;

    public WebExpressionConfigAttribute(Expression authorizeExpression) {
        this.authorizeExpression = authorizeExpression;
    }

    Expression getAuthorizeExpression() {
        return authorizeExpression;
    }

    public String getAttribute() {
        return null;
    }

    @Override
    public String toString() {
        return authorizeExpression.getExpressionString();
    }
}
