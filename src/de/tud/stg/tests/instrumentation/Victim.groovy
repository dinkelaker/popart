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


/**
 * @author Jan Stolzenburg
 */
public class Victim {
	
	//I use these instance variables because I want to check (as soon as I have time) if I can catch accesses to them.
	def one      = 1
	def two      = 2
	def five     = 5
	def seven    = 7
	def nine     = 9
	def eleven   = 11
	def twelve   = 12
	def thirteen = 13
	def instanceHistory = []
	def instanceRecursionParameter = []
	static three = 3
	static four  = 4
	static six   = 6
	static eight = 8
	static ten   = 10
	static classHistory = []
	static classRecursionParameter = []
	
	static {
		classHistory.add(0)
	}
	
	public static void main(def args) {
		putThree()
		putFour()
		def victim = new Victim()
		victim.putOne()
		victim.putTwo()
	}
	
	public Victim() {
		instanceHistory.add(0)
	}
	
	public putOne() {
		instanceHistory.add(one)
		one
	}
	
	public putTwo() {
		instanceHistory.add(two)
		two
	}
	
	public static putThree() {
		classHistory.add(three)
		three
	}
	
	public static putFour() {
		classHistory.add(four)
		four
	}
	
	public putFive(first = 'a', second = 'b', third = 'c') {
		instanceHistory.add([five, first, second, third])
		[five, first, second, third]
	}
	
	public static putSix(first = 'a', second = 'b', third = 'c') {
		classHistory.add([six, first, second, third])
		[six, first, second, third]
	}
	
	public putSeven() {
		instanceHistory.add(seven)
		if (! instanceRecursionParameter.isEmpty()) {
			instanceRecursionParameter.pop()
			return putSeven()
		}
		seven
	}
	
	public static putEight() {
		classHistory.add(eight)
		if (! classRecursionParameter.isEmpty()) {
			classRecursionParameter.pop()
			return putEight()
		}
		eight
	}
	
	public putNine(first = 'a', second = 'b', third = 'c') {
		instanceHistory.add([nine, first, second, third])
		if (! instanceRecursionParameter.isEmpty()) {
			return putNine(*instanceRecursionParameter.pop())
		}
		[nine, first, second, third]
	}
	
	public static putTen(first = 'a', second = 'b', third = 'c') {
		classHistory.add([ten, first, second, third])
		if (! classRecursionParameter.isEmpty()) {
			return putTen(*classRecursionParameter.pop())
		}
		[ten, first, second, third]
	}
	
	public putEleven(first) {
		instanceHistory.add([eleven, first])
		[eleven, first]
	}
	
	public putTwelve(first, second) {
		instanceHistory.add([twelve, first, second])
		[twelve, first, second]
	}
	
	public putThirteen(first, second, third) {
		instanceHistory.add([thirteen, first, second, third])
		[thirteen, first, second, third]
	}
	
}
