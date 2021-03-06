package shiroi_plugin.huya

import net.mamoe.mirai.alsoLogin
import net.mamoe.mirai.console.MiraiConsole
import net.mamoe.mirai.console.plugin.PluginManager.INSTANCE.enable
import net.mamoe.mirai.console.plugin.PluginManager.INSTANCE.load
import net.mamoe.mirai.console.terminal.MiraiConsoleTerminalLoader

suspend fun main() {
    MiraiConsoleTerminalLoader.startAsDaemon()

    //如果是Kotlin
    PluginMain.load()
    PluginMain.enable()

    val bot = MiraiConsole.addBot(1111111111, "1111111111") {
        fileBasedDeviceInfo()
    }.alsoLogin()

    MiraiConsole.job.join()
}