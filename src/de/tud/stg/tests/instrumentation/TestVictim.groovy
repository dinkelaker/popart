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
package de.tud.stg.tests.instrumentation

import groovy.util.GroovyTestCase


/**
 * @author Jan Stolzenburg
 */
public class TestVictim extends GroovyTestCase {
	
	void testStaticInitializerExecuted() {
		assertEquals(0, Victim.classHistory[0])
	}
	
	void testPutter() {
		assertEquals(0, Victim.classHistory[0])
		def oldClassHistory = Victim.classHistory.clone()
		def victim = new Victim()
		assertEquals([0], victim.instanceHistory)
		assertEquals(1, victim.putOne())
		assertEquals([0, 1], victim.instanceHistory)
		assertEquals(2, victim.putTwo())
		assertEquals([0, 1, 2], victim.instanceHistory)
		assertEquals(oldClassHistory, Victim.classHistory)
		assertEquals(3, victim.putThree())
		assertEquals([0, 1, 2], victim.instanceHistory)
		assertEquals(oldClassHistory + [3], Victim.classHistory)
		assertEquals(4, victim.putFour())
		assertEquals([0, 1, 2], victim.instanceHistory)
		assertEquals(oldClassHistory + [3, 4], Victim.classHistory)
		assertEquals([5, 'a', 'b', 'c'], victim.putFive())
		assertEquals([0, 1, 2, [5, 'a', 'b', 'c']], victim.instanceHistory)
		assertEquals(oldClassHistory + [3, 4], Victim.classHistory)
		assertEquals([6, 'a', 'b', 'c'], victim.putSix())
		assertEquals([0, 1, 2, [5, 'a', 'b', 'c']], victim.instanceHistory)
		assertEquals(oldClassHistory + [3, 4, [6, 'a', 'b', 'c']], Victim.classHistory)
		victim.instanceRecursionParameter = [[], []]
		assertEquals(7, victim.putSeven())
		assertEquals([0, 1, 2, [5, 'a', 'b', 'c'], 7, 7, 7], victim.instanceHistory)
		assertEquals(oldClassHistory + [3, 4, [6, 'a', 'b', 'c']], Victim.classHistory)
		victim.classRecursionParameter = [[], []]
		assertEquals(8, victim.putEight())
		assertEquals([0, 1, 2, [5, 'a', 'b', 'c'], 7, 7, 7], victim.instanceHistory)
		assertEquals(oldClassHistory + [3, 4, [6, 'a', 'b', 'c'], 8, 8, 8], Victim.classHistory)
		victim.instanceRecursionParameter = [[], []]
		assertEquals([9, 'a', 'b', 'c'], victim.putNine())
		assertEquals([0, 1, 2, [5, 'a', 'b', 'c'], 7, 7, 7, [9, 'a', 'b', 'c'], [9, 'a', 'b', 'c'], [9, 'a', 'b', 'c']], victim.instanceHistory)
		assertEquals(oldClassHistory + [3, 4, [6, 'a', 'b', 'c'], 8, 8, 8], Victim.classHistory)
		victim.classRecursionParameter = [[], []]
		assertEquals([10, 'a', 'b', 'c'], victim.putTen())
		assertEquals([0, 1, 2, [5, 'a', 'b', 'c'], 7, 7, 7, [9, 'a', 'b', 'c'], [9, 'a', 'b', 'c'], [9, 'a', 'b', 'c']], victim.instanceHistory)
		assertEquals(oldClassHistory + [3, 4, [6, 'a', 'b', 'c'], 8, 8, 8, [10, 'a', 'b', 'c'], [10, 'a', 'b', 'c'], [10, 'a', 'b', 'c']], Victim.classHistory)
		assertEquals([11, 'X'], victim.putEleven('X'))
		assertEquals([0, 1, 2, [5, 'a', 'b', 'c'], 7, 7, 7, [9, 'a', 'b', 'c'], [9, 'a', 'b', 'c'], [9, 'a', 'b', 'c'], [11, 'X']], victim.instanceHistory)
		assertEquals(oldClassHistory + [3, 4, [6, 'a', 'b', 'c'], 8, 8, 8, [10, 'a', 'b', 'c'], [10, 'a', 'b', 'c'], [10, 'a', 'b', 'c']], Victim.classHistory)
		assertEquals([12, 'X', 'Y'], victim.putTwelve('X', 'Y'))
		assertEquals([0, 1, 2, [5, 'a', 'b', 'c'], 7, 7, 7, [9, 'a', 'b', 'c'], [9, 'a', 'b', 'c'], [9, 'a', 'b', 'c'], [11, 'X'], [12, 'X', 'Y']], victim.instanceHistory)
		assertEquals(oldClassHistory + [3, 4, [6, 'a', 'b', 'c'], 8, 8, 8, [10, 'a', 'b', 'c'], [10, 'a', 'b', 'c'], [10, 'a', 'b', 'c']], Victim.classHistory)
		assertEquals([13, 'X', 'Y', 'Z'], victim.putThirteen('X', 'Y', 'Z'))
		assertEquals([0, 1, 2, [5, 'a', 'b', 'c'], 7, 7, 7, [9, 'a', 'b', 'c'], [9, 'a', 'b', 'c'], [9, 'a', 'b', 'c'], [11, 'X'], [12, 'X', 'Y'], [13, 'X', 'Y', 'Z']], victim.instanceHistory)
		assertEquals(oldClassHistory + [3, 4, [6, 'a', 'b', 'c'], 8, 8, 8, [10, 'a', 'b', 'c'], [10, 'a', 'b', 'c'], [10, 'a', 'b', 'c']], Victim.classHistory)
	}
	
}
