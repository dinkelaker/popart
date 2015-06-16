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
public class MethodManager2Main {

  public static void init() {
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    final Book b = new Book("Buch");
    Thread t1 = new Thread() {
      public void run() {
        try {
          b.changeTitle();
        } catch (Exception E) {
          System.err.println(E);
        }
        System.out.println(b.getTitle());
      }
    };
    Thread t2 = new Thread() {
      public void run() {
        try {
          Thread.currentThread().sleep(1000);
          b.title("TestbuchT2_");
          System.out.println(b.getTitle());
        } catch (Exception E) {
          System.err.println(E);
        }
      }
    };
    t1.start();
    t2.start();
  }

}
