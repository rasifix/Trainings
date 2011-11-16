/* 
 * Copyright 2010 Simon Raess
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.rasifix.trainings.format.tcx.internal.model;

public class Version {
	
	private final Short versionMajor;
	private final Short versionMinor;
	private final Short buildMajor;
	private final Short buildMinor;

	public Version(final Short versionMajor, final Short versionMinor, final Short buildMajor, final Short buildMinor) {
		this.versionMajor = versionMajor;
		this.versionMinor = versionMinor;
		this.buildMajor = buildMajor;
		this.buildMinor = buildMinor;
	}
	
	public Short getVersionMajor() {
		return versionMajor;
	}
	
	public Short getVersionMinor() {
		return versionMinor;
	}
	
	public Short getBuildMajor() {
		return buildMajor;
	}
	
	public Short getBuildMinor() {
		return buildMinor;
	}
	
}
