package org.openxava.actions;

import java.util.*;

import javax.inject.*;

import org.openxava.filters.*;
import org.openxava.tab.*;

import lombok.*;

public class GetEventsAction extends TabBaseAction{
	
	@Getter @Setter
	String month;
	
	@Inject
	Tab tab;
	
	
	@Override
	public void execute() throws Exception {
		// TODO Auto-generated method stub
		DateFilter dateFilter = new DateFilter();
        Calendar calendar = Calendar.getInstance();
        Date firstDayOfMonth = getFirstDayOfMonth(calendar.getTime());
        Date lastDayOfMonth = getLastDayOfMonth(calendar.getTime());
		dateFilter.setStart(firstDayOfMonth);
		dateFilter.setEnd(lastDayOfMonth);
		tab.setFilter(dateFilter);
		tab.setBaseCondition("date between ? and ?");
		
		System.out.println("ACCION");
		//System.out.println(month);
	}
	
	
    private static Date getFirstDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1); // resta 1 mes
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    private static Date getLastDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }
    
    
}
