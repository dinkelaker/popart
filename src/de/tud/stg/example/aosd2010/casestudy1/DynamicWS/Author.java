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
package de.tud.stg.example.aosd2010.casestudy1.DynamicWS;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Author", propOrder = {
    "authorFirstName",
    "authorLastName",
    "authorBirthDate"
})
public class Author {
	
	private String authorFirstName;
	private String authorLastName;
	private String authorBirthDate;
	
	
	public Author() {
		super();
	}
	
	public Author(String authorFirstName, String authorLastName, String authorBirthDate) {
		super();
		this.authorFirstName = authorFirstName;
		this.authorLastName = authorLastName;
		this.authorBirthDate = authorBirthDate;
	}

	public String getAuthorFirstName() {
		return authorFirstName;
	}

	public void setAuthorFirstName(String authorFirstName) {
		this.authorFirstName = authorFirstName;
	}

	public String getAuthorLastName() {
		return authorLastName;
	}

	public void setAuthorLastName(String authorLastName) {
		this.authorLastName = authorLastName;
	}
	
	public String getAuthorBirthDate() {
		return authorBirthDate;
	}

	public void setAuthorBirthDate(String authorBirthDate) {
		this.authorBirthDate = authorBirthDate;
	}

	
	
}
