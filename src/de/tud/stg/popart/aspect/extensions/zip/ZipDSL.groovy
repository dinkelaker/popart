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
package de.tud.stg.popart.aspect.extensions.zip;

import java.util.Formatter;
import java.lang.StringBuilder;

import de.tud.stg.popart.aspect.*;
import de.tud.stg.popart.aspect.extensions.zip.domainmodel.*;

/**
 * Interpreter layer (I) class that handles interpretation of Zip EDSL
 * programs utilizing Groovy's Meta Object Protocol.
 * @author Oliver Rehor
 */
public class ZipDSL implements IZipDSL {
  
  public final static boolean mDebug = true;
  
  private Zipper mZipper;
  
  private static def aspect = { map, definition ->
    return new CCCombiner().eval(map,definition)
  }
  
  // constructors
  public ZipDSL() {
  }
  
  /**
   * Return a closure that manages the actual call to a Zip interpreter.
   * This closure can be bound to a bootstrap keyword, for instance:
   * def zip = ZipDSL.getInterpreter();
   * @return a closure with an interpreter call
   */
  public static Closure getInterpreter() {
    return { params, body ->
             IZipDSL zipInterpreter = new ZipDSL();
             return zipInterpreter.eval(params, body) };
  }
  
  /**
   * Main entry point of the COOL interpreter. This method creates a
   * {@link Coordinator} object representing the coordinator this method
   * is called for. Keywords found in this coordinator's body are mapped
   * to method names of {@link CoolDSL}.
   * @param classNames       class names the current coordinator is
   *                         responsible for
   * @param coordinatorBody  the body of the coordinator containing COOL
   *                         statements/language constructs
   */
  public void eval(Class className, Closure zipBody) {
    preEvalActions(className);
	
    zipBody.delegate = this;
    zipBody.resolveStrategy = Closure.DELEGATE_FIRST;
    zipBody.call();
    
    postEvalActions(className);
  }
   
  public void preEvalActions(Class theClass) {
    mZipper = new Zipper(theClass);
  }
   
  public void postEvalActions(Class theClass) {
    mZipper.registerAspects();
  }
  
  public void compress_args(String methodName) {
    compress_args([methodName]);
  }
  
  public void compress_args(List<String> methodNames) {
    if (mDebug)
      debugMsg("ZipDSL", "found compress_args");
    mZipper.setAffectedMethod(methodNames, ActionType.COMPRESS_ARGS);
  }

  public void decompress_args(String methodName) {
    decompress_args([methodName]);
  }
  
  public void decompress_args(List<String> methodNames) {
    if (mDebug)
      debugMsg("ZipDSL", "found decompress_args");
    mZipper.setAffectedMethod(methodNames, ActionType.DECOMPRESS_ARGS);
  }
  
  public void compress_result(String methodName) {
    compress_result([methodName]);
  }
  
  public void compress_result(List<String> methodNames) {
    if (mDebug)
      debugMsg("ZipDSL", "found compress_result");
    mZipper.setAffectedMethod(methodNames, ActionType.COMPRESS_RESULT);
  }
  
  public void decompress_result(String methodName) {
    decompress_result([methodName]);
  }
  
  public void decompress_result(List<String> methodNames) {
    if (mDebug)
      debugMsg("ZipDSL", "found decompress_result");
    mZipper.setAffectedMethod(methodNames, ActionType.DECOMPRESS_RESULT);
  }

  public static void debugMsg(String module, String message) {
    StringBuilder sb = new StringBuilder();
    Formatter fm = new Formatter(sb, Locale.US);
    fm.format("Zip     [%02d] %-12s | %s",
    Thread.currentThread().getId(), module, message);
    System.out.println(sb);
  }
  
}
