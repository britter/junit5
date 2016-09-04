/*
 * Copyright 2015-2016 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.junit.platform.surefire.provider;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectJavaClass;
import static org.junit.platform.launcher.TagFilter.excludeTags;
import static org.junit.platform.launcher.TagFilter.includeTags;
import static org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder.request;

import java.util.function.Predicate;

import org.apache.maven.surefire.util.ScannerFilter;
import org.junit.platform.engine.Filter;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;

/**
 * @since 1.0
 */
final class TestPlanScannerFilter implements ScannerFilter {

	private static final Predicate<TestIdentifier> hasTests = testIdentifier -> testIdentifier.isTest()
			|| testIdentifier.isContainer();

	private final Launcher launcher;
	private final String[] groups;
	private final String[] excludedGroups;

	public TestPlanScannerFilter(final Launcher launcher, final String groups, final String excludedGroups) {
		this.launcher = launcher;
		this.groups = groups.isEmpty() ? new String[0] : groups.split(",");
		this.excludedGroups = excludedGroups.isEmpty() ? new String[0] : excludedGroups.split(",");
	}

	@Override
	@SuppressWarnings("rawtypes")
	public boolean accept(Class testClass) {
		LauncherDiscoveryRequest discoveryRequest = createLauncherDiscoveryRequest(testClass);
		TestPlan testPlan = launcher.discover(discoveryRequest);
		return testPlan.countTestIdentifiers(hasTests) > 0;
	}

	private LauncherDiscoveryRequest createLauncherDiscoveryRequest(final Class<?> testClass) {
		LauncherDiscoveryRequestBuilder builder = request().selectors(selectJavaClass(testClass));
		if (groups.length > 0) {
			builder = builder.filters(includeTags(groups));
		}
		if (excludedGroups.length > 0) {
			builder = builder.filters(excludeTags(excludedGroups));
		}
		return builder.build();
	}

}
