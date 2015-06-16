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
package de.tud.stg.example.aosd2010.process.domainmodel;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.tud.stg.example.aosd2010.casestudy3.SecureBankingServiceProxy;
import de.tud.stg.example.aosd2010.process.domainmodel.Registry;
import de.tud.stg.example.aosd2010.process.domainmodel.ServiceProxy;

public class Registry {
	
	private static Registry registry;
	
	private Set<ServiceProxy> services = new HashSet<ServiceProxy>();
	
	public static Registry getInstance() {
		if (registry == null) {
			registry = new Registry();
		}
		return registry;
	}

	public void register(ServiceProxy service) {
		services.add(service);
	}
	
	public void clear() {
		services = new HashSet<ServiceProxy>();
	}
	
	/**
	 * Return a list of <code>Service</code> objects that belong to the <code>category</code>.
	 * @param category The category 
	 * @return
	 */
	public Collection<ServiceProxy> find(String category) {
		Collection<ServiceProxy> selected = new HashSet<ServiceProxy>();
		
		Iterator<ServiceProxy> it = services.iterator();
		while (it.hasNext()) {
			ServiceProxy service = it.next();
		    if (service.getCategory().equals(category)) {
		    	selected.add(service);
		    }
		}
		
		return selected;
	}
}
