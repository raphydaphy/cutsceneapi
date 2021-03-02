package com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.button;

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
  protected TimelineStyle timelineStyle;
  protected CharIcon[] iconSet;

  public TimelineButton(char iconCode, TimelineStyle style) {
    super("");

    this.timelineStyle = style;

    this.setTabFocusable(false);
    this.getStyle().setWidths(style.getControlButtonSize().x).setHeights(style.getControlButtonSize().y);
    this.getStyle().setPosition(Style.PositionType.RELATIVE).setBorder(null);
    this.getStyle().setVerticalAlign(VerticalAlign.MIDDLE).setHorizontalAlign(HorizontalAlign.CENTER);

    this.iconSet = this.createIconSet(iconCode);
    this.useIconSet(this.iconSet);
  }

  protected CharIcon[] createIconSet(char iconCode) {
    CharIcon icon = createIcon(iconCode, this.timelineStyle.getControlButtonIconColor());
    CharIcon hoverIcon = createIcon(iconCode, this.timelineStyle.getHoveredControlButtonIconColor());

    return new CharIcon[] {icon, hoverIcon};
  }

  protected void useIconSet(CharIcon[] iconSet) {
    TimelineStyle style = this.timelineStyle;

    this.getStyle().getBackground().setColor(style.getControlButtonBackgroundColor()).setIcon(iconSet[0]);
    this.getHoveredStyle().getBackground().setColor(style.getHoveredControlButtonBackgroundColor()).setIcon(iconSet[1]);

    this.getPressedStyle().getBackground().setColor(style.getPressedControlButtonBackgroundColor());
    this.getPressedStyle().getBackground().setIcon(iconSet.length >= 3 ? iconSet[2] : iconSet[1]);
  }

  protected CharIcon createIcon(char iconCode, Vector4f color) {
    return (CharIcon) new CharIcon(
      this.timelineStyle.getControlButtonIconSize(),
      FontRegistry.FONT_AWESOME_ICONS, iconCode, color
    ).setHorizontalAlign(HorizontalAlign.CENTER).setVerticalAlign(VerticalAlign.MIDDLE);
  }

  public TimelineButton addClickListener(MouseClickEventListener listener) {
    this.getListenerMap().addListener(MouseClickEvent.class, (e) -> {
      if (e.getAction() == MouseClickEvent.MouseClickAction.CLICK) listener.process(e);
    });
    return this;
  }
}
