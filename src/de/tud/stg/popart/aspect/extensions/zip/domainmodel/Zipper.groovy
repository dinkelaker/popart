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
package de.tud.stg.popart.aspect.extensions.zip.domainmodel;

import de.tud.stg.popart.aspect.*;
import java.util.zip.*;
import de.tud.stg.popart.aspect.extensions.itd.StructuralPointcutDSL;
import de.tud.stg.popart.aspect.extensions.zip.ZipDSL;

public enum ActionType {
  COMPRESS_ARGS, DECOMPRESS_ARGS,
  COMPRESS_RESULT, DECOMPRESS_RESULT
}

/**
 * @author Oliver Rehor
 */
public class Zipper {
  
  private static boolean mDebug = true;

  private static def aspect = { map, definition ->
    return new CCCombiner(new StructuralPointcutDSL()).eval(map,definition)
  }

  // entry: methodName -> { c_args, dc_args, c_result, dc_result }
  private HashMap<String,List<ActionType>> mAffectedMethods = [:];
  
  private Class mClass;
  
  public Zipper(Class theClass) {
    mClass = theClass;
  }
  
  public void setAffectedMethod(List<String> methodNames, ActionType action) {
    methodNames.each {
      if (mAffectedMethods.containsKey(it))
        mAffectedMethods[it].add(action);
      else
        mAffectedMethods[it] = [action];
    }
  }
  
  public void registerAspects() {
    assert mAffectedMethods != null;
    mAffectedMethods.keySet().each { methodName ->
      Aspect compressArgsAspect = aspect(name:"zipAroundCompress") {
        around(is_class(mClass) & method_call(methodName)) {
          if (mDebug)
            ZipDSL.debugMsg("Zipper", "around '" + methodName + "'");
          if (mAffectedMethods[methodName].contains(ActionType.COMPRESS_ARGS)) {
            if (mDebug)
              ZipDSL.debugMsg("Zipper",
                              "compressing '" + methodName + "' arguments");
            compress(args[0]);
          }
          if (mAffectedMethods[methodName].contains(ActionType.DECOMPRESS_ARGS))
          {
            if (mDebug)
              ZipDSL.debugMsg("Zipper",
                              "decompressing '" + methodName + "' arguments");
            decompress(args[0]);
          }
          
          Object result = proceed();
          
          if (mAffectedMethods[methodName].contains(ActionType.COMPRESS_RESULT)) {
            if (mDebug)
              ZipDSL.debugMsg("Zipper",
                              "compressing '" + methodName + "' result");
            compress(result);
          }
          if (mAffectedMethods[methodName].contains(ActionType.DECOMPRESS_RESULT)) {
            if (mDebug)
              ZipDSL.debugMsg("Zipper",
                              "decompressing '" + methodName + "' result");
            assert result instanceof de.tud.stg.popart.aspect.extensions.zip.Compressable;
            decompress(result);
          }
          
          return result;
        }
      }
      AspectManager.getInstance().register(compressArgsAspect);
    }
  }
  
  private Object compress(Object objectToCompress) {
    Object originalObject = objectToCompress;
    
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    GZIPOutputStream gzos = new GZIPOutputStream(baos);
    ObjectOutputStream oos = new ObjectOutputStream(gzos);
    oos.writeObject(objectToCompress.data);
    oos.flush();
    oos.close();
    byte[] data = baos.toByteArray();
    // NOW change objectToCompress to have "byte[] data" inside
    Byte[] odata = new Byte[data.length];
    for (int i=0; i<data.length; i++) {
      odata[i] = data[i];
    }
    objectToCompress.data = odata;
    
    // intercepted method args may have wrong type in method body
    // (possible solution: container object or interface, like "Compressable")
    // some asserts to ensure type safety
    
    // objectToCompress must be MODIFIED, not recreated
    assert objectToCompress.is(originalObject);
    
    // type of objectToCompress must not have changed
    assert objectToCompress.class.equals(originalObject.class);
    
    return objectToCompress;
  }
  
  private Object decompress(Object objectToDecompress) {
    Byte[] idata = objectToDecompress.data;
    byte[] data = new byte[idata.length];
    for (int i=0; i<idata.length; i++) {
      data[i] = idata[i];
    }
    
    ByteArrayInputStream bais = new ByteArrayInputStream(data);
    GZIPInputStream gzis = new GZIPInputStream(bais);
    ObjectInputStream ois = new ObjectInputStream(gzis);
    
    objectToDecompress.data = ois.readObject();
    return objectToDecompress;
  }

}
