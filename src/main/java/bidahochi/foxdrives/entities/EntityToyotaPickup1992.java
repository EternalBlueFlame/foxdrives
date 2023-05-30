package bidahochi.foxdrives.entities;

import bidahochi.foxdrives.models.ToyotaPickup1992;
import fexcraft.tmt.slim.ModelBase;
import net.minecraft.world.World;


public class EntityToyotaPickup1992 extends EntityCarChest {

    public EntityToyotaPickup1992(World world, double xPos, double yPos, double zPos) {
        super(world, xPos, yPos, zPos);
    }
    public EntityToyotaPickup1992(World p_i1685_1_) {
        super(p_i1685_1_);
    }


    /**
     * Returns the model for the entity
     */
    @Override
    public ModelBase getModel() {
        return new ToyotaPickup1992();
    }

    /**
     * returns a list of skin texture addresses for the entity, the MODID is presumed to be the one defined in FoxDrives.java
     * example:
     * String[] getSkins(){return new String[]{"textures/skin1", "textures/skin2"};}
     */
    @Override
    public String[] getSkins() {
        return new String[]{"textures/toyota/JC1992BoiodaPickup"};
    }

    /**
     * Returns the movement speed in blocks per tick. entity horse uses 0.1f
     */
    @Override
    public float getMoveSpeed() {
        return 45f;
    }

    /**
     * Returns the acceleration speed in meters per second.
     * NOTE: this method is a placeholder and is not yet implemented.
     */
    @Override
    public float getAccelSpeed() {
        return 1;
    }

    /**
     * Returns the rider offset from the center of the entity, in blocks.
     */
    @Override
    public float[] getRiderOffset(){return new float[]{-0.3f,-0.05f,0.25f};}

    /**
     * Returns the amount to scale the player, MC default is 1, TC default is 0.65f
     */
    @Override
    public float getRiderScale() {
        return 0.65f;
    }

}
