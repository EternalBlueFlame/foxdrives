package bidahochi.foxdrives;

import bidahochi.foxdrives.entities.EntityCar;
import bidahochi.foxdrives.entities.EntityGilligPhantom;
import bidahochi.foxdrives.entities.EntityToyotaPickup1992;
import bidahochi.foxdrives.util.ItemCar;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public enum  EnumCars {

    toyotaPickup1992(EntityToyotaPickup1992.class, genItem(EntityToyotaPickup1992.class),
            new ItemStack[]{
            new ItemStack(Blocks.stone), new ItemStack(Blocks.glass_pane),new ItemStack(Blocks.stone),
            new ItemStack(Blocks.iron_door), new ItemStack(Blocks.oak_stairs),new ItemStack(Blocks.iron_door),
            new ItemStack(Blocks.stone), new ItemStack(Blocks.glass_pane),new ItemStack(Blocks.stone)
    }),

    gilligPhantom(EntityGilligPhantom.class, genItem(EntityGilligPhantom.class),
            new ItemStack[]{
        new ItemStack(Blocks.stone), new ItemStack(Blocks.glass_pane),new ItemStack(Blocks.stone),
                new ItemStack(Blocks.iron_door), new ItemStack(Blocks.oak_stairs),new ItemStack(Blocks.iron_door),
                new ItemStack(Blocks.stone), new ItemStack(Blocks.glass_pane),new ItemStack(Blocks.stone)
    })
    /* EXAMPLE ENTRY:

    , otherVehicle(EntityOther.class, genItem(EntityOther.class),
            new ItemStack[]{
            new ItemStack(Blocks.stone), new ItemStack(Blocks.glass_pane),new ItemStack(Blocks.stone),
            new ItemStack(Blocks.iron_door), new ItemStack(Blocks.oak_stairs),new ItemStack(Blocks.iron_door),
            new ItemStack(Blocks.stone), new ItemStack(Blocks.glass_pane),new ItemStack(Blocks.stone)
    })

    */




    ;



    private Class<? extends EntityCar> entityClass;
    private Item item;
    private ItemStack[] recipe;

    public Class<? extends EntityCar> getEntityClass(){return entityClass;}
    public Item getEntityItem(){return item;}
    public ItemStack[] getEntityRecipe(){return recipe;}

    EnumCars(Class<? extends EntityCar> c, Item i, ItemStack[] r){
        entityClass=c;
        item=i;
        recipe=r;
    }

    private static ItemCar genItem(Class<? extends EntityCar> c){
        return new ItemCar(c,FoxDrives.MODID,FoxDrives.tab);
    }

}
