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
import java.util.Map;
import java.util.Set;

public class DynamicRegistry {
	
	private static DynamicRegistry registry;
	public  Map<String, String> serviceToCartegory = new HashMap <String, String>();
	public  Map<String, Set<ServiceProxy>> categoryToService = new HashMap <String, HashSet<ServiceProxy>>();
	public static DynamicRegistry getInstance() {
		if (registry == null) {
			registry = new DynamicRegistry();
		}
		return registry;
	}

	public void register(ServiceProxy service) {
		def Set serviceList =new HashSet()
		def exist = false;
		serviceToCartegory.put(service.getEndPoint(), service.getCategory());
		if(categoryToService.size()>0){
			for(Iterator<String> iter = categoryToService.keySet().iterator();iter.hasNext();){
				 def currentCategory = (String)iter.next(); 
				if(currentCategory.equals(service.getCategory())){
					categoryToService.get(currentCategory).add(service);
					exist = true;
					break;
				}
			}
			if(!exist){
				serviceList.add(service);
				categoryToService.put(service.getCategory(), serviceList);
			}	
		}else {
		serviceList.add(service)
		categoryToService.put(service.getCategory(),serviceList);
		}
		
	}
	
	public void clear() {
	  serviceToCartegory = new HashMap <String, String>();
	  categoryToService = new HashMap <String, Set>();
	}
	
	/**
	 * Return a list of <code>Service</code> objects that belong to the <code>category</code>.
	 * @param category The category 
	 * @return
	 */
	public Set<ServiceProxy> find(String category) {
		Set<ServiceProxy> selected = new HashSet<ServiceProxy>();
		Iterator<ServiceProxy> it = categoryToService.keySet().iterator();
		while (it.hasNext()) {
			String currentCategory = (String)it.next();
		    if (currentCategory.equals(category)) {
		    	selected = categoryToService.get(currentCategory);
		    }
		}
		return selected;
	}
	
}
