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

import java.util.List;
import java.util.ArrayList;

/**
 * @author Oliver Rehor
 **/
public class Document {
  private List<String> content = new ArrayList<String>();
  
  public void addLine(String line) {
    this.content.add(line);
  }
  
  public void setContent(List<String> content) {
    this.content = content;
  }

  public List<String> getContent() {
    return this.content;
  }
  
  // from http://jami.sf.net/
  public long wordCount() {
    long result = 0;
    for (String line : this.content) {
      boolean prevWhitespace = true;
      for (int i = 0; i < line.length(); i++) {
        if (Character.isWhitespace(line.charAt(i)))
        {
          if (!prevWhitespace)
            result++;
          prevWhitespace = true;
        }
        else
          prevWhitespace = false;
      }
      if (!prevWhitespace)
        result++;
    }
    return result;
  }
  
  // from http://jami.sf.net/
  public static void jamiDocumentTest()
  {
    Document doc1 = new Document();
    doc1.addLine("This song's just six words long");
    System.out.println("Document 1 now contains " + doc1.wordCount() + " words.");
    doc1.addLine("This song's just six words long  ");
    System.out.println("Document 1 now contains " + doc1.wordCount() + " words.");
    System.out.println("Document 1 still contains " + doc1.wordCount() + " words.");
    
    Document doc2 = new Document();
    doc2.addLine("This is a completely different document.");
    doc2.addLine("Its contents do not bear any relation to those of the first document.");
    System.out.println("Document 2 contains " + doc2.wordCount() + " words.");
    doc2.addLine("Bla");
    System.out.println("Document 2 now contains " + doc2.wordCount() + " words.");
    ArrayList<String> newContents = new ArrayList<String>();
    newContents.add("New contents, just 5 words");
    doc2.setContent(newContents); // Should invalidate the cache
    System.out.println("Document 2 now contains " + doc2.wordCount() + " words.");
    
    // Should still be cached!
    System.out.println("Document 1 however still contains " + doc1.wordCount() + " words.");  
  }
  
}
