package bidahochi.foxdrives.util;


import bidahochi.foxdrives.entities.EntityCarChest;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;

public class ClientProxy extends CommonProxy {

    public static final RenderCar transportRenderer = new RenderCar();
    /**the keybind for opening the inventory*/
    public static KeyBinding KeyInventory = new KeyBinding("Open car GUI",  Keyboard.KEY_R, "Fox Drives");
    public static KeyBinding KeyBrake = new KeyBinding("Handbrake",  Keyboard.KEY_SPACE, "Fox Drives");

    @Override
    public Object getEntityRender(){return transportRenderer;}

    @Override
    public boolean isClient(){return true;}

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if(player.worldObj.getEntityByID(ID) instanceof EntityCarChest){
            return new GuiCarInventory(player.inventory, (EntityCarChest) player.worldObj.getEntityByID(ID));
        }
        return null;
    }
}
