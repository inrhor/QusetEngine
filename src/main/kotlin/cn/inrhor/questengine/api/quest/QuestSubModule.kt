package cn.inrhor.questengine.api.quest

import cn.inrhor.questengine.common.quest.QuestControl
import cn.inrhor.questengine.common.quest.QuestReward
import cn.inrhor.questengine.common.quest.QuestTarget

class QuestSubModule(val subQuestID: String,
                     var questControl: QuestControl,
                     var questReward: QuestReward,
                     var questTargetList: MutableMap<String, QuestTarget>) {

}