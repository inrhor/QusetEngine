package cn.inrhor.questengine.common.packet

import cn.inrhor.questengine.common.item.ItemManager
import cn.inrhor.questengine.common.nms.NMS
import cn.inrhor.questengine.utlis.file.GetFile
import cn.inrhor.questengine.utlis.public.UseString
import io.izzel.taboolib.module.locale.TLocale
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import java.io.File
import java.util.*

object PacketManager {

    val packetMap = mutableMapOf<String, PacketModule>()

    /**
     * type > packet
     */
    fun generate(packetID: String, type: String): Int {
        return "packet-$packetID-$type".hashCode()
    }

    fun register(packetID: String, packetModule: PacketModule) {
        packetMap[packetID] = packetModule
    }

    fun removeID(packetID: String) {
        packetMap.remove(packetID)
    }

    fun sendThisPacket(packetID: String, sender: Player, location: Location) {
        val packetModule = packetMap[packetID]?: return
        val viewers = mutableSetOf(sender)
        if (packetModule.viewer == "all") viewers.addAll(Bukkit.getOnlinePlayers())
        val hook = packetModule.hook
        if (hook.lowercase(Locale.getDefault()) == "this ") {
            val id = hook[1].toString()
            val getPacketModule = packetMap[id]?: return
            sendPacket(packetModule.entityID, getPacketModule, viewers, location)
        }
    }

    private fun sendPacket(entityID: Int, packetModule: PacketModule, viewers: MutableSet<Player>, location: Location) {
        getPackets().spawnEntity(viewers, entityID, packetModule.entityType, location)
        /*val itemEntityMap = packetModule.itemEntityID
        if (itemEntityMap.isNotEmpty()) {
            itemEntityMap.forEach { (itemID, entityID) ->
                val item = ItemManager.get(itemID)
                getPackets().spawnItem(viewers, entityID, location, item)
            }
        }*/
        packetModule.mate.forEach {
            val sp = it.split(" ")
            val sign = sp[0].lowercase(Locale.getDefault())
            if (sign == "equip") {
                // 目前版本仅有 head
                val slot = sp[1].lowercase(Locale.getDefault())
                if (slot == "head") {
                    val itemID = sp[2]
                    val item = ItemManager.get(itemID)
                    getPackets().updateEquipmentItem(viewers, entityID, item)
                }
            }else if (sign == "displayName") { // false 为 不显示
                val displayName = sp[1]
                getPackets().updateDisplayName(viewers, entityID, displayName)
                val display = sp[2].toBoolean()
                getPackets().updateEntityMetadata(viewers, entityID, getPackets().getMetaEntityCustomNameVisible(display))
            }else if (sign == "visible") {
                if (!sp[1].toBoolean()) {
                    getPackets().updateEntityMetadata(viewers, entityID, getPackets().getIsInvisible())
                }
            }
        }
    }

    /**
     * 加载并注册数据包文件
     */
    fun loadPacket() {
        val packetFolder = GetFile().getFile("space/packet", "PACKET.NO_FILES")
        GetFile().getFileList(packetFolder).forEach{
            checkRegPacket(it)
        }
    }

    /**
     * 检查和注册数据包
     */
    private fun checkRegPacket(file: File) {
        val yaml = YamlConfiguration.loadConfiguration(file)
        if (yaml.getKeys(false).isEmpty()) {
            TLocale.sendToConsole("PACKET.EMPTY_CONTENT", UseString.pluginTag, file.name)
            return
        }
        for (packetID in yaml.getKeys(false)) {
            PacketFile.init(yaml.getConfigurationSection(packetID)!!)
        }
    }

    fun returnEntityType(entityType: String): EntityType {
        return EntityType.valueOf(entityType.lowercase(Locale.getDefault()))
    }

    fun returnItemEntityID(packetID: String, mate: MutableList<String>): MutableMap<String, Int> {
        val itemEntityID = mutableMapOf<String, Int>()

        mate.forEach {
            if (it.lowercase(Locale.getDefault()).startsWith("equip ")) {
                val sp = it.split(" ")
                val itemID = sp[2]
                val entityID = generate(packetID, itemID)
                itemEntityID[itemID] = entityID
            }
        }

        return itemEntityID
    }

    private fun getPackets(): NMS {
        return NMS.INSTANCE
    }

}