package net.perfectdreams.dreamspicyboot

import com.github.ajalt.mordant.TermColors
import net.perfectdreams.dreamspicyboot.plugins.ServerPlugin
import net.perfectdreams.dreamspicyboot.tags.Tag
import net.perfectdreams.dreamspicyboot.tags.Tagging
import net.perfectdreams.dreamspicyboot.utils.VersionComparator
import net.perfectdreams.dreamspicyboot.utils.VersionTokenizer
import java.io.File

object DreamPackageManager {
	val t = TermColors()
	lateinit var rootFolder: File

	fun main(args: Array<String>) {
		// DreamPackageManager
		rootFolder = File(System.getProperty("serverFolder", ".")).absoluteFile
		val serverConfigFile = File(rootFolder, "server_config.yml")
		val serverConfig = if (serverConfigFile.exists()) {
			DreamSpicyBoot.mapper.readValue(serverConfigFile, ServerConfig::class.java)
		} else {
			null
		}

		val arg0 = args.getOrNull(0)
		val commandArgs = args.toMutableList()
		commandArgs.removeAt(0)
		if (arg0 == "deploy") {
			deployPlugin(commandArgs.toTypedArray())
		}
		if (arg0 == "add") {
			checkServerConfig(serverConfig)
			addPlugin(serverConfig!!, commandArgs.toTypedArray())
		}
		if (arg0 == "remove") {
			checkServerConfig(serverConfig)
			removePlugin(serverConfig!!, commandArgs.toTypedArray())
		}
		if (arg0 == "update") {
			checkServerConfig(serverConfig)
			updatePlugins(serverConfig!!, commandArgs.toTypedArray())
		}
		if (arg0 == "list") {
			checkServerConfig(serverConfig)
			listInstalledPlugins(serverConfig!!)
		}
	}

	fun updatePlugins(serverConfig: ServerConfig, args: Array<String>) {
		val newPlugins = mutableListOf<ServerPlugin>()
		val oldPlugins = mutableListOf<ServerPlugin>()

		serverConfig.plugins.forEach {
			val pluginName = it.name
			val version = it.version

			if (version == null) {
				println(t.brightRed("$pluginName não possui versão na configuração!"))
				return@forEach
			}

			val pluginFolder = File(DreamSpicyBoot.PLUGINS_PARADISE_FOLDER, pluginName)
			if (!pluginFolder.exists()) {
				println(t.brightRed("$pluginName não está na pasta de plugins! Você já deu deploy nele?"))
				return@forEach
			}

			val taggingFile = File(pluginFolder, "tags.yml")
			val tagging = if (taggingFile.exists()) {
				DreamSpicyBoot.mapper.readValue(taggingFile, Tagging::class.java)
			} else {
				println(t.brightYellow("$pluginName não possui tags!"))
				return@forEach
			}

			var newestVersion = version

			for (tag in tagging.tags) {
				val comparator = VersionComparator()
				val tokenizer1 = VersionTokenizer(tag.name)
				val tokenizer2 = VersionTokenizer(newestVersion)

				if (comparator.compare(tokenizer1, tokenizer2) == 1) {
					newestVersion = tag.name
				}
			}

			if (newestVersion != version) {
				println(t.brightCyan("Uma versão mais nova de $pluginName está disponível!"))
				println(t.cyan("Atual: $version"))
				println(t.cyan("Nova : $newestVersion"))
				println(t.brightCyan("Deseja atualizar? Para cancelar, escreva \"não\""))
				val input = readLine()!!
				if (input.startsWith("n")) {
					println(t.brightGreen("Okay... Não iremos atualizar o $pluginName"))
					return
				} else {
					oldPlugins.add(it)
					newPlugins.add(
							ServerPlugin(
									it.name,
									newestVersion,
									it.autoUpdater
							)
					)
					println(t.brightGreen("Atualizado com sucesso!"))
				}
			} else {
				println(t.brightGreen("$pluginName já está na última versão disponível em nosso repositório!"))
			}
		}

		val serverConfigFile = File(rootFolder, "server_config.yml")

		if (newPlugins.isNotEmpty()) {
			serverConfig.plugins.removeAll(oldPlugins)
			serverConfig.plugins.addAll(newPlugins)
			DreamSpicyBoot.mapper.writeValue(serverConfigFile, serverConfig)
			println(t.green("Finalizado com sucesso! As mudanças foram salvas ^-^"))
		} else {
			println(t.green("Finalizado com sucesso! Nenhuma mudança foi realizada..."))
		}
	}

	fun deployPlugin(args: Array<String>) {
		val pluginFile = File(args[0])
		if (!pluginFile.exists()) {
			println(t.brightRed("O plugin não existe!"))
			return
		}

		// hora de fazer deploy
		val pluginName = args[1]
		val split = pluginFile.nameWithoutExtension.split("-")
		var version = split.last()
		if (version == "SNAPSHOT") {
			version = split[split.size - 2] + "-SNAPSHOT"
		}
		val realVersion = if (version != pluginFile.nameWithoutExtension) version else args.getOrNull(2)

		println(t.brightCyan("Nome: $pluginName"))
		println(t.brightCyan("Versão: ${realVersion ?: "???"}"))
		println(t.brightCyan("JAR: ${pluginFile.name}"))
		println(t.white("Está tudo certo? Para cancelar, escreva \"não\""))

		val input = readLine()!!
		if (input.startsWith("n")) {
			println(t.brightGreen("Cancelando deploy..."))
			return
		} else {
			println(t.brightGreen("Iniciando deploy!"))
			val paradise = DreamSpicyBoot.PLUGINS_PARADISE_FOLDER
			val pluginFolder = File(paradise, pluginName)
			pluginFolder.mkdirs()
			pluginFile.copyTo(File(pluginFolder, pluginFile.name), true)

			if (realVersion != null) {
				val taggingFile = File(pluginFolder, "tags.yml")
				val tagging = if (taggingFile.exists()) {
					DreamSpicyBoot.mapper.readValue(taggingFile, Tagging::class.java)
				} else {
					Tagging(mutableListOf())
				}
				tagging.tags.add(
						Tag(
								realVersion,
								pluginFile.name
						)
				)
				DreamSpicyBoot.mapper.writeValue(taggingFile, tagging)
				println(t.brightGreen("A tag da versão deste plugin foi criada!"))
			}

			println(t.brightGreen("Prontinho! ^-^"))
			return
		}
	}

	fun addPlugin(serverConfig: ServerConfig, args: Array<String>) {
		val pluginName = args.getOrNull(0) ?: return
		val paradise = DreamSpicyBoot.PLUGINS_PARADISE_FOLDER

		val pluginFolder = File(paradise, pluginName)
		if (!pluginFolder.exists()) {
			println(t.brightRed("$pluginName não está na pasta de plugins! Você já deu deploy nele?"))
			return
		}

		val versions = mutableListOf<String>()
		versions.add("LATEST")

		val taggingFile = File(pluginFolder, "tags.yml")
		if (taggingFile.exists()) {
			val tagging = DreamSpicyBoot.mapper.readValue(taggingFile, Tagging::class.java)

			versions.addAll(tagging.tags.map { it.name })
		}

		var useVersion = "LATEST"
		if (versions.size != 0) {
			println(t.brightCyan("Qual versão será usada?"))
			for ((index, version) in versions.withIndex()) {
				val counter = index + 1
				println("$counter. ${t.brightGreen(version)}")
			}
			val input = readLine()!!.toInt()

			val versionToBeUsed = versions[input - 1]
			println(t.brightGreen("Iremos usar a versão $versionToBeUsed!"))
			useVersion = versionToBeUsed
		} else {
			println(t.brightYellow("$pluginName não possui tags! Nós iremos usar \"LATEST\" na versão do plugin..."))
		}

		serverConfig.plugins.add(
				ServerPlugin(
						pluginName,
						useVersion,
						null
				)
		)

		DreamSpicyBoot.mapper.writeValue(File(rootFolder, "server_config.yml"), serverConfig)

		println(t.brightGreen("$pluginName foi adicionado com sucesso!"))
	}

	fun removePlugin(serverConfig: ServerConfig, args: Array<String>) {
		val pluginName = args.getOrNull(0) ?: return
		serverConfig.plugins = serverConfig.plugins.filter { it.name != pluginName }.toMutableList()

		DreamSpicyBoot.mapper.writeValue(File(rootFolder, "server_config.yml"), serverConfig)

		println(t.brightGreen("$pluginName foi removido com sucesso!"))
	}

	fun listInstalledPlugins(serverConfig: ServerConfig) {
		serverConfig.plugins.forEach {
			println(t.brightWhite(it.name))
			val pluginFile = DreamSpicyBoot.getPluginJar(it)
			val jarName = if (pluginFile.exists()) {
				pluginFile.name
			} else {
				"???"
			}

			val hasNext = it.autoUpdater != null
			println("├─ ${t.brightCyan("Versão: ${it.version ?: "???"}")}")
			if (hasNext) {
				println("├─ ${t.brightCyan("JAR: $jarName")}")
				if (it.autoUpdater != null) {
					println("└─ ${t.brightYellow("Atualização Automática")}")
					println("   └─ ${t.brightCyan("Tipo: ${it.autoUpdater.javaClass.simpleName}")}")
				}
			} else {
				println("└─ ${t.brightCyan("JAR: $jarName")}")
			}
		}

		println("")
		println(t.cyan("${serverConfig.plugins.size} plugins instalados"))
	}

	fun checkServerConfig(serverConfig: ServerConfig?) {
		if (serverConfig == null)
			throw RequiresServerException("Precisa de um server config válido!")
	}

	class RequiresServerException(message: String) : RuntimeException(message)
}