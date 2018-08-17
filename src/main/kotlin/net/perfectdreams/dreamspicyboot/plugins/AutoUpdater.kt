package net.perfectdreams.dreamspicyboot.plugins

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import net.perfectdreams.dreamspicyboot.DreamSpicyBoot
import net.perfectdreams.dreamspicyboot.utils.UpdateCheckState
import java.io.File
import java.net.URL

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "update-source")
@JsonSubTypes(
		JsonSubTypes.Type(value = JenkinsUpdater::class, name = "JenkinsUpdater"),
		JsonSubTypes.Type(value = CircleUpdater::class, name = "CircleUpdater"),
		JsonSubTypes.Type(value = GitHubUpdater::class, name = "GitHubUpdater")
)
abstract class AutoUpdater {
	abstract fun hasUpdateAvailable(pluginInfo: ServerPlugin): UpdateCheckState

	fun checkIfAlreadyDownloaded(pluginInfo: ServerPlugin, suffix: String): Boolean {
		val jarFile = getJarFile(pluginInfo, suffix)

		return jarFile.exists()
	}

	fun getJarFile(pluginInfo: ServerPlugin, suffix: String): File {
		val pluginFolder = File(DreamSpicyBoot.PLUGINS_PARADISE_FOLDER, pluginInfo.name)
		pluginFolder.mkdirs()

		val storedJarName = ("${pluginInfo.name}-{suffix}.jar").replace("{suffix}", suffix)
		val jarFile = File(pluginFolder, storedJarName)

		return jarFile
	}

	fun getJarFileWithName(pluginInfo: ServerPlugin, name: String): File {
		val pluginFolder = File(DreamSpicyBoot.PLUGINS_PARADISE_FOLDER, pluginInfo.name)
		pluginFolder.mkdirs()
		val jarFile = File(pluginFolder, name)

		return jarFile
	}

	abstract fun handleUpdate(pluginInfo: ServerPlugin)

	fun downloadUpdate(pluginInfo: ServerPlugin, url: String, jar: File) {
		// Hora de baixar o artifact!
		val downloadUrl = URL(url)
		val downloadConnection = downloadUrl.openConnection()
		val downloadInputStream = downloadConnection.getInputStream()
		val jarBytes = downloadInputStream.readBytes()

		val circleArtifactFile = jar
		circleArtifactFile.writeBytes(jarBytes)
	}
}