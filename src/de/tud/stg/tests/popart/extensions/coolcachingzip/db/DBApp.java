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
package de.tud.stg.tests.popart.extensions.coolcachingzip.db;

import de.tud.stg.popart.aspect.extensions.zip.Compressable;
import junit.framework.*;
import junit.textui.TestRunner;
import net.sourceforge.groboutils.junit.v1.*;

/**
 * @author Oliver Rehor
 **/
public class DBApp extends TestCase {


  private class DBClientThread extends TestRunnable {
    
    private DBFile mDB;
    private int mId;
    
    private DBClientThread(DBFile db, int id) {
      mDB = db;
      mId = id;
    }
    
    public void runTest() throws Throwable {
      if (mId == 10) {
        Compressable c = mDB.read();
      }
      else if (mId == 30) {
        //Thread.currentThread().sleep(1000);
        delay(10);
        Compressable c = mDB.read();
      }
      else {
        //Thread.currentThread().sleep(2000);
        delay(20);
        Compressable c = new Compressable();
        c.data = "newData";
        mDB.write(c);
        mDB.read(); // must not use the cache since invalidated now
      }
    }
    
  }
  
  public void testSth() throws Throwable {
    System.out.println("====== Running  CoCaZip Test (JUnit+Grobo) ======");
    DBFile dbFile = new DBFile();
    Compressable initialContent = new Compressable();
    initialContent.data = "fileData";
    dbFile.setFileContent(initialContent);
    TestRunnable t1 = new DBClientThread(dbFile, 10);
    TestRunnable t2 = new DBClientThread(dbFile, 20);
    TestRunnable t3 = new DBClientThread(dbFile, 30);
    TestRunnable[] ts = { t1, t2, t3 };
    MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(ts);
    mttr.runTestRunnables();
    
    System.out.println("====== Finished CoCaZip Test (JUnit+Grobo) ======");
  }

  
  public static void main(String[] args) {
    String[] name = { DBFile.class.getName() };
    TestRunner.main(name);
  }
  
  public static Test suite() {
    return new TestSuite(DBFile.class);
  }
  
}
