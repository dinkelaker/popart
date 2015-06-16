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
package de.tud.stg.popart.aspect.extensions.cool;

/**
 * Interpreter layer (M) class to assist interpretation of selfex/mutex
 * syntax in COOL EDSL programs.
 * @author Oliver Rehor
 */
public class ExclusionStatementResolveHandler {
  
  private final static boolean mDebug = false;
  
  private List<String> mFoundMethodNames = [];
  
  public List<String> getFoundMethodNames() {
    return mFoundMethodNames;
  }
  
  Object getProperty(String name) {
    mFoundMethodNames.add(name);
  } 
}
