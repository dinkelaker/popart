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
package de.tud.stg.popart.dslsupport.sql.model

/**
 * <p>A qualified name consists of a name and an optional qualifier which are separated by a dot.</p>
 * <p><b>Examples</b><br />
 * id<br />
 * table.id
 * </p>
 */
class QualifiedName {
	String qualifier
	String name
	
	public QualifiedName(String qualifiedName) {
		(qualifier, name) = splitQualification(qualifiedName)
	}
	
	public QualifiedName(String qualifier, String name) {
		this.qualifier = qualifier
		this.name = name
	}
	
	protected List<String> splitQualification(String qualification) {
		String[] splitted = qualification.split("\\.", 2)
		if (splitted.length > 1) return [splitted[0], splitted[1]]
		else return [null, splitted[0]]
	}
	
	public String toString() {
		def string = ""
		string += (qualifier != null && qualifier != "") ? "${qualifier}." : ""
		string += name
		return string
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof QualifiedName)) return false
		def other = o as QualifiedName
		return other.qualifier == qualifier && other.name == name
	}
}
