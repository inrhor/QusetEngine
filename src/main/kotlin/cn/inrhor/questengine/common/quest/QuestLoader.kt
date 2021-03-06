package cn.inrhor.questengine.common.quest

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.api.quest.TargetExtend
import io.izzel.taboolib.TabooLibLoader
import io.izzel.taboolib.compat.kotlin.CompatKotlin
import io.izzel.taboolib.kotlin.SingleListener
import io.izzel.taboolib.module.inject.TFunction
import org.bukkit.event.Event

object QuestLoader {

    @Suppress("UNCHECKED_CAST")
    @TFunction.Init
    private fun registerTarget() {
        TabooLibLoader.getPluginClassSafely(QuestEngine.plugin).forEach {
            if (TargetExtend::class.java.isAssignableFrom(it)) {
                (CompatKotlin.getInstance(it) as? TargetExtend<Event>)?.register()
            }
        }
    }

    fun <T : Event> TargetExtend<T>.register() {
        if (isListener) {
            SingleListener.listen(event.java, priority, ignoreCancelled) { e ->
                tasker(e)?.run {
                    conditionMap.forEach { (_, condition) ->
                        if (isAsync) {
                            if (!condition.check()) return@forEach
                        }else {
                            if (!condition.check()) return@forEach
                        }
                    }
                }
            }
        }
    }

}