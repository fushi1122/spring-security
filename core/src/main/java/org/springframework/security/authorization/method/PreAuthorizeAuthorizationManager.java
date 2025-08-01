/*
 * Copyright 2004-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.security.authorization.method;

import java.util.function.Supplier;

import org.aopalliance.intercept.MethodInvocation;
import org.jspecify.annotations.Nullable;

import org.springframework.context.ApplicationContext;
import org.springframework.expression.EvaluationContext;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AnnotationTemplateExpressionDefaults;

/**
 * An {@link AuthorizationManager} which can determine if an {@link Authentication} may
 * invoke the {@link MethodInvocation} by evaluating an expression from the
 * {@link PreAuthorize} annotation.
 *
 * @author Evgeniy Cheban
 * @since 5.6
 */
public final class PreAuthorizeAuthorizationManager
		implements AuthorizationManager<MethodInvocation>, MethodAuthorizationDeniedHandler {

	private PreAuthorizeExpressionAttributeRegistry registry = new PreAuthorizeExpressionAttributeRegistry();

	/**
	 * Sets the {@link MethodSecurityExpressionHandler}.
	 * @param expressionHandler the {@link MethodSecurityExpressionHandler} to use
	 */
	public void setExpressionHandler(MethodSecurityExpressionHandler expressionHandler) {
		this.registry.setExpressionHandler(expressionHandler);
	}

	/**
	 * Configure pre/post-authorization template resolution
	 * <p>
	 * By default, this value is <code>null</code>, which indicates that templates should
	 * not be resolved.
	 * @param defaults - whether to resolve pre/post-authorization templates parameters
	 * @since 6.4
	 */
	public void setTemplateDefaults(AnnotationTemplateExpressionDefaults defaults) {
		this.registry.setTemplateDefaults(defaults);
	}

	public void setApplicationContext(ApplicationContext context) {
		this.registry.setApplicationContext(context);
	}

	/**
	 * Determine if an {@link Authentication} has access to a method by evaluating an
	 * expression from the {@link PreAuthorize} annotation that the
	 * {@link MethodInvocation} specifies.
	 * @param authentication the {@link Supplier} of the {@link Authentication} to check
	 * @param mi the {@link MethodInvocation} to check
	 * @return an {@link AuthorizationDecision} or {@code null} if the
	 * {@link PreAuthorize} annotation is not present
	 */
	@Override
	public @Nullable AuthorizationResult authorize(Supplier<Authentication> authentication, MethodInvocation mi) {
		ExpressionAttribute attribute = this.registry.getAttribute(mi);
		if (attribute == null) {
			return null;
		}
		EvaluationContext ctx = this.registry.getExpressionHandler().createEvaluationContext(authentication, mi);
		return ExpressionUtils.evaluate(attribute.getExpression(), ctx);
	}

	@Override
	public @Nullable Object handleDeniedInvocation(MethodInvocation methodInvocation,
			AuthorizationResult authorizationResult) {
		ExpressionAttribute attribute = this.registry.getAttribute(methodInvocation);
		PreAuthorizeExpressionAttribute preAuthorizeAttribute = (PreAuthorizeExpressionAttribute) attribute;
		return preAuthorizeAttribute.getHandler().handleDeniedInvocation(methodInvocation, authorizationResult);
	}

}
