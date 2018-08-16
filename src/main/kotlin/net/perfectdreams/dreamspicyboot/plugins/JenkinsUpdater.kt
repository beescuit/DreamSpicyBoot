package net.perfectdreams.dreamspicyboot.plugins

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.salomonbrys.kotson.fromJson
import com.github.salomonbrys.kotson.get
import com.github.salomonbrys.kotson.string
import net.perfectdreams.dreamspicyboot.DreamSpicyBoot
import net.perfectdreams.dreamspicyboot.JenkinsArtifact
import net.perfectdreams.dreamspicyboot.utils.UpdateCheckState
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.nio.file.Files

@JsonIgnoreProperties(ignoreUnknown=true)
class JenkinsUpdater @JsonCreator constructor(
		@JsonProperty("server")
		val server: String?,
		@JsonProperty("build-index")
		val buildIndex: String?
) : AutoUpdater() {
	fun getArtifactIndex() = buildIndex ?: "lastSuccessfulBuild"
	lateinit var downloadUrl: String
	lateinit var index: String

	override fun hasUpdateAvailable(pluginInfo: ServerPlugin): UpdateCheckState {
		val buildIndex = getArtifactIndex()
		val rootUrl = server ?: "https://jenkins.perfectdreams.net"
		val payload = getJenkinsArtifactInfo(pluginInfo.name, rootUrl, buildIndex) ?: return UpdateCheckState.NOT_FOUND

		val id = payload.first
		val artifacts = payload.second

		val firstArtifact = artifacts?.firstOrNull() ?: return UpdateCheckState.NO_ARTIFACT_FOUND

		if (checkIfAlreadyDownloaded(pluginInfo, id))
			return UpdateCheckState.ALREADY_UPDATED

		val firstArtifactRelativePath = firstArtifact.relativePath
		downloadUrl = "$rootUrl/job/${pluginInfo.name}/$buildIndex/artifact/$firstArtifactRelativePath"
		index = id

		return UpdateCheckState.UPDATE_AVAILABLE
	}

	override fun handleUpdate(pluginInfo: ServerPlugin) {
		val output = getJarFile(pluginInfo, "b$index")
		downloadUpdate(pluginInfo, downloadUrl, output)

		if (getArtifactIndex() == "lastSuccessfulBuild") {
			val latest = File(DreamSpicyBoot.PLUGINS_PARADISE_FOLDER, "${pluginInfo.name}/${pluginInfo.name}.jar")
			latest.delete()
			Files.createSymbolicLink(latest.toPath(), output.toPath())
		}
	}

	fun getJenkinsArtifactInfo(jobName: String, rootUrl: String, buildIndex: String = "lastSuccessfulBuild"): Pair<String, List<JenkinsArtifact>?>? {
		val url = URL("$rootUrl/job/$jobName/$buildIndex/api/json")

		val connection = url.openConnection() as HttpURLConnection
		val responseCode = connection.responseCode

		if (responseCode == 404) {
			return null
		}

		val inputStream = connection.inputStream
		val byteArray = inputStream.readBytes()
		val content = byteArray.toString(Charsets.UTF_8)

		val payload = DreamSpicyBoot.jsonParser.parse(content)
		return Pair(payload["id"].string, DreamSpicyBoot.gson.fromJson(payload["artifacts"]))
	}
}