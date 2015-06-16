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

import java.lang.reflect.Field;

import org.codehaus.groovy.reflection.CachedField;

import de.tud.stg.popart.aspect.extensions.itd.InterTypeDeclarationMetaProperty;

import groovy.lang.MetaProperty;

/**
 * this class represents a meta property as a declared element
 * @author Joscha Drechsler
 */
public class MetaPropertyDeclaration implements PropertyDeclaration {
	/**
	 * the meta property
	 */
	public final MetaProperty mp;
	
	/**
	 * the parent element
	 */
	public final Declaration parent;

	/**
	 * constructor.
	 * @param declaredMetaProperty the meta property
	 */
	public MetaPropertyDeclaration(MetaProperty declaredMetaProperty){
		this.mp = declaredMetaProperty;
		if(mp instanceof CachedField){
			Field field = ((CachedField)mp).field;
			parent = new ClassDeclaration(field.getDeclaringClass());
		}else if(mp instanceof InterTypeDeclarationMetaProperty){
			parent = ((InterTypeDeclarationMetaProperty)mp).getInterTypePropertyDeclaration();
		}else{
			parent = null;
		}
	}
	
	/**
	 * a meta properties parent is the class that declared it.
	 */
	public Declaration getParent() {
		return parent;
	}

	public String toString(){
		return mp.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof MetaPropertyDeclaration) && mp.equals(((MetaPropertyDeclaration)obj).mp);
	}
	
	public MetaProperty getDeclaredMetaProperty() {
		return mp;
	}
}
