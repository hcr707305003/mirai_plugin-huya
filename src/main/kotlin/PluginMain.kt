package shiroi_plugin.huya

import com.google.gson.Gson
import kotlinx.coroutines.launch
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.EventChannel
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.utils.info
import java.util.*
import shiroi_plugin.huya.helper.*

/**
 * 使用 kotlin 版请把
 * `src/main/resources/META-INF.services/net.mamoe.mirai.console.plugin.jvm.JvmPlugin`
 * 文件内容改成 `org.example.mirai.plugin.PluginMain` 也就是当前主类全类名
 *
 * 使用 kotlin 可以把 java 源集删除不会对项目有影响
 *
 * 在 `settings.gradle.kts` 里改构建的插件名称、依赖库和插件版本
 *
 * 在该示例下的 [JvmPluginDescription] 修改插件名称，id和版本，etc
 *
 * 可以使用 `src/test/kotlin/RunMirai.kt` 在 ide 里直接调试，
 * 不用复制到 mirai-console-loader 或其他启动器中调试
 */

object PluginMain : KotlinPlugin(
    JvmPluginDescription(
        id = "shiroi_plugin.huya",
        name = "shiroi_plugin",
        version = "0.1.0"
    ) {
        author("shiroi")
        info(
            """
            检测虎牙直播动态
        """.trimIndent()
        )
    }
) {
    override fun onEnable() {
        val eventChannel = GlobalEventChannel.parentScope(this)
        val cachePath = dataFolder.absolutePath + "\\cache\\"
        val dataPath = dataFolder.absolutePath + "\\data\\"
        eventChannel.subscribeAlways<GroupMessageEvent> {
            //群消息
            var groupMessage = message.contentToString()
            var fileHelper = FileHelper(dataPath + group.id.toString() + "\\")
            var command = listOf<String>(
                "/huya add",//增加虎牙订阅
                "/huya del",//删除虎牙订阅
                "/huya list",//虎牙订阅列表
            )
            //huya add -> 增加虎牙订阅
            if (groupMessage.contains(command[0])) {
                var id = groupMessage.substring(command[0].length + 1)
                var huya = HuYa(Helper().get(id))
                huya.id = id
                if (!huya.name.isEmpty()) {
                    fileHelper.create(id, huya.toString())
                    group.sendMessage(At(sender.id) + "\n" + "新增成功~")
                }
            }
            //huya del -> 删除虎牙订阅
            if (groupMessage.contains(command[1])) {
                if(fileHelper.delete(groupMessage.substring(command[1].length + 1))) {
                    group.sendMessage(At(sender.id) + "\n" + "删除成功~")
                }
            }
            //huya list -> 虎牙订阅列表
            if (groupMessage == command[2]) {
                var str = ""
                fileHelper.getFileList().forEach {
                    var huya = Gson().fromJson(fileHelper.get(it), HuYa::class.java)
                    str += "${huya.name}@${huya.id}\n"
                }
                group.sendMessage(At(sender.id) + "\n" + str)
            }
            //获取huya命令
            if (groupMessage.trim() == "/huya" || groupMessage.trim() == "huya") {
                var str = At(sender.id) + "\n"
                command.forEach {
                    str += "$it\n"
                }
                group.sendMessage(str)
            }
        }

        //发布虎牙直播动态
        GlobalEventChannel.subscribeOnce<BotOnlineEvent> {
            val nudgeTimer =object : TimerTask() {
                override fun run() {
                    this@PluginMain.launch {
                        bot.groups.forEach { botGroup ->
                            val fileHelper = FileHelper(dataPath + botGroup.id.toString() + "\\")
                            fileHelper.getFileList().forEach { its ->
                                val response = Helper().get(its)
                                if(!response.isNullOrEmpty()) {
                                    val liveStatus = Regex("""['\"]stream['\"]: null""").find(response)?.value
                                    val filePath = FileHelper(cachePath + "\\",".tmp")
                                    var tmpFile = "${botGroup.id.toString()}-${its}"
                                    if (liveStatus.isNullOrEmpty()) {//直播中
                                        var huya = Gson().fromJson(fileHelper.get(its), HuYa::class.java)
                                        if(!filePath.exists(tmpFile)) {
                                            filePath.create(tmpFile,"")
                                            botGroup.sendMessage(huya.name + "@" + huya.id + "\n" + Helper().url + huya.id + "\n" + "正在直播中~")
                                        }
                                    } else {//未直播
                                        if(filePath.exists(tmpFile)) filePath.delete(tmpFile)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Timer().schedule(nudgeTimer, Date(), 30 * 1000)//默认30秒触发
        }
    }
}
