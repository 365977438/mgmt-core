package com.yoju360.mgmt.security;

import java.util.Collection;
import java.util.Iterator;

import org.springframework.expression.EvaluationContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.access.expression.ExpressionUtils;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;

public class AccessDecisionManager  implements org.springframework.security.access.AccessDecisionManager {
	private SecurityExpressionHandler<FilterInvocation> expressionHandler = new DefaultWebSecurityExpressionHandler();
	
	@Override
	public void decide(Authentication authentication, Object object,
			Collection<ConfigAttribute> configAttributes)
			throws AccessDeniedException, InsufficientAuthenticationException {
		if(configAttributes == null){
            return ;
        }
        Iterator<ConfigAttribute> ite=configAttributes.iterator();
        while(ite.hasNext()){
            ConfigAttribute ca=ite.next();
            if (ca instanceof SecurityConfig) {
	            String needRole=((SecurityConfig)ca).getAttribute();
	            for(GrantedAuthority ga:authentication.getAuthorities()){
	                if(needRole.equals(ga.getAuthority())){  //ga is user's role.
	                    return;
	                }
	            }
            } else {
                EvaluationContext ctx = expressionHandler.createEvaluationContext(authentication, (FilterInvocation)object);
                if (ExpressionUtils.evaluateAsBoolean(((WebExpressionConfigAttribute)ca).getAuthorizeExpression(), ctx))
                	return;
            }
        }
        throw new AccessDeniedException("no right");
	}

	@Override
	public boolean supports(ConfigAttribute attribute) {
		return true;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return true;
	}

}
