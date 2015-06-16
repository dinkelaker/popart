///////////////////////////////////////////////////////////////////////////////
// Copyright 2008-2015, Technische Universitaet Darmstadt (TUD), Germany
//
// The TUD licenses this file to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.
///////////////////////////////////////////////////////////////////////////////
package de.tud.stg.example.interpreter.metamodel;

import java.util.ArrayList;

public abstract class ServiceProxy {
	
	protected String name = "";
	protected String endPoint = "";
	protected String category = "";
	
	public ServiceProxy(String name, String endPoint, String category) {
		this.name = name;
		this.endPoint = endPoint;
		this.category = category;
	}

	public String getName() {
		return name;
	}
	
	public String getEndPoint() {
		return endPoint;
	}
	
	public String getCategory() {
		return category;
	}
	
	public abstract Object call(String operation, ArrayList<Object> args);

}
