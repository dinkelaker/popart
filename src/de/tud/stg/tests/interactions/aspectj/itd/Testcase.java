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
package de.tud.stg.tests.interactions.aspectj.itd;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * This test case demonstrates Introduction of fields with AspectJ.
 * See A.aj for the Aspect, and C.java for the original class.<br><br>
 * See the files C.java and MetaProgram.groovy in the package
 * de.tud.stg.tests.interactions.popart.itd for the same functionallity
 * implemented via the groovy/popart aspect system.
 * 
 */
public class Testcase {
	@Test
	public void test() throws Exception {
		C c = new C();
		c.m1();
		c.m1();
		c.m2();
		c.m3();
		c.m1();
		c.m1();
		assertEquals(6, c.getClass().getField("counter").getInt(c));
	}
}
