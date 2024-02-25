package com.robocraft999.traidingnetwork.blockentity;

import com.jozufozu.flywheel.api.Instancer;
import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.core.materials.FlatLit;
import com.robocraft999.traidingnetwork.block.CreateShredderBlock;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityInstance;
import com.simibubi.create.content.kinetics.base.flwdata.RotatingData;
import com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock;
import net.minecraft.world.level.block.state.BlockState;

public class CreateShredderBlockEntityInstance extends KineticBlockEntityInstance<CreateShredderBlockEntity> {
    protected RotatingData rotatingModel1;
    protected RotatingData rotatingModel2;

    public CreateShredderBlockEntityInstance(MaterialManager materialManager, CreateShredderBlockEntity blockEntity) {
        super(materialManager, blockEntity);
    }

    public void init() {
        this.rotatingModel1 = this.setup((RotatingData)this.getModel().createInstance());
        this.rotatingModel2 = this.setup((RotatingData)this.getModel().createInstance());

        rotatingModel1.setRotationAxis(axis)
                .setRotationalSpeed(getBlockEntitySpeed())
                .setRotationOffset(-getRotationOffset(axis))
                .setColor(blockEntity)
                .setPosition(getInstancePosition());

        rotatingModel2.setRotationAxis(axis)
                .setRotationalSpeed(getBlockEntitySpeed())
                .setRotationOffset(-getRotationOffset(axis))
                .setColor(blockEntity)
                .setPosition(getInstancePosition())
                .nudge(0, 4f/16f, 0)
                .setRotationalSpeed(-getBlockEntitySpeed());
    }

    public void update() {
        this.updateRotation(this.rotatingModel1);
        this.updateRotation(this.rotatingModel2);
        rotatingModel2.setRotationalSpeed(-getBlockEntitySpeed());
    }

    public void updateLight() {
        this.relight(this.pos, new FlatLit[]{this.rotatingModel1, this.rotatingModel2});
    }

    public void remove() {
        this.rotatingModel1.delete();
        this.rotatingModel2.delete();
    }

    protected BlockState getRenderedBlockState() {
        return AllBlocks.SHAFT.getDefaultState().setValue(ShaftBlock.AXIS, blockState.getValue(CreateShredderBlock.HORIZONTAL_FACING).getAxis());
    }

    protected Instancer<RotatingData> getModel() {
        return this.getRotatingMaterial().getModel(this.getRenderedBlockState());
    }
}