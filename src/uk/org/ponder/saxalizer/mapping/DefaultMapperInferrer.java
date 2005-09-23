/*
 * Created on Sep 22, 2004
 */
package uk.org.ponder.saxalizer.mapping;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;

import uk.org.ponder.saxalizer.DefaultInferrible;
import uk.org.ponder.saxalizer.SAMSList;
import uk.org.ponder.saxalizer.SAXAccessMethodSpec;
import uk.org.ponder.stringutil.StringList;
import uk.org.ponder.stringutil.StringSet;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 *  
 */
public class DefaultMapperInferrer implements SAXalizerMapperInferrer {

  private HashMap collectionmap = new HashMap();
  private HashSet defaultiblemap = new HashSet();
  
  public DefaultMapperInferrer() {
    addCollectionType(StringList.class, String.class);
    addCollectionType(StringSet.class, String.class);
  }

  public Class getContaineeType(Class collectiontype) {
    return (Class)collectionmap.get(collectiontype);
  }
  
  
  public void addCollectionType(Class collectiontype, Class containeetype) {
    collectionmap.put(collectiontype, containeetype);
  }
  

  public void setDefaultInferrible(Class clazz) {
    defaultiblemap.add(clazz);
  }

  public boolean isDefaultInferrible(Class clazz) {
    return DefaultInferrible.class.isAssignableFrom(clazz)
    || defaultiblemap.contains(clazz);
  }
  
  public static int accessorType(Method method) {
    String methodname = method.getName();
    if (methodname.length() <= 3) return -1;
    if (methodname.startsWith("get")) {
      if (method.getParameterTypes().length == 0) {
        return SAXAccessMethodSpec.GET_METHOD;
      }
    }
    if (methodname.startsWith("set") || methodname.startsWith("add")) {
      if (method.getParameterTypes().length == 1 && method.getReturnType().equals(Void.TYPE))
      return SAXAccessMethodSpec.SET_METHOD;
    }
    return -1;
  }
// TODO: check that this actually agrees with beans spec!
// implement "isXxxx" methods for pedants.
  public static String deBean(String methodname) {
    return Character.toLowerCase(methodname.charAt(3))
        + methodname.substring(4);
  }

  /** Returns an new SAMS object with the given name.
   * @param tagname
   * @return
   */
  private SAXAccessMethodSpec byXMLNameSafe(SAMSList samslist, String tagname, Class clazz) {
    // must not fuse get and set at this point! Otherwise there will be
    // duplicate.
    SAXAccessMethodSpec spec = new SAXAccessMethodSpec();
    spec.xmlname = tagname;
    Class containeetype = getContaineeType(clazz);
    if (containeetype != null) {
      spec.clazz = containeetype;
    }
    return spec;
  }
  
  private static boolean isPublicNonStatic(int modifiers) {
    return Modifier.isPublic(modifiers) && !Modifier.isStatic(modifiers);
  }
  
  public SAXalizerMapperEntry inferEntry(Class clazz, 
      SAXalizerMapperEntry preventry) {
    SAXalizerMapperEntry togo = preventry == null? new SAXalizerMapperEntry() : preventry;
    togo.targetclass = clazz;
    SAMSList sams = togo.getSAMSList();
    Method[] methods = clazz.getMethods();

    for (int i = 0; i < methods.length; ++i) {
      int modifiers = methods[i].getModifiers(); 
      if (!isPublicNonStatic(modifiers)) continue;
      String methodname = methods[i].getName();
      if (methodname.equals("getClass")) continue;
      int methodtype = accessorType(methods[i]);
      if (methodtype != -1) {
        String basename = deBean(methodname);
        SAXAccessMethodSpec spec = byXMLNameSafe(sams, basename, methods[i].getReturnType());
        if (methodtype == SAXAccessMethodSpec.GET_METHOD) {
          spec.getmethodname = methodname;
        }
        else {
          spec.setmethodname = methodname;
        }
        togo.addNonDuplicate(spec);
      }
    }

    Field[] fields = clazz.getFields();
    for (int i = 0; i < fields.length; ++i) {
      String fieldname = fields[i].getName();
      int modifiers = fields[i].getModifiers();
      if (!isPublicNonStatic(modifiers)) continue;
      SAXAccessMethodSpec spec = byXMLNameSafe(sams, fieldname, fields[i].getType());
      spec.fieldname = fieldname;
      togo.addNonDuplicate(spec);
    }
    
    return togo;
  }

}