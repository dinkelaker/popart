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
package de.tud.stg.example.aosd2009.phone;

import java.util.Date;

public class Period {
	
	Date begin;
	Date end;
	
	public Period(Date begin, Date end) {
		this.begin = begin;
		this.end = end;
	}
	
	public boolean isInPeriod(Date date) {
		return (begin.compareTo(date)<=0) && (date.compareTo(end)<=0);
	}
	
	public String toString() {
		return "Period(from:"+begin+",to:"+end+")";
	}
}


