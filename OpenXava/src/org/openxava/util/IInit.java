package org.openxava.util;


/**
 * With the posibility of be initiated. <p> 
 *
 * This interface is thought to be used with {@link Factory}.
 * Although it can be used in others situations, where you
 * create a object of unknow class y you need initiate it.<p>
 * 
 * A possible example of use: 
 * <pre>
 * IInit obj = (IInit) myClass.newInstance();
 * obj.init("obj");
 * </pre>
 * The object that implements this interface generally
 * have a default constructor, and you initiate the object
 * with {@link #init}.<br>
 *
 * @author  Javier Paniza
 */

public interface IInit {

  /**
   * Initiate the object. <p> 
   * 
   * The init process must to be in this metho, and not in
   * constructor.<br>
   * It possible to specefy a name, thus you can have
   * different configurations in the same object type.
   * This name can be used to read a properties file, with
   * if/else or another technique.
   *
   * @param name Identifier name used on init. Can be null.
   * @exception InitException  Some problem on initiate.
   */
  void init(String name) throws InitException;
  
}
