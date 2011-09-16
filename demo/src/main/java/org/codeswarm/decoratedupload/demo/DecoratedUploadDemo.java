package org.codeswarm.decoratedupload.demo;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import org.codeswarm.decoratedupload.DecoratedUpload;

public class DecoratedUploadDemo implements EntryPoint {

  interface Binder extends UiBinder<IsWidget, DecoratedUploadDemo> { }
  private static final Binder BINDER = GWT.create(Binder.class);

  @UiField(provided = true) DecoratedUpload label, button1, button2, button3;

  @Override
  public void onModuleLoad() {
    label = new DecoratedUpload(new Label("Upload"));
    button1 = new DecoratedUpload("Upload");
    button2 = new DecoratedUpload("Upload");
    button3 = new DecoratedUpload("Upload");
    RootLayoutPanel.get().add(BINDER.createAndBindUi(this));
  }

}
