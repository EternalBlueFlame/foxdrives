package bidahochi.foxdrives.util;

import bidahochi.foxdrives.FoxDrives;
import bidahochi.foxdrives.entities.EntityCar;
import bidahochi.foxdrives.entities.EntityCarChest;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;

public class EventManager {

    public static EventManager instance = new EventManager();

    //handles user inputs, mainly just meant for opening the GUI
    //if an inventory is to be managed, this will require a send to server entity command
    //    which opens the GUI through the proxy.
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onClientKeyPress(InputEvent.MouseInputEvent event) {
        if(Minecraft.getMinecraft().currentScreen !=null || !(Minecraft.getMinecraft().thePlayer.ridingEntity instanceof EntityCar)){
            return;
        }

        if(bidahochi.foxdrives.util.ClientProxy.KeyInventory.isPressed()){
            if(Minecraft.getMinecraft().thePlayer.ridingEntity instanceof EntityCarChest){
                FoxDrives.interactChannel.sendToServer(new PacketInteract(
                        2,Minecraft.getMinecraft().thePlayer.ridingEntity.getEntityId()));
            } else {
                FMLCommonHandler.instance().showGuiScreen(new GuiCar((EntityCar) Minecraft.getMinecraft().thePlayer.ridingEntity));
            }
        } else if(bidahochi.foxdrives.util.ClientProxy.KeyBrake.isPressed()){
            FoxDrives.interactChannel.sendToServer(new PacketInteract(
                    3,Minecraft.getMinecraft().thePlayer.ridingEntity.getEntityId()));
        }
    }

}
