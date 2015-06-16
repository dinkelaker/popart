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
package de.tud.stg.tests.popart.extensions.cool.wordprocessor;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @author Oliver Rehor
 **/
public class WordProcessor {
  Document doc;
  
  public WordProcessor() {
    doc = new Document();
  }

  public void run() {
    try {
      while (true) {
        System.out.print("WP:> ");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String lineInput = br.readLine();
        if (lineInput.equals(":quit"))
          break;
        doc.addLine(lineInput);
      }
    } catch (Exception E) {
    }

  }
}
