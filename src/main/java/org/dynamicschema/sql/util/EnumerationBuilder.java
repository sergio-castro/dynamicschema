package org.dynamicschema.sql.util;

import java.util.ArrayList;
import java.util.List;

public class EnumerationBuilder {
	public static final String DEFAULT_SEPARATOR = ", ";
	
	private String separator;
	
	private List members;
	
	public EnumerationBuilder() {
		this(DEFAULT_SEPARATOR);
	}
	
	public EnumerationBuilder(String separator) {
		this(new ArrayList(), separator);
	}
	
	public EnumerationBuilder(List members, String separator) {
		this.separator = separator;
		this.members = members;
	}

	public String getSeparator() {
		return separator;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

	public List getMembers() {
		return members;
	}

	public void setMembers(List members) {
		this.members = members;
	}
	
	public boolean add(Object member) {
		if(member == null || member.toString().isEmpty())
			return false;
		members.add(member);
		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		for(int i=0; i<getMembers().size(); i++) {
			sb.append(getMembers().get(i));
			if(i<getMembers().size()-1)
				sb.append(getSeparator());
		}
		return sb.toString();
	}
}
