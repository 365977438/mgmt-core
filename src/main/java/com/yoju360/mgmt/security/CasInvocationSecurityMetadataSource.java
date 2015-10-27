package com.yoju360.mgmt.security;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 *  Reloadable security Metadata Source for <code>Resource</code> - web urls.
 * <p> 
 * Urls are configured in SysResource.
 * </p>
 * <p>
 * This class can be configured as a 'filter' to trigger reload.
 * </p>
 * 
 * @author evan
 *
 */
public class CasInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource,InitializingBean {
	private static final Logger logger = LoggerFactory.getLogger(CasInvocationSecurityMetadataSource.class);
	private static Map<String, Collection<ConfigAttribute>> resourceMap = null;
	@SuppressWarnings("unchecked")
	private Map<String, SpelExpression> spelMap = Collections.EMPTY_MAP;
    
    private Map<String, String> additionalConfigAttributes;
    private SpelExpressionParser spelPareser = new SpelExpressionParser(new SpelParserConfiguration(false, false));
   

	public static Map<String, Collection<ConfigAttribute>> getResourceMap() {
		return resourceMap;
	}

	public static void setResourceMap(Map<String, Collection<ConfigAttribute>> resourceMap) {
		CasInvocationSecurityMetadataSource.resourceMap = resourceMap;
	}


	@Override
	public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
		if (resourceMap == null) {
			// non existing role id, because no one logins in and no resourceMap is populated.
			return (Collection)Collections.singleton(new SecurityConfig("0"));
		}
		String url = ((FilterInvocation) object).getRequestUrl();
		Iterator<String> ite = resourceMap.keySet().iterator();
		while (ite.hasNext()) {
			String resURL = ite.next();
			if (resURL != null) {
				RequestMatcher urlMatcher = new AntPathRequestMatcher(resURL);
				if (urlMatcher.matches(((FilterInvocation) object).getHttpRequest())) {
					if ("/**".equals(resURL)) {
						while (ite.hasNext()) {
							String tmpResURL = ite.next();
							if (url.indexOf(tmpResURL.replaceAll("\\*", "")) != -1) {
								return resourceMap.get(tmpResURL);
							}
						}
					}
					return resourceMap.get(resURL);
				}
			}
		}

        for (String pattern : spelMap.keySet()) {
            AntPathRequestMatcher urlPathMatcher = new AntPathRequestMatcher(pattern);
            if (urlPathMatcher.matches(((FilterInvocation) object).getHttpRequest())) {
            	List<ConfigAttribute> configs = new LinkedList<ConfigAttribute>();
                configs.add(new WebExpressionConfigAttribute(spelMap.get(pattern)));
                return configs;
            }
        }
        
		logger.debug(object + " is public.");
		return Collections.emptyList();
	}

	
	public Map<String, String> getAdditionalConfigAttributes() {
		return additionalConfigAttributes;
	}

	public void setAdditionalConfigAttributes(
			Map<String, String> additionalConfigAttributes) {
		this.additionalConfigAttributes = additionalConfigAttributes;
	}

	@Override
	public Collection<ConfigAttribute> getAllConfigAttributes() {
		return null;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return true;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (additionalConfigAttributes != null) {
            spelMap = new HashMap<String, SpelExpression>();
            for (String url : additionalConfigAttributes.keySet()) {
                String config = additionalConfigAttributes.get(url);
                SpelExpression express = (SpelExpression)spelPareser.parseExpression(config);
                spelMap.put(url, express);
            }
        }
		//TODO do not verify SSL host
		javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
			    new javax.net.ssl.HostnameVerifier(){
			 
			        public boolean verify(String hostname,
			                javax.net.ssl.SSLSession sslSession) {
			            return true; 
			        }
			    });
	}

}
