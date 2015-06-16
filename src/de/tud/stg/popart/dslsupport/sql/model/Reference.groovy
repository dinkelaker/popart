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
 * <p>A reference consists of a qualified name and an optional alias.</p>
 * <p><b>Examples:</b><br />
 * id<br />
 * table.id<br />
 * table.id AS Id<br />
 * table.id Id
 * <p>Wherein the last two examples are semantically equal</p>
 * @see QualifiedName
 */
class Reference {
	QualifiedName qualifiedName
	String alias
	
	String getName() {
		qualifiedName.getName()
	}
	
	void setName(String name) {
		this.qualifiedName.setName(name)
	}
	
	String getQualifier() {
		qualifiedName.getQualifier()
	}
	
	void setQualifier(String qualifier) {
		qualifiedName.setQualifier(qualifier)
	}
	
	public Reference(String qualifier, String name, String alias) {
		this.qualifiedName = new QualifiedName(qualifier, name)
		this.alias = alias
	}

	public Reference(String qualifiedName, String alias) {
		this.qualifiedName = new QualifiedName(qualifiedName)
		this.alias = alias
	}
	
	public Reference(String qualifiedNameAndAs) {
		parse(qualifiedNameAndAs)
	}
	
	public String toString() {
		String string = qualifiedName.toString()
		if (alias != null && alias != "") string += " AS ${alias}"
		return string
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof Reference)) return false
		def other = o as Reference
		return other.qualifiedName == qualifiedName && other.alias == alias
	}
	
	protected parse(String string) {
		string = string.replaceAll("\\s+", " ").trim()
		String[] split = string.split(" ", 3)
		qualifiedName = new QualifiedName(split[0])
		if (split.length == 2) alias = split[1]
		else if (split.length == 3) alias = split[2]
	}
}
