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
package de.tud.stg.tests.popart.extensions.cool;

/**
 * @author Oliver Rehor
 **/
public class Book {
  private String title = "defaultTitle_";
  
  public Book(String t) {
    System.out.println("App:   Book constructor call: " + t + " by thread_" + Thread.currentThread().getId());
    title = t;
  }
  
  public void title(String t) {
    System.out.println("App:   Book.title(..) executing.");
    title = t;
  }
  
  public void changeTitle() throws Exception {
    System.out.println("App:   Book.changeTitle(..) executing.");
    title += "suffixed";
    Thread.currentThread().sleep(2000);
  }
  
  public void doSth() throws Exception {
    System.out.println("App:   Book.doSth(..) executing.");
    title +="doneSth";
    Thread.currentThread().sleep(3000);
  }
  
  public String getTitle() {
    System.out.println("App:   Book.getTitle(..) executing.");
    return title;
  }
  
  public String toString() { return title; }
}
