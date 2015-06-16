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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class HolidayDB {
	
	public static long ONE_DAY = (24*3600000);
	
	public static HashMap<String,List<Period>> nameToPeriodList= new HashMap<String,List<Period>>();
	
	static {
		Date today = new Date();
		//today = new Date(today.getTime()+10*HolidayDB.ONE_DAY);
		Date todayMinus3 = new Date(today.getTime()-3*ONE_DAY);
		Date todayPlus3 = new Date(today.getTime()+3*ONE_DAY);
		Period sevenDays = new Period(todayMinus3,todayPlus3);
		addHolidayFor("Tom",sevenDays);
	}
	
	public static void addHolidayFor(String name, Period period) {
		List<Period> holidayList = nameToPeriodList.get(name);
		if (holidayList == null) {
			holidayList = new LinkedList<Period>();
			nameToPeriodList.put(name, holidayList);
		}
		holidayList.add(period);
	}
	
	public static boolean hasHoliday(String name, Date date) {
		List<Period> holidayList = nameToPeriodList.get(name);
		if (holidayList == null) return false;
		Iterator<Period> it = holidayList.iterator();
		boolean foundHoliday = false;
		while (it.hasNext()) {
			Period p = it.next();
			if (p.isInPeriod(date)) {
				System.err.println("HolidayDB.hasHoliday: name="+name+" period="+p);
				foundHoliday = true;
				break;
			}
		}
		return foundHoliday;
	}
	
	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		System.out.println("hasHoliday(today="+new Date()+"): "+hasHoliday("Tom",new Date()));
		System.out.println("hasHoliday(lastWeek="+new Date(new Date().getTime()-(3*ONE_DAY))+"): "+hasHoliday("Tom",new Date(new Date().getDate()-7)));
		System.out.println("hasHoliday(nextWeek="+new Date(new Date().getTime()+(3*ONE_DAY))+"): "+hasHoliday("Tom",new Date(new Date().getDate()+7)));
	}
	

}
