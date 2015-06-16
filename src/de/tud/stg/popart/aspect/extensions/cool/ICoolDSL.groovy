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

import de.tud.stg.popart.dslsupport.DSL;

/**
 * Language Interface layer (L) for COOL EDSL programs
 * @author Oliver Rehor
 */
public interface ICoolDSL extends DSL {
  public void selfex(List<String> methods);
  public void mutex(List<String> methods);
  
  public void condition(HashMap<String, Boolean> vars);
  public void var(HashMap<String, Object> vars);
  
  public void guard(List<String> methods, Closure definitionBlock);
  public void requires(Closure requiredCondition);
  public void on_entry(Closure onEntryActions);
  public void on_exit(Closure onExitActions);
}
