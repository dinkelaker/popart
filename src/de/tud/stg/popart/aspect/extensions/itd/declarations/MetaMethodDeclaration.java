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
package de.tud.stg.popart.aspect.extensions.itd.declarations;

import de.tud.stg.popart.aspect.extensions.itd.InterTypeDeclarationMetaMethod;
import groovy.lang.MetaMethod;

/**
 * This class wraps a meta method as a declared element
 * @author Joscha Drechsler
 */
public class MetaMethodDeclaration implements MethodDeclaration {
	/**
	 * the meta method
	 */
	public final MetaMethod mm;
	
	/**
	 * the methods parent
	 */
	public final Declaration parent;

	/**
	 * constructor.
	 * @param declaredMetaMethod the meta method
	 */
	public MetaMethodDeclaration(MetaMethod declaredMetaMethod){
		this.mm = declaredMetaMethod;
		if(declaredMetaMethod instanceof InterTypeDeclarationMetaMethod){
			parent = ((InterTypeDeclarationMetaMethod)declaredMetaMethod).getInterTypeMethodDeclaration();
		}else{
			parent = new ClassDeclaration(mm.getDeclaringClass().getTheClass());
		}
	}
	
	/**
	 * a closures parent is the class which declared it.
	 */
	public Declaration getParent() {
		return parent;
	}

	public String toString(){
		return mm.toString();
	}
	
	@Override
	public int hashCode() {
		return mm.hashCode()+123;
	}
	
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof MetaMethodDeclaration) && mm.equals(((MetaMethodDeclaration)obj).mm);
	}
	
	public MetaMethod getDeclaredMetaMethod(Class<?>[] args) {
		if(mm.isValidMethod(args)){
			return mm;
		}else{
			return null;
		}
	}
}
