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
package de.tud.stg.popart.aspect.extensions.cool.domainmodel;

/**
 * @author Oliver Rehor
 */
public class ConflictingStatementException extends Exception {
  
  private static final long serialVersionUID = -1L;

  private String mMessage;
  
  public ConflictingStatementException(String statement1, String statement2,
                                       String reason) {
    mMessage = "Statement '" + statement1 + "' conflicts with '" + statement2 +
               "': " + reason;
  }
  
  public String getMessage() {
    return mMessage;
  }
}