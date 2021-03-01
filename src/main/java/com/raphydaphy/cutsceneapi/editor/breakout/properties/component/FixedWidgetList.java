package com.raphydaphy.cutsceneapi.editor.breakout.properties.component;

import com.raphydaphy.cutsceneapi.editor.breakout.properties.component.event.WidgetListHeightUpdatedEvent;
import org.liquidengine.legui.component.FlexPanel;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.listener.processor.EventProcessorProvider;
import org.liquidengine.legui.style.Style;
import org.liquidengine.legui.system.context.Context;

import java.util.ArrayList;
import java.util.List;

public class FixedWidgetList extends FlexPanel {
  private final Context context;
  private float width, height;
  private List<FixedWidget> widgets;

  public FixedWidgetList(Context context, float width) {
    super();

    this.context = context;
    this.widgets = new ArrayList<>();

    this.setWidth(width).setHeight(0);
    this.getStyle().setDisplay(Style.DisplayType.MANUAL);
    this.getStyle().getBackground().setColor(0, 1, 0, 0.2f);
  }

  public void addWidget(FixedWidget widget) {
    widget.setSize(this.width, widget.getMaxHeight());

    this.widgets.add(widget);
    this.add(widget);
    this.updateWidgets();

    widget.getMinimizeButton().getListenerMap().addListener(MouseClickEvent.class, (e) -> {
      if (e.getAction() == MouseClickEvent.MouseClickAction.CLICK) {
        this.onWidgetMinimized(widget, widget.isMinimized());
      }
    });

    widget.addWidgetCloseEventListener((e) -> {
      this.onWidgetClosed(widget);
    });
  }

  public FixedWidgetList setHeight(float height) {
    float oldHeight = this.height;
    this.height = height;
    this.getStyle().setHeights(this.height);
    EventProcessorProvider.getInstance().pushEvent(new WidgetListHeightUpdatedEvent<>(this, this.context, this.getFrame(), oldHeight, this.height));
    return this;
  }

  public FixedWidgetList setWidth(float width) {
    this.width = width;
    this.getStyle().setWidths(this.width);
    for (FixedWidget widget : this.widgets) {
      widget.getStyle().setWidths(this.width);
      widget.getSize().x = width;
    }
    return this;
  }

  private void onWidgetMinimized(FixedWidget widget, boolean minimized) {
    this.updateWidgets();
  }

  private void onWidgetClosed(FixedWidget widget) {
    this.remove(widget);
    this.widgets.remove(widget);

    this.updateWidgets();
  }

  private void updateWidgets() {
    float y = 1;
    for (FixedWidget widget : this.widgets) {
      widget.setPosition(0, y);
      widget.setSize(this.width, widget.getCurrentHeight());
      widget.getStyle().setWidths(this.width);
      y += widget.getCurrentHeight() + 1;
    }
    this.setHeight(y);
  }
}
