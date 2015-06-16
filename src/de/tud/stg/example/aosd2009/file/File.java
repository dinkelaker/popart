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
package de.tud.stg.example.aosd2009.file;

import java.util.Random;

public class File {
	
	protected String fileName;
	protected int index = 0, size=0; 
	protected int[] data;
	
	public File(String fileName) {
		this.fileName = fileName;
	    this.size = Math.abs((new Random().nextInt()) % 32)+5;
	    this.data = new int[this.size];
	    for (int i=0; i < this.size; i++) {
	    	this.data[i] = new Random().nextInt();
	    }
	}
	
	public int getSize() { return this.size; }
	
	public void resetHandle() { this.index = 0; }
	
	public int readInt() {
		System.err.println(fileName+"["+index+"]:read");
		return data[index++]; 
	}

	public void writeInt(int data) {
		System.err.println(fileName+"["+index+"]:write");
		this.data[index++]=data;
	}
	
	public void copy(File into) {
		into.size = this.size;
		into.data = new int[into.size];
		this.index = 0;
		for (int i=0; i<this.size; i++) {
			System.err.println(fileName+"["+i+"]:copy size="+size);
			into.writeInt(this.readInt());
		}
//		int i = 0;
//		while (i < this.size) {
//			System.err.println(fileName+"["+i+"]:copy size="+size);
//            into.writeInt(this.readInt());
//            i++;
//		}
	}

}
