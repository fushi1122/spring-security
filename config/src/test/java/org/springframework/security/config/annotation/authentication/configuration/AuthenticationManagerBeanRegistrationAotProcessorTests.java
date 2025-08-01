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

package org.springframework.security.config.annotation.authentication.configuration;

import org.junit.jupiter.api.Test;

import org.springframework.aop.SpringProxy;
import org.springframework.aop.framework.Advised;
import org.springframework.aot.generate.GenerationContext;
import org.springframework.aot.hint.predicate.RuntimeHintsPredicates;
import org.springframework.aot.test.generate.TestGenerationContext;
import org.springframework.beans.factory.aot.BeanRegistrationAotContribution;
import org.springframework.beans.factory.aot.BeanRegistrationCode;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RegisteredBean;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.DecoratingProxy;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link AuthenticationManagerBeanRegistrationAotProcessor}
 *
 * @author Marcus da Coregio
 */
class AuthenticationManagerBeanRegistrationAotProcessorTests {

	private final AuthenticationManagerBeanRegistrationAotProcessor processor = new AuthenticationManagerBeanRegistrationAotProcessor();

	private final GenerationContext generationContext = new TestGenerationContext();

	@Test
	void shouldSkipWhenInterfaceNotImplemented() {
		process(NoAuthenticationManager.class);
		assertThat(this.generationContext.getRuntimeHints().proxies().jdkProxyHints()).isEmpty();
	}

	@Test
	void shouldProcessWhenImplementsInterface() {
		process(MyAuthenticationManager.class);
		assertThat(RuntimeHintsPredicates.proxies()
			.forInterfaces(AuthenticationManager.class, SpringProxy.class, Advised.class, DecoratingProxy.class))
			.accepts(this.generationContext.getRuntimeHints());
	}

	@Test
	void shouldProcessWhenSuperclassImplementsInterface() {
		process(ChildAuthenticationManager.class);
		assertThat(RuntimeHintsPredicates.proxies()
			.forInterfaces(AuthenticationManager.class, SpringProxy.class, Advised.class, DecoratingProxy.class))
			.accepts(this.generationContext.getRuntimeHints());
	}

	private void process(Class<?> beanClass) {
		BeanRegistrationAotContribution contribution = createContribution(beanClass);
		if (contribution != null) {
			contribution.applyTo(this.generationContext, mock(BeanRegistrationCode.class));
		}
	}

	@Nullable
	private BeanRegistrationAotContribution createContribution(Class<?> beanClass) {
		DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
		beanFactory.registerBeanDefinition(beanClass.getName(), new RootBeanDefinition(beanClass));
		return this.processor.processAheadOfTime(RegisteredBean.of(beanFactory, beanClass.getName()));
	}

	static class NoAuthenticationManager {

	}

	static class MyAuthenticationManager implements AuthenticationManager {

		@Override
		public Authentication authenticate(Authentication authentication) throws AuthenticationException {
			return null;
		}

	}

	static class ChildAuthenticationManager extends MyAuthenticationManager {

	}

}
