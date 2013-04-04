package org.dynamicschema.sql;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.dynamicschema.reification.Table;

public abstract class RelationCondition {

	public static final String EVAL_METHOD_NAME = "eval";
	
	public SqlCondition eval(List<Table> tables) {
		return eval(tables.toArray(new Table[]{}));
	}
	
	public SqlCondition eval(Table ...tables) {
		List<Class> paramClasses = new ArrayList<Class>();
		Method m = null;
		for(int i=0; i<tables.length; i++)
			paramClasses.add(Table.class);
		try {
			m = this.getClass().getMethod(EVAL_METHOD_NAME, paramClasses.toArray(new Class[]{}));
		} catch (SecurityException e) {
			throw(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException("A condition should be provided", e);
		}
		try {
			return (SqlCondition) m.invoke(this, tables);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
	
	private boolean allArgumentsAreTables(Method method) {
		for(Class parameterClass : method.getParameterTypes()) {
			if(!Table.class.isAssignableFrom(parameterClass))
				return false;
		}
		return true;
	}
	
	public Method getCustomEvalMethod() {
		Method[] methods = getClass().getMethods();
		Method matchedMethod = null;
		for(Method method : methods) {
			if(method.getName().equals(EVAL_METHOD_NAME) && allArgumentsAreTables(method))
				matchedMethod = method;
		}
		return matchedMethod;
	}
	
	/*
	public static class A {
		
		public boolean eval(Table ...tables){
			List<Class> paramClasses = new ArrayList<Class>();
			Method m = null;
			for(int i=0; i<tables.length; i++)
				paramClasses.add(Table.class);
			try {
				m = this.getClass().getMethod("eval", paramClasses.toArray(new Class[]{}));
			} catch (SecurityException e) {
				throw(e);
			} catch (NoSuchMethodException e) {
				throw new RuntimeException("A condition should be provided", e);
			}
			try {
				return (Boolean) m.invoke(this, tables);
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		}
		
		
	}
	
	public static class B extends A {
		public boolean eval(Table t){
			System.out.println(t);
			return true;
		}
	}
	
	public static void main(String[] args) {
		Table t = null;
		System.out.println(new B().eval(new Table[]{t}));
	}
	*/

}