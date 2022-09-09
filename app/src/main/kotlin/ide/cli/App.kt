package ide.cli

import UnzipUtils
import com.github.ajalt.clikt.core.CliktCommand
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.nio.channels.Channels

class UpdatePlugin : CliktCommand() {
    private fun downloadFile(url: URL, outputFileName: String) {
        url.openStream().use {
            Channels.newChannel(it).use { rbc ->
                FileOutputStream(outputFileName).use { fos ->
                    fos.channel.transferFrom(rbc, 0, Long.MAX_VALUE)
                }
            }
        }
    }

//    private fun String.runCommand(workingDir: File) {
//        ProcessBuilder(*split(" ").toTypedArray())
//            .directory(workingDir)
//            .redirectOutput(ProcessBuilder.Redirect.INHERIT)
//            .redirectError(ProcessBuilder.Redirect.INHERIT)
//            .start()
//            .waitFor(60, TimeUnit.MINUTES)
//    }


    // TODO: detect if gateway is installed
    val gatewayBin = "/Applications/JetBrains Gateway.app/Contents/MacOS/gateway"

    // TODO: make this dynamic
    private fun getPluginsDir() :String  {
        return "/System/Volumes/Data/Users/andrea/Library/Application Support/JetBrains/JetBrainsGateway2022.2/plugins"
    }

    // TODO: check if plugin exists and adjust log
    private fun removePlugin() {
        val dir = File("${getPluginsDir()}/gitpod-gateway")
        if (dir.isDirectory) {
            echo("✅ Uninstalled old plugin")
            dir.deleteRecursively()
        }
    }

    // TODO: allow passing URL
    private fun downloadPlugin() {
        downloadFile(
//            URL("https://plugins.jetbrains.com/plugin/download?rel=true&updateId=221350"),
            URL("https://plugins.jetbrains.com/plugin/download?rel=true&updateId=220845"),
            "/Users/andrea/dev/gitpod/kotlin-cli/gitpod-plugin.zip"
        )
        echo("✅ Plugin downloaded")
    }

    // TODO: make path dynamic
    private fun installPlugin() {
        UnzipUtils.unzip(File("/Users/andrea/dev/gitpod/kotlin-cli/gitpod-plugin.zip"), getPluginsDir())
    }

    private fun stopGateway() {
        Runtime.getRuntime().exec("killall gateway")
        echo("✅ Gateway stopped")
    }

    private fun startGateway() {
        ProcessBuilder(
            gatewayBin
        ).start()

        echo("✅ Gateway started")
    }

    // TODO: prompt user for version number
    override fun run() = runBlocking {
        stopGateway();
        removePlugin()
        downloadPlugin()
        installPlugin()
        startGateway()
    }
}

fun main(args: Array<String>) = UpdatePlugin().main(args)
