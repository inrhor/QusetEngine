package cn.inrhor.questengine.command.collaboration

import cn.inrhor.questengine.common.collaboration.TeamManager
import io.izzel.taboolib.module.command.base.BaseSubCommand
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class DelTeamCmd: BaseSubCommand() {

    override fun onCommand(sender: CommandSender, command: Command, label : String, args: Array<out String>) {
        val player = sender as Player
        val pUUID = player.uniqueId
        if (!TeamManager.hasTeam(pUUID)) return
        val teamData = TeamManager.getTeamData(pUUID)?: return
        teamData.delTeam()
        return
    }

}