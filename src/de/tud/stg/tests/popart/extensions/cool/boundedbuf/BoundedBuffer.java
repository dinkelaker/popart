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
package de.tud.stg.tests.popart.extensions.cool.boundedbuf;

import java.util.Vector;

// Bounded Buffer example from Lopez Diss.
public class BoundedBuffer {
  
  private Vector<String> trace = new Vector<String>();
  
  private Object array[];
  private int takePtr = 0, putPtr = 0;
  public int usedSlots = 0, size;
  
  BoundedBuffer(int capacity) throws IllegalArgumentException {
  if (capacity <= 0) throw new IllegalArgumentException();
  array = new Object[capacity];
  size = capacity ;
  }
  
  public int count() { return usedSlots; }
  
  public int capacity() { return size; }
  
  public void put(Object x) throws Exception {
  System.out.println("Running put(" + x + ")");
  if (usedSlots == size) {
    trace.add("Full, wanted to add " + x);
    throw new Exception("Full");
  }
  array[putPtr] = x;
  putPtr = (putPtr + 1) % size;
  usedSlots++;
  trace.add("done put(" + x + ")");
  }
  
  public Object take() throws Exception {
    System.out.println("Running take()");
    if (usedSlots == 0) {
      trace.add("Empty, wanted to take ");
      throw new Exception("Empty");
    }
    Object old = array[takePtr];
    takePtr = (takePtr + 1) % size;
    usedSlots--;
    trace.add("done take() = " + old);
    return old;
    }
  
  public String toString() {
    return trace.toString();
  }
}
