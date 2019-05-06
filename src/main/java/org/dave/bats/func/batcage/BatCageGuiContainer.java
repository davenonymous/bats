package org.dave.bats.func.batcage;

import org.dave.bats.gui.framework.WidgetGuiContainer;

public class BatCageGuiContainer extends WidgetGuiContainer {
    public BatCageGuiContainer(BatCageContainer container) {
        super(container);

        this.xSize = 300;
        this.ySize = 184;

        this.gui = new BatCageGui(this.xSize, this.ySize, container);
        //this.gui.drawDebugFrames = true;
    }
}
