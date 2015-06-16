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

import org.codehaus.groovy.runtime.GeneratedClosure;

import groovy.lang.Closure;

/**
 * This class wraps a closure as a declared element.
 * Only {@link GeneratedClosure}s are accepted as declared
 * elements!
 * @author Joscha Drechsler
 */
public class ClosureDeclaration implements Declaration {
	/**
	 * the wrapped closure
	 */
	public final Closure closure;
	/**
	 * the precomputed closures parent
	 */
	public final Declaration parent;
	
	/**
	 * constructor.
	 * @param declarator the declared closure
	 */
	public ClosureDeclaration(GeneratedClosure declarator){
		closure = (Closure)declarator;
		Object owner = closure.getOwner();
		if(owner instanceof GeneratedClosure){
			parent = new ClosureDeclaration((GeneratedClosure)owner);
		}else{
			parent = new ClassDeclaration(owner.getClass());
		}
	}
	
	/**
	 * a {@link GeneratedClosure}s parent is its surrounding closure
	 * (if this is generated aswell), otherwise it is its owners class.
	 */
	public Declaration getParent() {
		return parent;
	}
	
	public String toString(){
		return closure.toString();
	}
	
	@Override
	public int hashCode() {
		return closure.hashCode()+123;
	}
	
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof ClosureDeclaration) && closure.equals(((ClosureDeclaration)obj).closure);
	}
	
	public Closure getClosure() {
		return closure;
	}
}
