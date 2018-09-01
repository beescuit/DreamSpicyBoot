package net.perfectdreams.dreamspicyboot

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.github.ajalt.mordant.TermColors
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import net.perfectdreams.dreamspicyboot.plugins.ServerPlugin
import net.perfectdreams.dreamspicyboot.tags.Tagging
import net.perfectdreams.dreamspicyboot.utils.UpdateCheckState
import org.jsoup.Jsoup
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.nio.file.Paths
import java.util.*
import java.util.regex.Pattern

object DreamSpicyBoot {
	val gson = Gson()
	val jsonParser = JsonParser()
	val mapper = ObjectMapper(YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER))
	val randomStartupTips = mutableListOf(
			"Yeah, I know... I gotta believe!",
			"Hmmmmm... Spicy Calamari!",
			"Sonhos que cabem no seu bolso!",
			"Transformando sonhos em realidade!",
			"A Lori e a Pantufa te amam! <3",
			"You are filled with DETERMINATION"
	)
	lateinit var PLUGINS_PARADISE_FOLDER: File

	const val USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:62.0) Gecko/20100101 Firefox/62.0"
	val random = SplittableRandom()
	val t = TermColors()
	val CIRCLECI_PATTERN = Pattern.compile("([0-9]+)-([0-9]+)")
	const val HEADER = """  _____                            _____       _            ____              _
 |  __ \                          / ____|     (_)          |  _ \            | |
 | |  | |_ __ ___  __ _ _ __ ___ | (___  _ __  _  ___ _   _| |_) | ___   ___ | |_
 | |  | | '__/ _ \/ _` | '_ ` _ \ \___ \| '_ \| |/ __| | | |  _ < / _ \ / _ \| __|
 | |__| | | |  __/ (_| | | | | | |____) | |_) | | (__| |_| | |_) | (_) | (_) | |_
 |_____/|_|  \___|\__,_|_| |_| |_|_____/| .__/|_|\___|\__, |____/ \___/ \___/ \__|
                                        | |            __/ |
                                        |_|           |___/                      """

	@JvmStatic
	fun main(args: Array<String>) {
		mapper.registerModule(KotlinModule())
				.configure(MapperFeature.PROPAGATE_TRANSIENT_MARKER, true)
				.setVisibility(
						mapper.serializationConfig.defaultVisibilityChecker.
								withFieldVisibility(JsonAutoDetect.Visibility.ANY).
								withGetterVisibility(JsonAutoDetect.Visibility.NONE).
								withIsGetterVisibility(JsonAutoDetect.Visibility.NONE)
				)
				.setSerializationInclusion(JsonInclude.Include.NON_NULL)

		// hmmmmmm, spicy! https://youtu.be/6zGCKhfXPVo
		println("\n" + t.yellow(HEADER))

		val randomTip = randomStartupTips[random.nextInt(randomStartupTips.size)]

		println(t.brightBlue(">> $randomTip"))

		val rootFolder = File(System.getProperty("serverFolder", ".")).absoluteFile
		val spicyFolder = Paths.get(DreamSpicyBoot::class.java.protectionDomain.codeSource.location.toURI()).toFile().parentFile

		val spicyConfigFile = File(spicyFolder, "config.yml")

		if (!spicyConfigFile.exists()) {
			error("config.yml não existe!")
			return
		}

		val spicyConfig = mapper.readValue(spicyConfigFile, SpicyConfig::class.java)

		val pluginsParadiseFolder = File(spicyConfig.pluginsFolder)

		PLUGINS_PARADISE_FOLDER = pluginsParadiseFolder

		if (args.getOrNull(0) == "manager") {
			val args = args.toMutableList()
			args.removeAt(0)
			DreamPackageManager.main(args.toTypedArray())
			return
		}

		val serverConfigFile = File(rootFolder, "server_config.yml")
		if (!serverConfigFile.exists()) {
			error("server_config.yml não existe!")
			return
		}

		val serverConfig = mapper.readValue(serverConfigFile, ServerConfig::class.java)

		with(t) {
			println("Preparando ${(magenta + bold)(serverConfig.serverName)}...")
			println("Versão                      ${serverConfig.serverVersion.getPretty()} / ${serverConfig.platformType.name}")
			println("Deletar plugins ao iniciar? ${colorfulBoolean(serverConfig.deletePluginsOnBoot)}")
			println("Plugins                     ${serverConfig.plugins.size}")
		}

		val pluginsFolder = File(rootFolder, "plugins")

		if (serverConfig.autoUpdate) {
			println(t.cyan("Verificando novas versões do servidor..."))

			val document = Jsoup.connect("https://yivesmirror.com/downloads/paperspigot")
					.userAgent(USER_AGENT)
					.get()
					.body()

			val rel = document.getElementsByAttributeValue("rel", serverConfig.serverVersion.getPretty())

			val parent = rel.first().parent()
			val downloadButton = parent.getElementsByTag("a").first()
			val href = downloadButton.attr("href")
			println(t.cyan("Download encontrado: ${href}"))
			val fileName = href.split("/").last()

			val storedServerJarFile = File(pluginsParadiseFolder, fileName)

			if (storedServerJarFile.exists()) {
				println(t.brightGreen("A última versão de ${serverConfig.serverVersion.getPretty()} / ${serverConfig.platformType.name} (${t.white(fileName)}) já está disponível no nosso repositório de plugins! ~(˘▾˘~)"))
			} else {
				val downloadUrl = URL("https://yivesmirror.com/files/paperspigot/$fileName")
				val downloadConnection = downloadUrl.openConnection() as HttpURLConnection
				downloadConnection.setRequestProperty("User-Agent", USER_AGENT)
				val downloadInputStream = downloadConnection.inputStream
				val jarBytes = downloadInputStream.readBytes()
				storedServerJarFile.writeBytes(jarBytes)

				println(t.brightGreen("${serverConfig.serverVersion.getPretty()} / ${serverConfig.platformType.name} (${t.white(fileName)}) foi atualizada com sucesso!"))
			}
			val file = File(rootFolder, "server.jar")
			storedServerJarFile.copyTo(file, true)

			println(t.brightGreen("Servidor atualizado com sucesso!"))
		}

		val eulaFile = File(rootFolder, "eula.txt")
		if (!eulaFile.exists()) {
			println(t.cyan("Criando e aceitando EULA..."))
			eulaFile.writeText("eula=true")
			println(t.brightGreen("EULA aceito com sucesso!"))
		}

		val list = serverConfig.plugins

		val deferred = list.filter { it.autoUpdater != null }.map { pluginInfo ->
			async {
				println(t.cyan("Verificando ${pluginInfo.name}..."))
				val autoUpdater = pluginInfo.autoUpdater!!

				val updateStatus = autoUpdater.hasUpdateAvailable(pluginInfo)

				when (updateStatus) {
					UpdateCheckState.NO_ARTIFACT_FOUND -> error("${t.brightYellow(pluginInfo.name)} não tem nenhum artifact no ${autoUpdater.javaClass.simpleName}!")
					UpdateCheckState.NOT_FOUND -> error("${t.brightYellow(pluginInfo.name)} não foi encontrado no ${autoUpdater.javaClass.simpleName}!")
					UpdateCheckState.ALREADY_UPDATED -> println(t.brightGreen("A última versão de ${t.brightYellow(pluginInfo.name)} já está disponível no nosso repositório de plugins! ~(˘▾˘~)"))
					UpdateCheckState.UPDATE_AVAILABLE -> {
						autoUpdater.handleUpdate(pluginInfo)
						println(t.brightGreen("${t.brightYellow(pluginInfo.name)} foi atualizado com sucesso!"))
					}
				}
			}
		}

		runBlocking {
			deferred.forEach {
				it.await()
			}

			pluginsFolder.mkdirs()
			if (serverConfig.deletePluginsOnBoot) {
				println(t.cyan("Deletando plugins antigos..."))
				pluginsFolder.listFiles().filter { it.extension == "jar" }.forEach { it.delete() }
			}
			println(t.cyan("Movendo plugins..."))

			// Agora nós iremos pegar todas as JARs necessárias para iniciar o servidor
			for (pluginInfo in list) {
				val sourceJar = getPluginJar(pluginInfo)

				if (sourceJar.exists()) {
					sourceJar.copyTo(File(pluginsFolder, "${pluginInfo.name}.jar"), true)
					println(t.brightGreen("${t.brightYellow(pluginInfo.name)} copiado com sucesso para a pasta do servidor! Nome: ${t.brightYellow(sourceJar.name)}"))
				} else {
					error("Source JAR de ${t.brightYellow(pluginInfo.name)} não existe ou não foi encontrada!")
				}
			}
		}

		println(t.cyan("Criando script de inicialização..."))

		// É windows ou linux?
		if (getSystem().startsWith("Windows")) {
			val startupScript = File(rootFolder, "start0.bat")
			startupScript.delete()

			var scriptLines = ""

			var javaStartup = "\"${spicyConfig.javaPath}\""

			if (spicyConfig.extraFlags != null) {
				javaStartup += " ${spicyConfig.extraFlags.replace("{{serverName}}", serverConfig.serverName.replace(" ", "_").toLowerCase())}"
			}
			if (serverConfig.jrebel.enabled && spicyConfig.jrebelFlags != null) {
				javaStartup += " ${spicyConfig.jrebelFlags.replace("{{jrebelPort}}", serverConfig.jrebel.port.toString())}"
			}
			val classpathJars = mutableListOf("server.jar")
			classpathJars.addAll(spicyConfig.classpathJars)

			val classPath = "\"${classpathJars.joinToString(";")}\""

			val mainClass = when (serverConfig.platformType) {
				PlatformType.PAPER -> "org.bukkit.craftbukkit.Main"
				PlatformType.AKARIN -> "net.minecraft.launchwrapper.Launch"
				else -> throw RuntimeException("${serverConfig.platformType.name} não é suportado!")
			}

			javaStartup += " ${serverConfig.flags} -cp $classPath $mainClass"

			scriptLines += javaStartup

			startupScript.writeText(scriptLines)

			println(t.brightGreen("Sucesso! Servidor está pronto para iniciar e brilhar! ʕ•ᴥ•ʔ"))


		} else if (getSystem() == "Linux"){
			val startupScript = File(rootFolder, "start0.sh")
			startupScript.delete()

			var scriptLines = ""

			for (line in HEADER.lines()) {
				scriptLines += "# $line\n"
			}

			scriptLines += "# $randomTip\n"
			scriptLines += "# Gerado às ${System.currentTimeMillis()}\n\n"

			var javaStartup = "/usr/local/jdk1.8.0_172/bin/java"

			if (spicyConfig.extraFlags != null) {
				javaStartup += " ${spicyConfig.extraFlags.replace("{{serverName}}", serverConfig.serverName.replace(" ", "_").toLowerCase())}"
			}
			if (serverConfig.jrebel.enabled && spicyConfig.jrebelFlags != null) {
				javaStartup += " ${spicyConfig.jrebelFlags.replace("{{jrebelPort}}", serverConfig.jrebel.port.toString())}"
			}

			val classpathJars = mutableListOf("server.jar")
			classpathJars.addAll(spicyConfig.classpathJars)

			val mainClass = when (serverConfig.platformType) {
				PlatformType.PAPER -> "org.bukkit.craftbukkit.Main"
				PlatformType.AKARIN -> "net.minecraft.launchwrapper.Launch"
				else -> throw RuntimeException("${serverConfig.platformType.name} não é suportado!")
			}

			javaStartup += " ${serverConfig.flags} -cp ${classpathJars.joinToString(":", transform = { "\"$it\""})} $mainClass"

			scriptLines += javaStartup

			startupScript.writeText(scriptLines)

			println(t.brightGreen("Sucesso! Servidor está pronto para iniciar e brilhar! ʕ•ᴥ•ʔ"))
		}


	}

	fun error(text: Any) {
		println(t.brightRed(text.toString()))
	}

	fun colorfulBoolean(boolean: Boolean): String {
		return if (boolean) {
			t.brightGreen("SIM")
		} else {
			t.red("NÃO")
		}
	}
	fun getSystem() : String {
		return System.getProperty("os.name")
	}

	fun getPluginJar(pluginInfo: ServerPlugin): File {
		val pluginFolder = File(PLUGINS_PARADISE_FOLDER, pluginInfo.name)
		pluginFolder.mkdirs()

		val tagsFile = File(pluginFolder, "tags.yml")

		var jarName = pluginInfo.name + ".jar"

		if (pluginInfo.version != null) {
			if (tagsFile.exists()) {
				val tagging = mapper.readValue(tagsFile, Tagging::class.java)
				val tag = tagging.tags.firstOrNull { it.name == pluginInfo.version }
				if (tag != null) {
					jarName = tag.jarName
				}
			} else {
				if (pluginInfo.version == "LATEST") {
					jarName = pluginFolder.listFiles().filter { it.extension == "jar" }.sortedByDescending { it.lastModified() }.first().name
				} else {
					jarName = pluginInfo.name + "-" + pluginInfo.version + ".jar"
				}
			}
		}

		return File(pluginFolder, jarName)
	}

	fun HttpURLConnection.getContentAsString(): String {
		val inputStream = this.inputStream
		val byteArray = inputStream.readBytes()
		val content = byteArray.toString(kotlin.text.Charsets.UTF_8)

		return content
	}
}
