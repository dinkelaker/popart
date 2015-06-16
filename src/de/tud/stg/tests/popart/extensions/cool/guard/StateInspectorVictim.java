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
package de.tud.stg.tests.popart.extensions.cool.guard;

import java.util.Vector;

public class StateInspectorVictim {
  public int mState = 0;
  
  public Vector<String> mTrace = new Vector<String>();
  
  public void initState() {
    mState = 1;
    mTrace.add("init");
  }
  
  public void useTrace(Vector<String> trace) {
    mTrace = trace;
  }
  
  public void doSth(String s) {
    System.out.println("App:  doSth(" + s + ")");
    mTrace.add("run " + s);
  }
  
  public void changeTitle(String s) {
    System.out.println("App:  changeTitle(" + s + ")");
    mTrace.add("changeTitle " + s);
  }

  public void getTitle(String s) {
    System.out.println("App:  getTitle(" + s + ")");
    mTrace.add("getTitle " + s);
  }

  public void setTitle(String s) {
    System.out.println("App:  setTitle(" + s + ")");
    mTrace.add("setTitle " + s);
  }

  public String toString() {
    return mTrace.toString();
  }
}
