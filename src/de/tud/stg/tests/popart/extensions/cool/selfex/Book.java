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
package de.tud.stg.tests.popart.extensions.cool.selfex;

import java.util.Random;
import java.util.Vector;

/**
 * @author Oliver Rehor
 **/
public class Book {
  private String title = "defaultTitle_";
  private Vector<String> pages = new Vector<String>();
  private Random sleepTime;
  
  public Book(String t) {
    sleepTime = new Random();
    System.out.println("App:   Book constructor call: " + t + " by thread_" + Thread.currentThread().getId());
    title = t;
  }
  
  public void title(String t) {
    System.out.println("App:   Book.title(..) executing.");
    title = t;
  }
  
  public void addPage(String name) throws Exception {
    System.out.println("App:   Book.addPage(" + name + ") executing, may take up to 10s.");
    pages.add(name + "i");
    Thread.currentThread().sleep(sleepTime.nextInt(5000));
    pages.add(name + "o");
    Thread.currentThread().sleep(sleepTime.nextInt(5000));
  }
  
  public String getPages() {
    System.out.println("App:   Book.getPages() executing.");
    return pages.toString();
  }
  
  public String toString() { return title; }
  
}
