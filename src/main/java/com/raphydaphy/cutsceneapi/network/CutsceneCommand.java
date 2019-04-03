package com.raphydaphy.cutsceneapi.network;

import com.raphydaphy.cutsceneapi.api.Cutscene;
import com.raphydaphy.cutsceneapi.api.CutsceneAPI;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class CutsceneCommand extends CommandBase
{
	@Override
	public String getName()
	{
		return "cutscene";
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return "/cutscene <player> <id>";
	}

	@Override
	public List<String> getAliases()
	{
		return Collections.singletonList("cutscene");
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if (args.length != 2)
		{
			sender.sendMessage(new TextComponentTranslation("command.cutsceneapi.invalid_args"));
			return;
		}
		EntityPlayer player = getPlayer(server, sender, args[0]);
		ResourceLocation id = new ResourceLocation(args[1]);
		System.out.println(("Trying to play cutscene with id " + id.toString()));
		Cutscene cutscene = CutsceneAPI.getCutsceneManager().get(id, false);
		if (cutscene != null)
		{
			CutsceneAPI.getCutsceneManager().start(player, cutscene);
		} else
		{
			sender.sendMessage(new TextComponentTranslation("command.cutsceneapi.invalid_cutscene"));
		}
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender)
	{
		return sender.canUseCommand(2, "cutscene");
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
	{
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index)
	{
		return false;
	}

	@Override
	public int compareTo(ICommand o)
	{
		return 0;
	}
}
