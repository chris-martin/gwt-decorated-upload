package org.codeswarm.decoratedupload.demo;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RootPanel;

public class DecoratedUploadDemo implements EntryPoint {

  interface Binder extends UiBinder<IsWidget, DecoratedUploadDemo> { }
  private static final Binder BINDER = GWT.create(Binder.class);

  private static final DecoratedUploadDemoResources RESOURCES =
    GWT.create(DecoratedUploadDemoResources.class);
  private static final DecoratedUploadDemoResources.Style STYLE = RESOURCES.style();
  static { STYLE.ensureInjected(); }

  @UiField(provided = true) DecoratedUploadDemoResources.Style style = STYLE;

  @Override
  public void onModuleLoad() {
    RootPanel.get().add(BINDER.createAndBindUi(this));
  }

}
