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
package de.tud.stg.tests.instrumentation.core;

import java.beans.IntrospectionException;
import groovy.lang.GroovySystem;
import groovy.lang.MetaClass;
import groovy.lang.MetaClassRegistry;
import groovy.lang.ProxyMetaClass;

class MyProxyMetaClass extends ProxyMetaClass {
    public static void enable(@SuppressWarnings("unchecked") Class theClass) {
        MetaClassRegistry metaRegistry = GroovySystem.getMetaClassRegistry();
        MetaClass originalMetaClass = metaRegistry.getMetaClass(theClass);
        MyProxyMetaClass myProxyMetaClass;
		try {
			myProxyMetaClass = new MyProxyMetaClass(metaRegistry, theClass, originalMetaClass);
		} catch (IntrospectionException exception) {
			throw new RuntimeException(exception);
		}
        metaRegistry.setMetaClass(theClass, myProxyMetaClass);
    }
    public MyProxyMetaClass(MetaClassRegistry registry, @SuppressWarnings("unchecked") Class theClass, MetaClass adaptee) throws IntrospectionException {
    	super(registry, theClass, adaptee);
    }
    @Override
    public Object invokeMethod(Object object, String methodName, Object[] arguments) {
    	if (methodName.equals("getMetaClass"))
    		return this;
    	return "Intercepted";
    }
    @Override
    public Object invokeStaticMethod(Object object, String methodName, Object[] arguments) {
    	if (methodName.equals("getMetaClass"))
    		return this;
    	return super.invokeStaticMethod(object, methodName, arguments);
    }
    @Override
    public Object getProperty(@SuppressWarnings("unchecked") Class aClass, Object object, String property, boolean b, boolean b1) {
    	if (property.equals("metaClass"))
    		return this;
        return super.getProperty(aClass, object, property, b, b1);
    }
}