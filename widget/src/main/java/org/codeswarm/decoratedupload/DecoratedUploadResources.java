package org.codeswarm.decoratedupload;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

public interface DecoratedUploadResources extends ClientBundle {

  @Source("DecoratedUpload.css")
  Style style();

  interface Style extends CssResource, DecoratedUpload.StyleNames {
    String container();
    String disabled();
  }

}
