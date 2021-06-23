package cn.inrhor.questengine.common.dialog.optional.holo

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.api.dialog.ReplyModule
import cn.inrhor.questengine.common.kether.KetherHandler
import cn.inrhor.questengine.utlis.location.LocationTool
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

/**
 * 全息对话回复管理
 */
class HoloReply(
    var replyList: MutableList<ReplyModule>,
    var npcLoc: Location,
    var viewers: MutableSet<Player>,
    val delay: Long) {

    fun run() {
        object : BukkitRunnable() {
            override fun run() {
                for (replyModule in replyList) {
                    runContent(replyModule)
                }
            }
        }.runTaskLaterAsynchronously(QuestEngine.plugin, delay)
    }

    private fun runContent(replyModule: ReplyModule) {
        var holoLoc = npcLoc
        var nextY = 0.0
        var textIndex = 0
        var itemIndex = 0
        for (i in replyModule.content) {
            val iUc = i.uppercase(Locale.getDefault())
            when {
                iUc.startsWith("HITBOX") -> {

                }
                iUc.startsWith("INITLOC") -> {
                    holoLoc = LocationTool().getFixedLoc(npcLoc, KetherHandler.evalFixedLoc(i))
                }
                iUc.startsWith("ADDLOC") -> {
                    holoLoc = LocationTool().getFixedLoc(holoLoc, KetherHandler.evalFixedLoc(i))
                }
                iUc.startsWith("NEXTY") -> {
                    val get = i.substring(0, iUc.indexOf(" "))
                    nextY = i.substring(get.length + 1, i.length).toDouble()
                }
                iUc.startsWith("TEXT") -> {

                }
                iUc.startsWith("ITEM") -> {

                }
            }
        }
    }

}