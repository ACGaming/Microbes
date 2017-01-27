package ca.wescook.microbes.configs;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Property;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CatalystData {

	private static List<Catalyst> catalysts = new ArrayList<Catalyst>();

	// Occurs once after configs have been loaded
	public static void parse() {
		// Iterate through list of catalysts
		Property rawCatalystData = Config.rawCatalystData;
		for (String entry : rawCatalystData.getStringList()) {

			// Split at spaces
			Pattern compiledRegex = Pattern.compile("(\\w+):(\\w+)(:\\d+)?\\s+(\\w+)\\s+(.+)"); // Regex is always easier to write than read, isn't it?
			Matcher regexResult = compiledRegex.matcher(entry);
			if (regexResult.matches()) {

				// Clean data
				String itemName = regexResult.group(1) + ":" + regexResult.group(2); // Form item name (eg. minecraft:dye)
				int metadata = regexResult.group(3) != null ? Integer.valueOf(regexResult.group(3).substring(1)) : 0; // Clean metadata (:15 > 15) and set to 0 if null
				ItemStack itemStack = new ItemStack(Item.getByNameOrId(itemName), 0, metadata); // Create ItemStack from name and metadata
				String property = regexResult.group(4); // eg. growthrate
				int amount = Integer.valueOf(regexResult.group(5)); // eg. -5

				// Create catalysts and add to list
				catalysts.add(new Catalyst(itemStack, property, amount));
			}
		}
	}

	// Accept ItemStack, return full catalyst data if found
	public static Catalyst find(ItemStack itemStack) {
		for (Catalyst catalyst : catalysts)
			if (catalyst.itemStack.isItemEqual(itemStack)) // TODO: Queue up multiple items
				return catalyst;

		return null;
	}
}
