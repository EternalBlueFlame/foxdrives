package bidahochi.foxdrives.entities;

import bidahochi.foxdrives.models.ModelGilligPhantom;
import bidahochi.foxdrives.models.ToyotaPickup1992;
import fexcraft.tmt.slim.ModelBase;
import net.minecraft.world.World;

public class EntityGilligPhantom extends EntityCar {

    public EntityGilligPhantom(World world, double xPos, double yPos, double zPos) {
        super(world, xPos, yPos, zPos);
    }
    public EntityGilligPhantom(World p_i1685_1_) {
        super(p_i1685_1_);
    }


    /**
     * Returns the model for the entity
     */
    @Override
    public ModelBase getModel() {
        return new ModelGilligPhantom();
    }

    /**
     * returns a list of skin texture addresses for the entity, the MODID is presumed to be the one defined in FoxDrives.java
     * example:
     * String[] getSkins(){return new String[]{"textures/skin1", "textures/skin2"};}
     */
    @Override
    public String[] getSkins() {
        return new String[]{"textures/gillig/GILLIG_BUSSIN_generic"};
    }

    /**
     * Returns the movement speed in blocks per tick. entity horse uses 0.1f
     */
    @Override
    public float getMoveSpeed() {
        return 0.2f;
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
    public float[] getRiderOffset(){return new float[]{-1.5f,-0.0f,0.3f};}

    /**
     * Returns the amount to scale the player, MC default is 1, TC default is 0.65f
     */
    @Override
    public float getRiderScale() {
        return 0.65f;
    }
}
