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
package de.tud.stg.tests.popart.extensions.coolcaching;

public class Test1Victim {
  
  private String content;
  
  public long wordCount(String s) throws Exception {
    System.out.println("App:  wordCount(" + s + ")");
    Thread.currentThread().sleep(1000);
    return System.currentTimeMillis();
  }
  
  public void addLine(String s) throws Exception {
    System.out.println("App:  addLine(" + s + ")");
  }
  
  public String toString() {
    return "Test1Victim";
  }
}
