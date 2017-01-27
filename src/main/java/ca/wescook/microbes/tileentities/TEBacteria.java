package ca.wescook.microbes.tileentities;

import ca.wescook.microbes.configs.Catalyst;
import ca.wescook.microbes.configs.CatalystData;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;

public class TEBacteria extends TileEntity implements ITickable {

	// Properties/Traits
	public int population;
	public float growthRate;
	public int age;
	public float resistance;
	public NBTTagCompound traits;

	public TEBacteria() {
		// Initial property values
		population = 1;
		growthRate = 1.0F;
		age = 1;
		resistance = 1.0F;
		traits = new NBTTagCompound();
	}

	@Override
	public void update() {

		// Bail out if not a valid tick
		if (worldObj.getTotalWorldTime() % (20 * 10 * growthRate) != 0) // Default: 10 seconds
			return;

		// Detect catalysts
		if (!worldObj.isRemote) {
			// Look for catalysts in bacteria
			List<EntityItem> entityItemsFound = worldObj.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos, pos.add(1, 1, 1))); // Get item list from bacteria pool
			EntityItem entityItem = entityItemsFound.get(0); // Get first item from list
			Catalyst catalyst = CatalystData.find(entityItem.getEntityItem()); // Fetch catalyst from ItemStack if available, return null if not

			// Act on items
			if (catalyst != null) { // Accept and consume item
				System.out.println(catalyst.itemStack.getDisplayName() + " modifies " + catalyst.property + " by " + catalyst.amount);
				--entityItem.getEntityItem().stackSize; // Remove one from stack size until gone
			} else
				entityItem.addVelocity(worldObj.rand.nextGaussian() * 0.13D, 0.6D, worldObj.rand.nextGaussian() * 0.13D); // Eject item from bacteria

			// Population doubles each update, up to its limit
			if (population < 1000)
				population = Math.min(population * 2, 1000);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);

		// Get bacteria properties
		population = compound.getInteger("population");
		growthRate = compound.getFloat("growthRate");
		age = compound.getInteger("age");
		resistance = compound.getFloat("resistance");
		traits = compound.getCompoundTag("traits");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);

		// Set bacteria properties
		compound.setInteger("population", population);
		compound.setFloat("growthRate", growthRate);
		compound.setInteger("age", age);
		compound.setFloat("resistance", resistance);
		compound.setTag("traits", traits);
		return compound;
	}
}
