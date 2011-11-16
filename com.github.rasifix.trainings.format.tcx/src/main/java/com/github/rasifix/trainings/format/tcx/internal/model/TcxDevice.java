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

public class TcxDevice extends TcxCreator {
	
	private String unitId;
	
	private String productId;
	
	private Version version;
	
	public String getUnitId() {
		return unitId;
	}
	
	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}
	
	public String getProductId() {
		return productId;
	}
	
	public void setProductId(String productId) {
		this.productId = productId;
	}
	
	public Version getVersion() {
		return version;
	}
	
	public void setVersion(Version version) {
		this.version = version;
	}
	
}
