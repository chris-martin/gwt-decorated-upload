package org.codeswarm.decoratedupload.demo;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

public interface DecoratedUploadDemoResources extends ClientBundle {

  @Source("DecoratedUploadDemo.css")
  Style style();

  @Source("open.png")
  ImageResource open();

  @Source("save.png")
  ImageResource save();

  interface Style extends CssResource {
    String root();
    String section();
    String horizontal();
    String vertical();
    String open();
    String save();
    String iconButton();
    String margin();
  }

}
