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
package de.tud.stg.tests.popart.extensions.cool.mutex;

import junit.framework.*;
import junit.textui.TestRunner;
import net.sourceforge.groboutils.junit.v1.*;

public class BookAdderTestCase extends TestCase {


  private class SelfexTestWithGrobo extends TestRunnable {
    
    private Book mBook;
    private int mId;
    
    private SelfexTestWithGrobo(Book b, int id) {
      mBook = b;
      mId = id;
    }
    
    public void runTest() throws Throwable {
      if (mId == 10)
        mBook.addPage(Integer.toString(mId));
      else
        mBook.deletePage(Integer.toString(mId));
    }
    
  }
  
  public void testSth() throws Throwable {
    System.out.println("====== Running  COOL Mutex Test (JUnit+Grobo) ======");
    Book book = new Book("myBook");
    TestRunnable t1 = new SelfexTestWithGrobo(book, 10);
    TestRunnable t2 = new SelfexTestWithGrobo(book, 20);
    TestRunnable[] ts = { t1, t2 };
    MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(ts);
    mttr.runTestRunnables();
   
    assertTrue(book.getPages().toString().equals("[10ai, 10ao, 20di, 20do]") ||
      book.getPages().toString().equals("[20di, 20do, 10ai, 10ao]"));

    System.out.println("====== Finished COOL Mutex Test (JUnit+Grobo) ======");
  }

  
  public static void main(String[] args) {
    String[] name = { BookAdderTestCase.class.getName() };
    TestRunner.main(name);
  }
  
  public static Test suite() {
    return new TestSuite(BookAdderTestCase.class);
  }
  
}
