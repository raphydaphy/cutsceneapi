package com.raphydaphy.cutsceneapi.editor.breakout.properties;

import com.raphydaphy.cutsceneapi.CutsceneAPI;
import com.raphydaphy.cutsceneapi.editor.breakout.properties.component.*;
import com.raphydaphy.cutsceneapi.editor.breakout.properties.component.event.WidgetListHeightUpdatedEvent;
import com.raphydaphy.cutsceneapi.editor.breakout.properties.component.prop.InlineProp;
import com.raphydaphy.cutsceneapi.editor.breakout.properties.component.prop.MultiNumericInlineProp;
import com.raphydaphy.cutsceneapi.editor.breakout.properties.component.prop.NumericInlineProp;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.liquidengine.legui.component.*;
import org.liquidengine.legui.component.optional.align.HorizontalAlign;
import org.liquidengine.legui.event.WindowSizeEvent;
import org.liquidengine.legui.style.font.FontRegistry;
import org.liquidengine.legui.system.context.Context;

import java.util.Set;

import static org.liquidengine.legui.style.Style.*;
import static org.liquidengine.legui.style.flex.FlexStyle.*;

public class PropertiesGUI extends Panel {
  public final SelectBox<String> objectSelector;
  public final Button addObjectButton;

  public final ScrollablePanel scrollArea;
  public final FixedWidgetList widgetList;

  public final NumericInlineProp framerateInput;
  public final NumericInlineProp lengthInput;

  public final MultiNumericInlineProp positionInput;
  public final MultiNumericInlineProp rotationInput;

  public PropertiesGUI(Context context, int height) {
    super(0, 0, 400, height);

    this.getStyle().setDisplay(DisplayType.FLEX).getFlexStyle().setFlexDirection(FlexDirection.COLUMN);

    Label title = new Label("Cutscene Objects");
    {
      title.getStyle().enableFlex(400, 30).setMargin(5, 5, 0, 5);
      title.getStyle().setFontSize(30f).setFont(FontRegistry.ROBOTO_BOLD);
      this.add(title);
    }

    FlexPanel addObjectPanel = new FlexPanel();
    {
      addObjectPanel.getStyle().setMaxWidth(Float.MAX_VALUE).setHeights(40);
      addObjectPanel.getFlexStyle().setFlexGrow(1).setAlignItems(AlignItems.CENTER);

      this.objectSelector = new SelectBox<>();
      this.objectSelector.getStyle().enableFlex(150, 25).setMargin(5, 5);
      this.objectSelector.setElementHeight(25);
      this.objectSelector.getSelectionButton().getStyle().setHorizontalAlign(HorizontalAlign.LEFT).setPadding(5, 15);

      this.objectSelector.addElement(ObjectType.MOB.getName());
      this.objectSelector.addElement(ObjectType.PARTICLE_SOURCE.getName());

      this.addObjectButton = new Button("Add to Scene");
      this.addObjectButton.getStyle().enableFlex(80, 25);

      addObjectPanel.add(this.objectSelector).add(this.addObjectButton);

      this.add(addObjectPanel);
    }

    this.scrollArea = new ScrollablePanel();
    {
      float TOP_WIDTH = 75;

      this.scrollArea.getStyle().enableFlex( 400, height - TOP_WIDTH);
      this.scrollArea.setHorizontalScrollBarVisible(false);

      float scrollBarWidth = (float)this.scrollArea.getVerticalScrollBar().getStyle().getWidth().get();

      Component container = this.scrollArea.getContainer();
      container.setSize(400 - scrollBarWidth, 0);
      container.getStyle().setDisplay(DisplayType.FLEX).setShadow(null);

      this.widgetList = new FixedWidgetList(context, 400 - scrollBarWidth);
      container.add(this.widgetList);

      this.widgetList.getListenerMap().addListener(WidgetListHeightUpdatedEvent.class, (e) -> {
        container.setSize(container.getSize().x, e.getNewHeight());
      });

      this.scrollArea.getListenerMap().addListener(WindowSizeEvent.class, (e) -> {
        this.scrollArea.getStyle().setWidths(e.getWidth()).setHeights(e.getHeight() - TOP_WIDTH);
        float innerWidth = e.getWidth() - scrollBarWidth;

        container.setSize(innerWidth, container.getSize().y);
        this.widgetList.setWidth(innerWidth);
      });

      this.add(this.scrollArea);
    }

    FixedWidget cutsceneProps = new FixedWidget("Cutscene Properties", 90);
    {
      cutsceneProps.setCloseable(false);

      this.lengthInput = new NumericInlineProp("Length (Frames)", 1200);
      this.framerateInput = new NumericInlineProp("Frames Per Second", 30);

      cutsceneProps.getContainer().add(lengthInput).add(framerateInput);
      this.widgetList.addWidget(cutsceneProps);
    }

    FixedWidget camera = new FixedWidget("Camera", 90);
    {
      camera.setCloseable(false);

      this.positionInput = new MultiNumericInlineProp("Position", 60,"X", "Y", "Z");
      this.rotationInput = new MultiNumericInlineProp("Rotation",80,"Pitch", "Yaw");
      camera.getContainer().add(this.positionInput).add(this.rotationInput);

      this.widgetList.addWidget(camera);
    }
  }

  public void addParticleSource() {
    FixedWidget particleSource = new FixedWidget("Particle Source", 120);

    InlineProp typeProp = new InlineProp("Type");

    SelectBox<String> typeSelector = new SelectBox<>();
    typeSelector.getStyle().enableFlex().setHeights(20).setMaxWidth(Float.MAX_VALUE).setMargin(0, 1);
    typeSelector.getSelectionButton().getStyle().setHorizontalAlign(HorizontalAlign.LEFT);
    typeSelector.getFlexStyle().setFlexGrow(1);
    typeSelector.setElementHeight(20);
    typeSelector.setVisibleCount(12);

    Set<Identifier> particleTypes = Registry.PARTICLE_TYPE.getIds();
    for (Identifier particleType : particleTypes) {
      typeSelector.addElement(particleType.toString());
    }

    typeProp.getValueContainer().add(typeSelector);
    particleSource.getContainer().add(typeProp);

    MultiNumericInlineProp positionInput = new MultiNumericInlineProp("Position",60, "X", "Y", "Z");
    MultiNumericInlineProp rotationInput = new MultiNumericInlineProp("Velocity",60, "X", "Y", "Z");

    particleSource.getContainer().add(positionInput).add(rotationInput);

    this.widgetList.addWidget(particleSource);
  }

  public enum ObjectType {
    MOB("Mob"), PARTICLE_SOURCE("Particle Source");

    private String name;

    ObjectType(String name) {
      this.name = name;
    }

    public String getName() {
      return this.name;
    }
  }
}
