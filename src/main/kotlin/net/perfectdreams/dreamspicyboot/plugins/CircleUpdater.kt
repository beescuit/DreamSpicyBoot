package net.perfectdreams.dreamspicyboot.plugins

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.salomonbrys.kotson.fromJson
import net.perfectdreams.dreamspicyboot.CircleArtifact
import net.perfectdreams.dreamspicyboot.DreamSpicyBoot
import net.perfectdreams.dreamspicyboot.DreamSpicyBoot.CIRCLECI_PATTERN
import net.perfectdreams.dreamspicyboot.utils.UpdateCheckState
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.nio.file.Files

@JsonIgnoreProperties(ignoreUnknown=true)
class CircleUpdater @JsonCreator constructor(
		@JsonProperty("organization")
		val organization: String?,
		@JsonProperty("build-index")
		val buildIndex: String?
) : AutoUpdater() {
	fun getArtifactIndex() = buildIndex ?: "latest"
	@Transient
	@JsonIgnore
	lateinit var downloadUrl: String
	@Transient
	@JsonIgnore
	lateinit var index: String

	override fun hasUpdateAvailable(pluginInfo: ServerPlugin): UpdateCheckState {
		val payload = getCircleArtifactInfo("github", organization
				?: "PerfectDreams", pluginInfo.name, getArtifactIndex()) ?: return UpdateCheckState.NOT_FOUND

		val firstArtifact = payload.firstOrNull() ?: return UpdateCheckState.NO_ARTIFACT_FOUND

		val matcher = CIRCLECI_PATTERN.matcher(firstArtifact.url).apply { this.find() }
		val buildNumber = matcher.group(1)

		if (checkIfAlreadyDownloaded(pluginInfo, "b$buildNumber"))
			return UpdateCheckState.ALREADY_UPDATED

		downloadUrl = firstArtifact.url
		index = buildNumber

		return UpdateCheckState.UPDATE_AVAILABLE
	}

	override fun handleUpdate(pluginInfo: ServerPlugin) {
		val output = getJarFile(pluginInfo, "b$index")
		downloadUpdate(pluginInfo, downloadUrl, output)

		if (getArtifactIndex() == "latest") {
			val latest = File(DreamSpicyBoot.PLUGINS_PARADISE_FOLDER, "${pluginInfo.name}/${pluginInfo.name}.jar")
			latest.delete()
			Files.createSymbolicLink(latest.toPath(), output.toPath())
		}
	}

	fun getCircleArtifactInfo(vcsType: String, organization: String, repository: String, buildIndex: String = "latest"): List<CircleArtifact>? {
		val url = URL("https://circleci.com/api/v1.1/project/$vcsType/$organization/$repository/$buildIndex/artifacts")

		val connection = url.openConnection() as HttpURLConnection
		val responseCode = connection.responseCode

		if (responseCode == 404) {
			return null
		}

		val inputStream = connection.inputStream
		val byteArray = inputStream.readBytes()
		val content = byteArray.toString(Charsets.UTF_8)

		return DreamSpicyBoot.gson.fromJson(content)
	}
}