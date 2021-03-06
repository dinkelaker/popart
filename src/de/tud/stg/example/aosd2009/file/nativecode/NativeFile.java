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
package de.tud.stg.example.aosd2009.file.nativecode;

import de.tud.stg.example.aosd2009.file.File;

public class NativeFile extends File {

	public NativeFile(String fileName) {
		super(fileName);
	}
	
	public int readInt() {
		System.err.println(fileName+"["+index+"]:native-read");
		return data[index++]; 
	}

	public void writeInt(int data) {
		System.err.println(fileName+"["+index+"]:native-write");
		this.data[index++]=data;
	}

}
