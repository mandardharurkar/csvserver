package com.gslab.poc.csvserver.model;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;

/**
 * Base for Beans used within CME.
 *
 *
 */

public class BeanBase implements Cloneable, Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public String toLog() {
		final StringBuffer results = new StringBuffer();
		dumpObject(results, this);
		results.append("} ");
        return results.toString();
	}



	@SuppressWarnings("rawtypes")
	public void dumpObject(StringBuffer results, Object object) {
		final Class<BeanBase> baseLogClass = BeanBase.class;
		results.append("{");
		if (object instanceof BeanBase) {
			try {
		        final BeanInfo info = Introspector.getBeanInfo( object.getClass(), baseLogClass);
		        for ( final PropertyDescriptor pd : info.getPropertyDescriptors() ) {
		            final String propName = pd.getName();
		            final Method getter = pd.getReadMethod();
		            final Field field = getField(object.getClass(), propName);
		            final Object value = getter.invoke(object);
		            results.append(propName);
		            results.append(": ");
		            if (value == null) {
                		results.append("{null} ");
		            } else if (field != null) {
		            	try {
		            		int length = value.toString().length();
		            		if (length > 20) {
                                length = 20;
                            }
		            		results.append('{');
		            		for (int i = 0; i < length; i++) {
                                results.append('*');
                            }
							results.append("} ");
		            	} catch (final Exception e) {
		            		results.append("{**LoggingSuppressed**} ");
		            	}
	            	} else {
    	            	dumpObject(results, value);
	            	}

				}
			} catch(final Exception e) {
				results.append("Unable to dump " + object.toString() + ". Exception: " + e.toString());
			}
		} else if (object instanceof Collection) {
			for (final Object element : ((Collection) object)) {
				results.append("[");
				dumpObject(results, element);
				results.append("] ");
			}
		} else {
			results.append(object.toString());
		}
		results.append("} ");
	}

	@SuppressWarnings("rawtypes")
	public static Field getField(Class theClass, String name) {
		try {
			return theClass.getDeclaredField(name);
		} catch (final NoSuchFieldException fnfe) {
			final Class superclass = theClass.getSuperclass();
			if (superclass != null) {
				return getField(superclass, name);
			} else {
				return null;
			}
		}
	}

        @Override
        public  BeanBase clone(){
        return new BeanBase();
        }
}
