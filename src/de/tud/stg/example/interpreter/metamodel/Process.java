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
package de.tud.stg.example.interpreter.metamodel;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Process extends ProcessElement {
	
	protected static int lastId = 0; 
	
	protected String id = "Process"+(lastId++);
	
	protected Set<Task> tasks = new HashSet<Task>();
	
	protected List<Task> sequence = new LinkedList<Task>();
	
	public Process(String id) { 
		super();
		this.id = id;
	}
	
	public String getId() { return id; }
	
	public void setId(String id) { this.id = id; }
	
	public Set<Task> getTasks() { return tasks; }
	
	public void setTasks(Set<Task> tasks) { this.tasks = tasks; }
	
	public List<Task> getSequence() { return sequence; }
	
	public void setSequence(List<Task> sequence) { this.sequence = sequence; }
	
	public String toString() { return "process:"+id; }

	public void execute() {
		if (ProcessElement.DEBUG) debug("Process.execute id="+id+" ... ");
		Iterator<Task> it = sequence.iterator();
		while (it.hasNext()) {
			Task task = it.next();
			task.execute();
		}
		if (ProcessElement.DEBUG) debug("Process.execute id="+id+" ... finished. ");
	}
	
}
