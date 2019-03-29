package org.openxava.util.xmlparse;

import java.io.InputStream;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 * @author Daniel López (greeneyed@dev.java.net)
 */
public class XMLEntityResolver implements EntityResolver
{
  public static final String OPENXAVA_DTD_ID = "WEB-INF/classes/dtds";
  /**
 *
 */
  public InputSource resolveEntity(String publicId, String systemId)
  {
    if (systemId != null && systemId.indexOf(OPENXAVA_DTD_ID) != -1)
    {
      String resourceName = systemId.substring(systemId.indexOf(OPENXAVA_DTD_ID)
          + OPENXAVA_DTD_ID.length() - 5);
      InputStream theStream = XMLEntityResolver.class
          .getResourceAsStream(resourceName);
      if (theStream != null)
      {
        // return a special input source
        InputSource theIS = new InputSource(theStream);
        theIS.setSystemId(systemId);
        theIS.setPublicId(publicId);
        return theIS;
      }
      else
      {
        return null;
      }
    }
    else
    {
      // use the default behaviour
      return null;
    }
  }
}
