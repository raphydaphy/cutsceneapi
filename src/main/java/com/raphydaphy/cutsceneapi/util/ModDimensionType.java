package com.raphydaphy.cutsceneapi.util;

import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;

import java.util.function.BiFunction;

public class ModDimensionType extends DimensionType
{
	public ModDimensionType(int id, String suffix, String saveFolder, BiFunction<World, DimensionType, ? extends Dimension> factory, boolean isOverworld)
	{
		super(id, suffix, saveFolder, factory, isOverworld);
	}
}
