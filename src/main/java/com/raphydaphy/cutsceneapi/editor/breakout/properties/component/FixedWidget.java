package com.raphydaphy.cutsceneapi.editor.breakout.properties.component;

import com.raphydaphy.shaded.org.joml.Vector4f;
import org.liquidengine.legui.component.Component;
import org.liquidengine.legui.component.Widget;
import org.liquidengine.legui.style.Style;
import org.liquidengine.legui.style.border.SimpleLineBorder;
import org.liquidengine.legui.style.color.ColorConstants;
import org.liquidengine.legui.style.flex.FlexStyle;
import org.liquidengine.legui.style.font.FontRegistry;

public class FixedWidget extends Widget {
  private float height;
  private float titleHeight;

  public FixedWidget(String title, float height) {
    this(title, height, 30, 20);
  }

  public FixedWidget(String title, float height, float titleSize, float fontSize) {
    super(title);

    this.height = height;
    this.titleHeight = titleSize;

    this.setResizable(false).setDraggable(false);
    this.getStyle().setShadow(null);

    float b = 120;
    this.getStyle().setBorder(new SimpleLineBorder(new Vector4f(b / 255f, b / 255f, b / 255f, 1), 1f));

    this.getTitleContainer().getFlexStyle().setAlignItems(FlexStyle.AlignItems.CENTER);
    this.getTitle().getStyle().setFont(FontRegistry.ROBOTO_BOLD);

    this.setTitleSize(titleSize).setFontSize(fontSize);

    Component container = this.getContainer();
    container.getStyle().setDisplay(Style.DisplayType.FLEX);
    container.getFlexStyle().setFlexDirection(FlexStyle.FlexDirection.COLUMN);
  }

  public FixedWidget setFontSize(float fontSize) {
    this.getTitleContainer().getStyle().setFontSize(fontSize);
    return this;
  }

  public FixedWidget setTitleSize(float titleSize) {
    this.getTitleContainer().getStyle().setHeights(titleSize);
    this.getMinimizeButton().getStyle().setHeights(titleSize).setWidths(titleSize);
    return this;
  }

  public float getMaxHeight() {
    return this.height;
  }

  public float getTitleHeight() {
    return this.titleHeight;
  }

  public float getCurrentHeight() {
    return this.isMinimized() ? this.getTitleHeight() : this.getMaxHeight();
  }
}
