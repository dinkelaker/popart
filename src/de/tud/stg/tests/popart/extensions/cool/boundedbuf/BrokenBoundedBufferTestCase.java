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

import groovy.lang.GroovySystem;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import net.sourceforge.groboutils.junit.v1.MultiThreadedTestRunner;
import net.sourceforge.groboutils.junit.v1.TestRunnable;
import de.tud.stg.popart.aspect.Aspect;
import de.tud.stg.popart.aspect.AspectManager;
import de.tud.stg.popart.aspect.MetaAspect;
import de.tud.stg.popart.aspect.extensions.cool.CoolAspect;
import de.tud.stg.popart.aspect.extensions.cool.CoolDebugMetaAspect;

public class BrokenBoundedBufferTestCase extends TestCase {

  private class GuardTestWithGrobo extends TestRunnable {
    
    private BoundedBuffer mVictim;
    private int mId;
    
    private GuardTestWithGrobo(BoundedBuffer v, int id) {
      mVictim = v;
      mId = id;
    }
    
    public void runTest() throws Throwable {
      try {
        
        if (mId == 10) {
          Thread.currentThread().sleep(1000);
          mVictim.put(new Integer(mId));
        }
        else if (mId == 20) {
          Thread.currentThread().sleep(100);
          mVictim.put(new Integer(mId));
         
        } else {
          Thread.currentThread().sleep(5);
          mVictim.take();
          mVictim.take();
        }
        
      } catch (Exception e) {
        System.err.println(e);
      }
    }
    
  }
  
  public void testSth() throws Throwable {
	 
	AspectManager am = AspectManager.getInstance();
	List<Aspect> aspects = am.getAspects();
	
    System.out.println("====== loaded COOL Aspects ======");
    int i=0;
    for (Aspect aspect : aspects ) {
     	System.out.println(i+": "+aspect.getName());
    	if (aspect instanceof CoolAspect) {
          System.out.println(i+": "+aspect.getName()+" is a cool aspect");
          System.out.print(i+": "+aspect.getName());
    	  
      	  System.out.println(" setting the debug meta-aspect");
          MetaAspect debugMetaAspect = new CoolDebugMetaAspect(GroovySystem.getMetaClassRegistry(), aspect.getClass());   	  
    	  aspect.setMetaAspect(debugMetaAspect);    	  
    	}
    	i++;
    }
	 
    System.out.println("====== Running  COOL Guard Test (JUnit+Grobo) ======");
    BoundedBuffer victim = new BoundedBuffer(2);
    TestRunnable t1 = new GuardTestWithGrobo(victim, 10);
    TestRunnable t2 = new GuardTestWithGrobo(victim, 20);
    TestRunnable t3 = new GuardTestWithGrobo(victim, 30);
    TestRunnable[] ts = { t1, t2, t3 };
    MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(ts);
    mttr.runTestRunnables();
    
    //assertTrue(victim.toString().equals("[changeTitle 10, getTitle 20, changeTitle 20, setTitle 30]"));
    System.out.println(victim.toString());
   
    System.out.println("====== Finished COOL Guard Test (JUnit+Grobo) ======");
  }

  
  public static void main(String[] args) {
    String[] name = { BrokenBoundedBufferTestCase.class.getName() };
    TestRunner.main(name);
  }
  
  public static Test suite() {
    return new TestSuite(BrokenBoundedBufferTestCase.class);
  }
  
}
