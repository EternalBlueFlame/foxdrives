package bidahochi.foxdrives.entities;

import bidahochi.foxdrives.FoxDrives;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fexcraft.tmt.slim.ModelBase;
import fexcraft.tmt.slim.ModelRendererTurbo;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.*;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public abstract class EntityCar extends EntityAnimal {

    @SideOnly(Side.CLIENT)
    public ModelBase modelInstance;
    @SideOnly(Side.CLIENT)
    public List<ModelRendererTurbo> frontWheels = new ArrayList<>();
    @SideOnly(Side.CLIENT)
    public List<ModelRendererTurbo> backWheels = new ArrayList<>();
    @SideOnly(Side.CLIENT)
    public long lastFrame = System.currentTimeMillis();

    public float health =20, roll=0;
    public double transportX=0,transportY=0,transportZ=0;
    public int tickOffset=0;
    public byte running=0, drift=0;
    public float velocity=0;
    public float driftStartTick=0;

    /**
     * client side entity spawn
     */
    public EntityCar(World world) {
        super(world);
        //this can be called on server from the inventory car class, so, dont do client stuff there
        if(world.isRemote) {
            this.preventEntitySpawning = true;
            this.setSize(getHitboxSize(), 1.6F);
            this.yOffset = 0;
            ignoreFrustumCheck = true;
            this.isImmuneToFire = true;
        }
    }


    /**
     * server side entity spawn
     */
    public EntityCar(World world, double xPos, double yPos, double zPos) {
        super(world);

        this.setPosition(xPos, yPos, zPos);
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
        this.prevPosX = xPos;
        this.prevPosY = yPos;
        this.prevPosZ = zPos;
        this.setSize(getHitboxSize(), 1.6F);
        ignoreFrustumCheck = true;
        this.isImmuneToFire = true;
        this.preventEntitySpawning = true;
    }


    /**
     * Literally just exists to properly init the datawatcher stuff.
     */
    @Override
    public void entityInit(){
        super.entityInit();
        this.dataWatcher.addObject(17, running);//tracks if the entity is on or not
        this.dataWatcher.addObject(18, roll);//tracks the entity roll from being hit
        this.dataWatcher.addObject(19, health);//tracks entity health
        this.dataWatcher.addObject(20, 0);//used to track currently selected skin
        this.dataWatcher.addObject(21, 0f);//used to track rotation yaw
    }

    /**
     * Returns the model for the entity
     */
    public abstract ModelBase getModel();

    /**
     * returns a list of skin texture addresses for the entity, the MODID is presumed to be the one defined in FoxDrives.java
     * example:
     * String[] getSkins(){return new String[]{"textures/skin1", "textures/skin2"};}
     */
    public abstract String[] getSkins();

    /**
     * Returns the max movement speed in meters per tick (50ms). entity horse uses 0.1f
     */
    public abstract float getMoveSpeed();

    /**
     * Returns the acceleration speed in meters per tick (50ms). (currently unused)
     */
    public abstract float getAccelSpeed();

    /**
     * returns a multiplier for how much the entity will rotate during turning
     */
    public float turnStrength(boolean reversing){return reversing?1f:1.25f;}

    /**
     * returns a multiplier for how much the entity will rotate during turning
     */
    public float turnRenderDegree(boolean reversing){return reversing?-35:35;}

    /**
     * returns a multiplier for the render of wheel spin speed
     */
    public float wheelSpinMultiplier(){return 1;}

    /**
     * allows entities to climb slabs
     */
    public boolean canClimbSlabs(){return true;}

    /**
     * allows entities to climb a single full block
     */
    public boolean canClimbFullBlocks(){return false;}

    /**
     * defines the hitbox size (width and depth) of the vehicle
     */
    public float getHitboxSize(){return 1.4f;}


    /************
     * overrides from host class to disable/modify features
     ***********/

    @Override
    public EntityAgeable createChild(EntityAgeable p_90011_1_) {return null;}

    /**
     * Returns a boundingBox used to collide the entity with other entities and blocks. This enables the entity to be
     * pushable on contact, like boats or minecarts.
     */
    @Override
    public AxisAlignedBB getCollisionBox(Entity p_70114_1_) {
        return p_70114_1_.boundingBox;
    }
    @Override
    public AxisAlignedBB getBoundingBox() {
        return boundingBox;
    }
    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    /**add entity mount functionality, and remove item interactions*/
    @Override
    public boolean interact(EntityPlayer p_70085_1_){
        //if it's the skinning item, iterate to the next skin
        if(!this.worldObj.isRemote && p_70085_1_.getHeldItem()!=null &&
                p_70085_1_.getHeldItem().getItem()== FoxDrives.wrap) {

                //gets current skin value and loops around to 0 if it's past the entity's skin count.
                int skin =dataWatcher.getWatchableObjectInt(20)+1;
                if(skin>=getSkins().length){
                    skin=0;
                }
                dataWatcher.updateObject(20,skin);
        //otherwise, try to mount the entity
        } else if (!this.worldObj.isRemote && this.riddenByEntity == null) {
            p_70085_1_.mountEntity(this);
            return true;
        }
        return false;
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
        if (!this.worldObj.isRemote && !this.isDead) {
            if (this.isEntityInvulnerable()) {
                return false;
            } else {
                setRollingDirection(-this.getRollingDirection());
                setRollingDirection(10);
                setDamage(this.getDamage() + p_70097_2_ * 10.0F);

                if ((p_70097_1_.getEntity() instanceof EntityPlayer
                        && ((EntityPlayer)p_70097_1_.getEntity()).capabilities.isCreativeMode)
                        || this.getDamage() > 40.0F) {
                    //dismount rider
                    if (this.riddenByEntity != null) {
                        this.riddenByEntity.mountEntity(this);
                    }

                    if (this.getDamage()<40) {
                        this.setDead();
                    }
                }

                return true;
            }
        } else {
            return true;
        }
    }

    public float getDamage(){
        return this.dataWatcher.getWatchableObjectFloat(19);
    }
    public void setDamage(float d){
        this.dataWatcher.updateObject(19, d);
    }
    public float getRollingDirection(){
        return this.dataWatcher.getWatchableObjectFloat(18);
    }
    public void setRollingDirection(float r){
        this.dataWatcher.updateObject(18, r);
    }

    /** called every tick
     * replaces core entity update functionality, since EntityLiving does too much stuff we don't need.*/
    @Override
    public void onUpdate() {

        //handle the get punched animation
        if (this.getRollingDirection() > 0) {
            this.setRollingDirection(this.getRollingDirection() - 1);
        }

        if (this.getDamage() > 0.0F) {
            this.setDamage(this.getDamage() - 1.0F);
        }
        if(posY<-64){
            this.kill();
        }
        //handle super update crap
        this.onEntityUpdate();
        //handle movement
        this.moveEntityWithHeading();
    }

    /** save/load stuff */
    @Override
    public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
        running= p_70037_1_.getByte("run");
        velocity=p_70037_1_.getFloat("vel");
        rotationYaw=p_70037_1_.getFloat("yaw");
    }
    @Override
    public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
        p_70014_1_.setByte("run", running);
        p_70014_1_.setFloat("vel", velocity);
        p_70014_1_.setFloat("yaw", rotationYaw);
    }

    /**
     * handles interaction from client over network.
     * @see bidahochi.foxdrives.util.PacketInteract
     * @see bidahochi.foxdrives.util.EventManager#onClientKeyPress(InputEvent.MouseInputEvent) */
    public boolean networkInteract(int player, int key) {
        if (!worldObj.isRemote) {
            if(key==1){
                this.dataWatcher.updateObject(17, running==(byte)1?(byte)0:(byte)1);
                return true;
            } else if (key==3){
                drift=1;
                return true;
            }
        }
        return false;
    }

    /**
     * Moves the entity based on the rider heading and rider.moveForward
     */
    public void moveEntityWithHeading() {
        if(!worldObj.isRemote) {
            velocity*=0.92f;
            EntityLivingBase rider = ((EntityLivingBase) this.riddenByEntity);
            if (rider != null) {
                velocity += rider.moveForward * this.getAccelSpeed();
                if(rider.moveForward==0){
                    drift=0;
                }
            }
            if (running != dataWatcher.getWatchableObjectByte(17)) {
                running = dataWatcher.getWatchableObjectByte(17);
            }
            if (running == 0) {
                velocity = 0;
            } else if (velocity <= 0.0F) {
                velocity *= 0.35F;
            }
            //clamp top speed
            if (velocity > getMoveSpeed()*0.0625f) {
                velocity = getMoveSpeed()*0.0625f;
            } else if (velocity < -getMoveSpeed()*0.0625f) {
                velocity = -getMoveSpeed()*0.0625f;
            }

            if (rider != null && rider.moveStrafing!=0) {
                if (velocity <= 0.0F) {
                    rotationYaw += (rider.moveStrafing * turnStrength(true));
                } else {
                    rotationYaw -= (rider.moveStrafing * turnStrength(false));
                }
                dataWatcher.updateObject(21, rotationYaw);
                driftStartTick=ticksExisted;
                System.out.println(drift + " : " + rider.moveStrafing);
            }

            this.stepHeight = canClimbFullBlocks()?1.0f:canClimbSlabs()?0.5f:0.0f;
            moveEntityWithHeading(0, velocity);
            if(drift==1) {
                if (velocity <= 0.0F) {
                    rotationYaw += (rider.moveStrafing * turnStrength(true)*0.5);
                } else {
                    rotationYaw -= (rider.moveStrafing * turnStrength(false)*0.5);
                }
                if (ticksExisted > driftStartTick + 40) {
                    drift = 0;
                }
            }

            double d0 = 0.25D;
            List list = worldObj.getEntitiesWithinAABBExcludingEntity(this, getBoundingBox().expand(d0, d0, d0));

            for (Object o : list) {
                if (o instanceof EntityLiving && ((Entity) o).getBoundingBox()!=null
                        && ((Entity) o).getBoundingBox().intersectsWith(getBoundingBox())) {
                    ((Entity) o).attackEntityFrom(new EntityDamageSource("player", this), 5);
                }
            }
        } else if(tickOffset >0) {
            prevPosX=posX;prevPosZ=posZ;
            setPosition(
                    this.posX + (this.transportX - this.posX) / (double) this.tickOffset,
                    this.posY + (this.transportY - this.posY) / (double) this.tickOffset,
                    this.posZ + (this.transportZ - this.posZ) / (double) this.tickOffset
            );
            tickOffset--;
            rotationYaw=dataWatcher.getWatchableObjectFloat(21);
        }
    }


    @SideOnly(Side.CLIENT)
    public void setPositionAndRotation2(double p_70056_1_, double p_70056_3_, double p_70056_5_, float p_70056_7_, float p_70056_8_, int p_70056_9_) {
        transportX=p_70056_1_;
        transportY=p_70056_3_;
        transportZ=p_70056_5_;
        //adds 2 to the tick offset to match the entity registration's update frequency of 3.
        //as noted in the cpw.mods.fml.common.registry.EntityRegistry.registerModEntity
        //     call of FoxDrives.java#init(FMLInitializationEvent)
        tickOffset = p_70056_9_ + 2;

        //force an extra rider position update. probably unnecessary, but better safe than laggy.
        updateRiderPosition();
    }

    /**
     * Returns the rider offset from the center of the entity, in blocks.
     */
    public abstract float[] getRiderOffset();

    /**
     * Returns the amount to scale the player, MC default is 1, TC default is 0.65
     */
    public abstract float getRiderScale();

    /**sets the position of the entity riding*/
    @Override
    public void updateRiderPosition(){
        if (this.riddenByEntity != null) {

            float[] xyz = getRiderOffset();
            //rotate yaw
            if (rotationYaw != 0.0F) {
                float cos = MathHelper.cos((rotationYaw)*((float) Math.PI / 180.0f));
                float sin = MathHelper.sin((rotationYaw)*((float) Math.PI / 180.0f));

                xyz[0] = (getRiderOffset()[0] * cos) - (getRiderOffset()[2] * sin);
                xyz[2] = (getRiderOffset()[0] * sin) + (getRiderOffset()[2] * cos);
            }

            this.riddenByEntity.setPosition(this.posX + xyz[0],
                    this.posY + this.riddenByEntity.getYOffset()+xyz[1],
                    this.posZ+xyz[2]);
        }
    }

    /*todo:
    @Override
    public void openGUI(EntityPlayer p_110199_1_)
    {
        if (!this.worldObj.isRemote && (this.riddenByEntity == null || this.riddenByEntity == p_110199_1_) && this.isTame())
        {
            this.horseChest.func_110133_a(this.getCommandSenderName());
            p_110199_1_.displayGUIHorse(this, this.horseChest);
        }
    }*/


    //todo: plays driving sounds using vanilla step sound heresy
    @Override
    public void func_145780_a(int p_145780_1_, int p_145780_2_, int p_145780_3_, Block p_145780_4_){}
}
