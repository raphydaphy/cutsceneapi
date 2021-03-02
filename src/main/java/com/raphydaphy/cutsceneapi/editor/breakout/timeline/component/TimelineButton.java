package com.raphydaphy.cutsceneapi.editor.breakout.timeline.component;

import com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.style.TimelineStyle;
import com.raphydaphy.shaded.org.joml.Vector4f;
import org.liquidengine.legui.component.Button;
import org.liquidengine.legui.component.optional.align.HorizontalAlign;
import org.liquidengine.legui.component.optional.align.VerticalAlign;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.icon.CharIcon;
import org.liquidengine.legui.listener.MouseClickEventListener;
import org.liquidengine.legui.style.Style;
import org.liquidengine.legui.style.font.FontRegistry;

public class TimelineButton extends Button {

  public TimelineButton(char iconCode, TimelineStyle style) {
    super("");

    this.setTabFocusable(false);
    this.getStyle().setWidths(style.getControlButtonSize().x).setHeights(style.getControlButtonSize().y);
    this.getStyle().setPosition(Style.PositionType.RELATIVE).setBorder(null);
    this.getStyle().setVerticalAlign(VerticalAlign.MIDDLE).setHorizontalAlign(HorizontalAlign.CENTER);

    CharIcon icon = createIcon(iconCode, style, style.getControlButtonIconColor());
    CharIcon hoverIcon = createIcon(iconCode, style, style.getHoveredControlButtonIconColor());

    this.getStyle().getBackground().setColor(style.getControlButtonBackgroundColor()).setIcon(icon);
    this.getHoveredStyle().getBackground().setColor(style.getHoveredControlButtonBackgroundColor()).setIcon(hoverIcon);
    this.getPressedStyle().getBackground().setColor(style.getPressedControlButtonBackgroundColor()).setIcon(hoverIcon);
  }

  private static CharIcon createIcon(char iconCode, TimelineStyle style, Vector4f color) {
    CharIcon icon = new CharIcon(style.getControlButtonIconSize(), FontRegistry.FONT_AWESOME_ICONS, iconCode, color);
    icon.setHorizontalAlign(HorizontalAlign.CENTER).setVerticalAlign(VerticalAlign.MIDDLE);
    return icon;
  }

  public TimelineButton addClickListener(MouseClickEventListener listener) {
    this.getListenerMap().addListener(MouseClickEvent.class, (e) -> {
      if (e.getAction() == MouseClickEvent.MouseClickAction.CLICK) listener.process(e);
    });
    return this;
  }
}
