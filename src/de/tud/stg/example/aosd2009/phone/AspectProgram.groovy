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
package de.tud.stg.example.aosd2009.phone

import de.tud.stg.popart.aspect.*;
import de.tud.stg.popart.aspect.extensions.*;
import de.tud.stg.popart.aspect.AspectFactory

Main.main((String[])["test"].toArray());	

System.out.println("Defining Bootstrap Closure");
def aspect = { map, definition ->
  def result = new CCCombiner().eval(map,definition);
  AspectManager.getInstance().register(result);
  return result;
}

def forwardTemplate = { fromInstance, toInstance, p ->
  println "Applying Template to define  to ${fromInstance.name}To${toInstance.name} params=($fromInstance, $toInstance, $p) "
  aspect(
	  name:"${fromInstance.name}To${toInstance.name}",priority:p,
	  perInstance:fromInstance,deployed:true) { 
    around(method_execution("receiveCall.*")) { 
	  boolean answered = proceed();
      println "$thisAspect.name: call was answered by original phone $targetObject? $answered";
      if (!answered) { 
        Phone forwardPhone = toInstance;
        println "$thisAspect.name: forwarding to "+forwardPhone;
        answered = forwardPhone?.receiveCall(thisJoinPoint.args[0]);
        println "$thisAspect.name: forwarded phone $forwardPhone answered? $answered"
        println "$thisAspect.name: enclosingJoinPoint=$enclosingJoinPoint"	  
      } 
      return answered;
    }
  }
};


Phone alice = Main.original;
println "Get alice's phone $alice" 
println "  Forward phone is "+alice.getForwardPhone()
println "  Answer machine is "+alice.getAnswerMachine()
println "";

Phone bob = Main.secretary;
println "Get bob's phone $bob" 
println "  Forward phone is "+bob.getForwardPhone()
println "  Answer machine is "+bob.getAnswerMachine()
assert alice.getForwardPhone() == bob;

def fromAliceToAM = forwardTemplate(alice,alice.getAnswerMachine(),0);
def fromBobToAM = forwardTemplate(bob,bob.getAnswerMachine(),0);

def fromAliceToBob = forwardTemplate(alice,alice.getForwardPhone(),1);

println "-------";
System.out.println("\nCall will be forwarded, but the secretary's answer machine is off. ");
Main.testSecretaryAnswerMachineOff();	

System.out.println("\nCall will be forwarded, and also the secretary's answer machine is on. ");
Main.testSecretaryAnswerMachineOn();	

AspectFactory.defaultComparator = new PhoneManagementSystemComparator<AspectMember>();

println "-------";
System.out.println("\nCall will be forwarded, and also the secretary's answer machine is on. ");
Main.testSecretaryAnswerMachineOn();	
	





/*System.out.println("Define Aspect 1");
//Remaining lines in OpenCCC
def ftc = aspect(name:"ForwardToCollegue",priority:1,deployed:false) { 
  around(method_execution("receiveCall.*")) { 
	boolean answered = proceed();
    println "$thisAspect.name: call was answered by original phone $targetObject? $answered";
    if (!answered) { 
      Phone forwardPhone = targetObject.getForwardPhone();
      println "$thisAspect.name: forwarding to "+forwardPhone;
      answered = forwardPhone?.receiveCall(thisJoinPoint.args[0]);
      println "$thisAspect.name: forwarded phone $forwardPhone answered? $answered"
      println "$thisAspect.name: enclosingJointPoint=$enclosingJointPoint"	  
    } 
    return answered;
  }
};

System.out.println("Define Aspect 2");
def fta = aspect(name:"ForwardToAnswerMachine",priority:0,deployed:false) { 
  around(method_execution("receiveCall.*")) {
    boolean answered = proceed(); 
    println "$thisAspect.name: call was answered by original phone $targetObject? $answered";
	if (!answered) { 
	  AnswerMachine am = targetObject.getAnswerMachine();
      println "$thisAspect.name: forwarding to $am";
	  answered = am?.receiveCall(thisJoinPoint.args[0]); 
      println "$thisAspect.name: forwarded phone $am answered? $answered"
      println "$thisAspect.name: enclosingJointPoint=$enclosingJointPoint"	  
	} 
	return answered;
  }
} 

//def ftsc = ftc.clone(); 
//ftsc.name = "Secretary"+ftsc.name 
//def ftsa = fta.clone();
//ftsa.name = "Secretary"+ftsa.name 

System.out.println("Define Aspect 3");
def ftsc = aspect(name:"SecretaryForwardToCollegue",priority:1,deployed:false) { 
  around(method_execution("receiveCall.*")) { 
	boolean answered = proceed();
    println "$thisAspect.name: call was answered by original phone $targetObject? $answered";
    if (!answered) { 
      Phone forwardPhone = targetObject.getForwardPhone();
      println "$thisAspect.name: forwarding to "+forwardPhone;
      answered = forwardPhone?.receiveCall(thisJoinPoint.args[0]);
      println "$thisAspect.name: forwarded phone $forwardPhone answered? $answered"
      println "$thisAspect.name: enclosingJointPoint=$enclosingJointPoint"	  
    } 
    return answered;
  }
};

System.out.println("Define Aspect 4");
def ftsa = aspect(name:"SecretaryForwardToAnswerMachine",priority:3,deployed:false) { 
  around(method_execution("receiveCall.*")  & not(cflow(advice_execution()))  ) {
    boolean answered = proceed(); 
    println "$thisAspect.name: call was answered by original phone $targetObject? $answered";
	if (!answered) { 
	  AnswerMachine am = targetObject.getAnswerMachine();
      println "$thisAspect.name: forwarding to $am";
	  answered = am?.receiveCall(thisJoinPoint.args[0]); 
      println "$thisAspect.name: forwarded phone $am answered? $answered"
      println "$thisAspect.name: enclosingJointPoint=$enclosingJointPoint"	  
	} 
	return answered;
  }
} 

//println fta
AspectManager.getInstance().register(ftc)
AspectManager.getInstance().register(fta)
AspectManager.getInstance().register(ftsc)
AspectManager.getInstance().register(ftsa)
//println AspectManager.getInstance().getAspect("ForwardToAnswerMachine")

*/

//ftc.metaAspect = new DebugMetaAspect(fta.class);
//fta.metaAspect = new DebugMetaAspect(fta.class);
//Main.testAnswerMachineOff();	

/*
System.out.println("\nCall will be not forwarded, also the answer machine is off.");
Main.testAnswerMachineOff();	

System.out.println("\nCall will be not forwarded, but answer machine is on.");
Main.testAnswerMachineOn();	
*/

////System.out.println("\nDeploying the Aspects, calls will now be forwarded");
////ftc.deployPerInstance(Main.original);
////fta.deployPerInstance(Main.original);

/*
System.out.println("\nCall will be forwarded, but secretary's answer machine is off");
Main.testSecretaryAnswerMachineOff();	

System.out.println("\nCall will be forwarded, and also the secretary's answer machine is on. ");
Main.testSecretaryAnswerMachineOn();	

System.out.println("\nCall will be forwarded, and also the secretary's answer machine is on. ");

Main.testSecretaryAnswerMachineOn();	
*/ 

////
/*
System.out.println("\nDeploying the aspects for secretary");
//ftsc.deployPerInstance(Main.secretary);
ftsa.deployPerInstance(Main.secretary);

System.out.println("\nCall will be forwarded, and also the secretary's answer machine is on. ");

Main.testSecretaryAnswerMachineOn();	
*/