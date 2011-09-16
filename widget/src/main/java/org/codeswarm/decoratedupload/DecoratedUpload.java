/*
 * Copyright 2010 Manuel Carrasco Monino. (manolo at apache/org)
 * http://code.google.com/p/gwtupload
 *
 * Copyright 2011 Codeswarm
 * http://github.com/codeswarm
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.codeswarm.decoratedupload;

import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasMouseOutHandlers;
import com.google.gwt.event.dom.client.HasMouseOverHandlers;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.HasName;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A widget which hides a FileUpload showing a clickable widget (normally a button).
 */
public class DecoratedUpload extends Composite implements HasText, HasName, HasChangeHandlers {

  /**
   * A FileUpload which implements onChange, onMouseOver and onMouseOut events.
   *
   * Note: although FileUpload implements HasChangeHandlers in version Gwt 2.0.x,
   * we put it here in order to be compatible with older Gwt versions.
   *
   */
  public static class FileUploadWithMouseEvents extends FileUpload implements HasMouseOverHandlers, HasMouseOutHandlers, HasChangeHandlers {

    public HandlerRegistration addChangeHandler(ChangeHandler handler) {
      return addDomHandler(handler, ChangeEvent.getType());
    }

    public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
      return addDomHandler(handler, MouseOutEvent.getType());
    }

    public HandlerRegistration addMouseOverHandler(final MouseOverHandler handler) {
      return addDomHandler(handler, MouseOverEvent.getType());
    }
  }

  /**
   * An abstract class which is the base for specific browser implementations.
   */
  private abstract static class DecoratedFileUploadImpl {

    private static final int DEFAULT_HEIGHT = 15;
    private static final int DEFAULT_WIDTH = 100;
    protected Widget button;
    protected AbsolutePanel container;
    protected FileUploadWithMouseEvents input;
    protected StyleNames style;

    public void init(AbsolutePanel container, FileUploadWithMouseEvents input, StyleNames style) {
      this.container = container;
      this.input = input;
      this.style = style;
    }

    public void setSize(String width, String height) {
       button.setSize(width, height);
       container.setSize(width, height);
    }

    protected int width = 0, height = 0;

    public void onAttach() {
      if (width != 0 && height != 0) {
        container.setSize(width + "px", height + "px");
      } else {
        resize();
      }
    }

    // TODO: computed size
    public void resize() {
      if (button != null) {
        int w = button.getElement().getOffsetWidth();
        int h = button.getElement().getOffsetHeight();
        if (w <= 0) {
          String ws = button.getElement().getStyle().getWidth();
          if (ws != null) {
            try {
              w = Integer.parseInt(ws.replaceAll("[^\\d]", ""));
            } catch (Exception e) {
            }
          }
          if (w <= 0) {
            w = DEFAULT_WIDTH;
          } else {
            width = w;
          }
        }
        if (h <= 0) {
          String hs = button.getElement().getStyle().getHeight();
          if (hs != null) {
            try {
              h = Integer.parseInt(hs.replaceAll("[^\\d]", ""));
            } catch (Exception e) {
            }
          }
          if (h <= 0) {
            h = DEFAULT_HEIGHT;
          } else {
            height = h;
          }
        }
        container.setSize(w + "px", h + "px");
      }
    }

    public void setButton(Widget widget) {
      this.button = widget;
      if (button instanceof HasMouseOverHandlers) {
        ((HasMouseOverHandlers) button).addMouseOverHandler(new MouseOverHandler() {
          public void onMouseOver(MouseOverEvent event) {
            button.addStyleDependentName(STYLE_BUTTON_OVER_SUFFIX);
            if (style != null) container.addStyleName(style.over());
          }
        });
      }
      if (button instanceof HasMouseOutHandlers) {
        ((HasMouseOutHandlers) button).addMouseOutHandler(new MouseOutHandler() {
          public void onMouseOut(MouseOutEvent event) {
            button.removeStyleDependentName(STYLE_BUTTON_OVER_SUFFIX);
            if (style != null) container.removeStyleName(style.over());
          }
        });
      }
    }

  }

  /**
   * Implementation for browsers which support the click() method:
   * IE, Chrome, Safari
   *
   * The hack here is to put the customized button
   * and the file input statically positioned in an absolute panel.
   * This panel has the size of the button, and the input is not shown
   * because it is placed out of the width and height panel limits.
   *
   */
  @SuppressWarnings("unused")
  private static class DecoratedFileUploadImplClick extends DecoratedFileUploadImpl {

    private static HashMap<Widget, HandlerRegistration> clickHandlerCache = new HashMap<Widget, HandlerRegistration>();

    private static native void clickOnInputFile(Element elem) /*-{
      elem.click();
    }-*/;

    public void init(AbsolutePanel container, FileUploadWithMouseEvents input, StyleNames style) {
      super.init(container, input, style);
      container.add(input, 500, 500);
      DOM.setStyleAttribute(input.getElement(), "top", "-10000px");
      DOM.setStyleAttribute(input.getElement(), "left", "-10000px");
    }

    public void setButton(Widget widget) {
      super.setButton(widget);
      HandlerRegistration clickRegistration = clickHandlerCache.get(widget);
      if (clickRegistration != null) {
        clickRegistration.removeHandler();
      }
      if (button != null) {
        if (button instanceof HasClickHandlers) {
          clickRegistration = ((HasClickHandlers) button).addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
              clickOnInputFile(input.getElement());
            }
          });
          clickHandlerCache.put(widget, clickRegistration);
        }
      }
    }
  }

  /**
   * Implementation for browsers which do not support the click() method:
   * FF, Opera
   *
   * The hack here is to place the customized button and the file input positioned
   * statically in an absolute panel which has size of the button.
   * The file input is wrapped into a transparent panel, which also has the button
   * size and is placed covering the customizable button.
   *
   * When the user puts his mouse over the button and clicks on it, what really
   * happens is that the user clicks on the transparent file input showing
   * the choose file dialog.
   *
   */
  @SuppressWarnings("unused")
  private static class DecoratedFileUploadImplNoClick extends DecoratedFileUploadImpl {

    private SimplePanel wrapper;

    public void init(AbsolutePanel container, FileUploadWithMouseEvents input, StyleNames style) {
      super.init(container, input, style);
      wrapper = new SimplePanel();
      wrapper.add(input);
      container.add(wrapper, 0, 0);
      wrapper.setStyleName("wrapper");

      // Not using the GWT 2.0.x way to set Style attributes in order to be
      // compatible with old GWT releases
      DOM.setStyleAttribute(wrapper.getElement(), "textAlign", "left");
      DOM.setStyleAttribute(wrapper.getElement(), "zIndex", "1");
      DOM.setStyleAttribute(input.getElement(), "marginLeft", "-1500px");
      DOM.setStyleAttribute(input.getElement(), "fontSize", "500px");
      DOM.setStyleAttribute(input.getElement(), "borderWidth", "0px");
      DOM.setStyleAttribute(input.getElement(), "opacity", "0");
      DOM.setElementAttribute(input.getElement(), "size", "1");

      // Trigger over and out handlers which already exist in the covered button.
      input.addMouseOverHandler(new MouseOverHandler() {
        public void onMouseOver(MouseOverEvent event) {
          if (button != null) {
            button.fireEvent(event);
          }
        }
      });
      input.addMouseOutHandler(new MouseOutHandler() {
        public void onMouseOut(MouseOutEvent event) {
          if (button != null) {
            button.fireEvent(event);
          }
        }
      });
    }

    public void onAttach() {
      super.onAttach();
      wrapper.setSize(width + "px", height + "px");
    }

    public void resize() {
      super.resize();
      wrapper.setSize(width + "px", height + "px");
    }

    public void setSize(String width, String height) {
      super.setSize(width, height);
      wrapper.setSize(width, height);
    }
  }

  private static final String STYLE_BUTTON_OVER_SUFFIX = "over";
  protected Widget button;
  protected AbsolutePanel container;
  protected FileUploadWithMouseEvents input = new FileUploadWithMouseEvents();
  protected boolean reuseButton = false;
  private DecoratedFileUploadImpl impl;
  private String text = "";
  private final StyleNames style;

  public interface StyleNames {
    String container();
    String disabled();
    String over();
  }

  public static StyleNames defaultStyle() {
    DecoratedUploadResources resources = GWT.create(DecoratedUploadResources.class);
    DecoratedUploadResources.Style style = resources.style();
    style.ensureInjected();
    return style;
  }

  /**
   * Default constructor, it renders a default button with the provided
   * text when the element is attached.
   */
  public DecoratedUpload(String text, StyleNames style) {
    this.style = style;
    impl = GWT.create(DecoratedFileUploadImpl.class);
    container = new AbsolutePanel();
    if (style != null) container.addStyleName(style.container());
    initWidget(container);
    impl.init(container, input, style);
    this.text = text;
  }

  public DecoratedUpload(String text) {
    this(text, null);
  }

  public DecoratedUpload() {
    this("");
  }

  public DecoratedUpload(StyleNames style) {
    this("", style);
  }

  /**
   * Constructor which uses the provided widget as the button where the
   * user has to click to show the browse file dialog.
   * The widget has to implement the HasClickHandlers interface.
   */
  public DecoratedUpload(Widget button, StyleNames style) {
    this(style);
    setButton(button);
  }

  public DecoratedUpload(Widget button) {
    this(button, null);
  }

  /**
   * Add a handler which will be fired when the user selects a file.
   */
  public HandlerRegistration addChangeHandler(ChangeHandler handler) {
    return input.addChangeHandler(handler);
  }

  /**
   * Return the file name selected by the user.
   */
  public String getFilename() {
    return input.getFilename();
  }

  /**
   * Return the original FileUpload wrapped by this decorated widget.
   */
  public FileUpload getFileUpload() {
    return input;
  }

  /**
   * Return the name of the widget.
   */
  public String getName() {
    return input.getName();
  }

  /**
   * Return the text shown in the clickable button.
   */
  public String getText() {
    return text;
  }

  /**
   * Return this widget instance.
   */
  public Widget getWidget() {
    return this;
  }

  /**
   * Return whether the input is enabled.
   */
  public boolean isEnabled() {
    return input.isEnabled();
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.Composite#onAttach()
   */
  @Override
  public void onAttach() {
    super.onAttach();
    if (button == null) {
      button = new Button(text);
      setButton(button);
    }
    new Timer(){
      public void run() {
        impl.onAttach();
      }
    }.schedule(5);
  }

  /**
   * Set the button the user has to click on to show the browse dialog.
   */
  public void setButton(Widget button) {
    assert button instanceof HasClickHandlers : "Button should implement HasClickHandlers";
    if (this.button != null) {
      container.remove(this.button);
    }
    this.button = button;
    container.add(button, 0, 0);
    impl.setButton(button);
    updateSize();
  }

  /**
   * Set the button size.
   */
  public void setButtonSize(String width, String height) {
    button.setSize(width, height);
    updateSize();
  }

  /**
   * Enable or disable the FileInput.
   */
  public void setEnabled(boolean enabled) {
    input.setEnabled(enabled);
    if (style != null) container.setStyleName(style.disabled(), !enabled);
  }

  /**
   * Set the widget name.
   */
  public void setName(String fieldName) {
    input.setName(fieldName);
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.UIObject#setSize(java.lang.String, java.lang.String)
   */
  public void setSize(String width, String height){
    setButtonSize(width, height);
  }

  /**
   * Set the text of the button.
   */
  public void setText(String text) {
    this.text = text;
    if (button instanceof HasText) {
      ((HasText) button).setText(text);
      updateSize();
    }
  }

  /**
   * Resize the absolute container to match the button size.
   */
  public void updateSize() {
    impl.resize();
  }

}
