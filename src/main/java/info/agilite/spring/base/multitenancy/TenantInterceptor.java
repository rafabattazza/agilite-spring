package info.agilite.spring.base.multitenancy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import info.agilite.spring.base.security.SecurityUser;

public class TenantInterceptor implements HandlerInterceptor{
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if(SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null) {
			Object loggedUser = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			if (loggedUser != null && loggedUser instanceof SecurityUser) {
				SecurityUser user = (SecurityUser)loggedUser;
				if(user.getSchema() != null) {
					setSchema(user.getSchema());
				}
			}
		}
		
		return HandlerInterceptor.super.preHandle(request, response, handler);
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		TenantContext.clear();
		HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
	}
	
	private void setSchema(String schema) {
		TenantContext.setCurrentTenant(schema);
	}

}
