package com.bawnorton.trulyrandom.command;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.server.ServerPlayerUtils;
import com.bawnorton.trulyrandom.team.Team;
import com.bawnorton.trulyrandom.team.TeamActionResult;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.DataResult;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class TeamCommand {
    public LiteralArgumentBuilder<CommandSourceStack> create() {
        return Commands.literal("team")
                .then(Commands.literal("invite")
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(this::executeInvitePlayer)
                        )
                )
                .then(Commands.literal("uninvite")
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(this::executeUninvitePlayer)
                        )
                )
                .then(Commands.literal("join")
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(this::executeJoinTeam)
                        )
                )
                .then(Commands.literal("reject")
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(this::executeRejectJoinTeam)
                        )
                )
                .then(Commands.literal("leave")
                        .executes(this::executeLeaveTeam)
                )
                .then(Commands.literal("kick")
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(this::executeKickPlayer)
                        )
                )
                .then(Commands.literal("ban")
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(this::executeBanPlayer)
                        )
                )
                .then(Commands.literal("unban")
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(this::executeUnbanPlayer)
                        )
                )
                .then(Commands.literal("transfer")
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(this::executeTransferOwnership)
                        )
                )
                .executes(this::executeTeamInfo);
    }

    private int executeInvitePlayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Player self = context.getSource().getPlayerOrException();
        Team team = self.trulyrandom$getTeam();
        Player player = EntityArgument.getPlayer(context, "player");
        TeamActionResult result = team.invitePlayer(player.getUUID());
        if (result.isPositive()) {
            MutableComponent inviteMessage = Component.translatable("trulyrandom.message.team.invite", self.getDisplayName())
                    .withStyle(ChatFormatting.GOLD);
            Component acceptButton = ComponentUtils.wrapInSquareBrackets(Component.translatable("trulyrandom.message.team.invite.accept"))
                    .withStyle(style -> style.withClickEvent(new ClickEvent.RunCommand("/trulyrandom team join " + self.getPlainTextName()))
                            .withColor(ChatFormatting.GREEN));
            Component rejectButton = ComponentUtils.wrapInSquareBrackets(Component.translatable("trulyrandom.message.team.invite.reject"))
                    .withStyle(style -> style.withClickEvent(new ClickEvent.RunCommand("/trulyrandom team reject " + self.getPlainTextName()))
                            .withColor(ChatFormatting.RED));
            Component message = inviteMessage.append(CommonComponents.space())
                    .append(acceptButton)
                    .append(CommonComponents.space())
                    .append(rejectButton);
            player.sendSystemMessage(message);
        }
        return sendResponse(context, result, player, self);
    }

    private int executeUninvitePlayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Player self = context.getSource().getPlayerOrException();
        Team team = self.trulyrandom$getTeam();
        Player player = EntityArgument.getPlayer(context, "player");
        TeamActionResult result = team.uninvitePlayer(player.getUUID());
        return sendResponse(context, result, player, self);
    }

    private int executeJoinTeam(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Player self = context.getSource().getPlayerOrException();
        Team currentTeam = self.trulyrandom$getTeam();
        if (currentTeam.isOwner(self.getUUID()) && currentTeam.hasMembers()) {
            context.getSource().sendFailure(TeamActionResult.IS_OWNER.asNegative().withVariant("leave_with_members").getReasonText());
            return 0;
        }

        Player player = EntityArgument.getPlayer(context, "player");
        TeamActionResult result = player.trulyrandom$getTeam().addPlayer(self.getUUID());

        if (result.isPositive()) {
            if (!currentTeam.isOwner(self.getUUID())) {
                currentTeam.kickPlayer(self.getUUID());
            }
            TrulyRandom.getTeams(context.getSource().getServer()).addMembership(self, player.trulyrandom$getTeam());
            player.sendSystemMessage(Component.translatable("trulyrandom.message.team.joined", self.getDisplayName()).withStyle(ChatFormatting.GOLD));
        }

        return sendResponse(context, result, player, self);
    }

    private int executeRejectJoinTeam(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Player self = context.getSource().getPlayerOrException();
        Player player = EntityArgument.getPlayer(context, "player");
        TeamActionResult result = player.trulyrandom$getTeam().uninvitePlayer(self.getUUID()).withVariant("invite_rejected");
        return sendResponse(context, result, player, self);
    }

    private int executeLeaveTeam(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Player self = context.getSource().getPlayerOrException();
        Team currentTeam = self.trulyrandom$getTeam();

        if (currentTeam.isOwner(self.getUUID())) {
            if (currentTeam.hasMembers()) {
                context.getSource().sendFailure(TeamActionResult.IS_OWNER.asNegative().withVariant("leave_with_members").getReasonText());
                return 0;
            } else {
                TrulyRandom.getTeams(context.getSource().getServer()).removeMembership(self);
                context.getSource().sendSuccess(() -> TeamActionResult.SUCCESS.withVariant("left").getReasonText(self.getDisplayName()), false);
                return 1;
            }
        }

        currentTeam.kickPlayer(self.getUUID());
        TrulyRandom.getTeams(context.getSource().getServer()).removeMembership(self);
        UUID owner = currentTeam.getOwner();
        DataResult<Component> result = ServerPlayerUtils.getPlayerName(context.getSource().getServer(), owner);
        if(result.isSuccess()) {
            context.getSource().sendSuccess(() -> TeamActionResult.SUCCESS.withVariant("left")
                    .getReasonText(result.getOrThrow()), false);
        } else {
            context.getSource().sendSuccess(() -> TeamActionResult.SUCCESS.withVariant("left")
                    .withVariant("owner_unknown")
                    .getReasonText(result.error().orElseThrow().error()), false);
        }
        return 1;
    }

    private int executeKickPlayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Player self = context.getSource().getPlayerOrException();
        Team team = self.trulyrandom$getTeam();
        if (!team.isOwner(self.getUUID())) {
            context.getSource().sendFailure(TeamActionResult.IS_NOT_OWNER.withVariant("kick").getReasonText());
            return 0;
        }

        Player player = EntityArgument.getPlayer(context, "player");
        TeamActionResult result = team.kickPlayer(player.getUUID());
        if (result.isPositive()) {
            TrulyRandom.getTeams(context.getSource().getServer()).removeMembership(player);
        }
        return sendResponse(context, result, player, self);
    }

    private int executeBanPlayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Player self = context.getSource().getPlayerOrException();
        Team team = self.trulyrandom$getTeam();
        if (!team.isOwner(self.getUUID())) {
            context.getSource().sendFailure(TeamActionResult.IS_NOT_OWNER.withVariant("ban").getReasonText());
            return 0;
        }

        Player player = EntityArgument.getPlayer(context, "player");
        TeamActionResult result = team.banPlayer(player.getUUID());
        if (result.isPositive()) {
            TrulyRandom.getTeams(context.getSource().getServer()).removeMembership(player);
        }
        return sendResponse(context, result, player, self);
    }

    private int executeUnbanPlayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Player self = context.getSource().getPlayerOrException();
        Team team = self.trulyrandom$getTeam();
        if (!team.isOwner(self.getUUID())) {
            context.getSource().sendFailure(TeamActionResult.IS_NOT_OWNER.withVariant("unban").getReasonText());
            return 0;
        }

        Player player = EntityArgument.getPlayer(context, "player");
        TeamActionResult result = team.unbanPlayer(player.getUUID());
        return sendResponse(context, result, player, self);
    }

    private int executeTransferOwnership(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Player self = context.getSource().getPlayerOrException();
        Team team = self.trulyrandom$getTeam();
        if (!team.isOwner(self.getUUID())) {
            context.getSource().sendFailure(TeamActionResult.IS_NOT_OWNER.withVariant("transfer").getReasonText());
            return 0;
        }

        Player player = EntityArgument.getPlayer(context, "player");
        TeamActionResult result = self.trulyrandom$getTeam().transferOwnership(player.getUUID());
        return sendResponse(context, result, player, self);
    }

    private int sendResponse(CommandContext<CommandSourceStack> context, TeamActionResult result, Player player, Player self) {
        Component reasonText = result.getReasonText(player.getDisplayName(), self.getDisplayName());
        if (result.isPositive()) {
            context.getSource().sendSuccess(() -> reasonText, false);
            return 1;
        } else {
            context.getSource().sendFailure(reasonText);
            return 0;
        }
    }

    private int executeTeamInfo(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Player self = context.getSource().getPlayerOrException();
        Team team = self.trulyrandom$getTeam();
        MinecraftServer server = context.getSource().getServer();
        if (team.isOwner(self.getUUID())) {
            if (!team.hasMembers()) {
                context.getSource().sendSuccess(() -> Component.translatable("trulyrandom.message.team.info.owner.empty"), false);
            } else {
                context.getSource().sendSuccess(() -> Component.translatable(
                        "trulyrandom.message.team.info.owner.members",
                        Component.literal(Integer.toString(team.getPlayers().size())).withStyle(ChatFormatting.AQUA)
                ), false);
                for (UUID member : team.getPlayers()) {
                    DataResult<Component> result = ServerPlayerUtils.getPlayerName(server, member);
                    String name = result.mapOrElse(Component::getString, DataResult.Error::message);
                    context.getSource().sendSuccess(() -> Component.literal("- ").append(name), false);
                }
            }
        } else {
            UUID ownerId = team.getOwner();
            DataResult<Component> result = ServerPlayerUtils.getPlayerName(server, ownerId);
            if(result.isSuccess()) {
                context.getSource().sendSuccess(() -> Component.translatable(
                        "trulyrandom.message.team.info.member",
                        result.getOrThrow(),
                        Component.literal(Integer.toString(team.getPlayers().size())).withStyle(ChatFormatting.AQUA)
                ), false);
            } else {
                context.getSource().sendFailure(Component.translatable(
                        "trulyrandom.message.owner_unknown",
                        result.error().orElseThrow().message()
                ));
            }
        }
        return 1;
    }
}
