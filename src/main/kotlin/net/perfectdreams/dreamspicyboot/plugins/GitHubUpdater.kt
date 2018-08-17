package net.perfectdreams.dreamspicyboot.plugins

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.salomonbrys.kotson.array
import com.github.salomonbrys.kotson.fromJson
import com.github.salomonbrys.kotson.get
import net.perfectdreams.dreamspicyboot.DreamSpicyBoot
import net.perfectdreams.dreamspicyboot.DreamSpicyBoot.getContentAsString
import net.perfectdreams.dreamspicyboot.GitHubAsset
import net.perfectdreams.dreamspicyboot.utils.UpdateCheckState
import java.net.HttpURLConnection
import java.net.URL

@JsonIgnoreProperties(ignoreUnknown=true)
class GitHubUpdater @JsonCreator constructor(
		@JsonProperty("organization")
		val organization: String?
) : AutoUpdater() {
	lateinit var downloadUrl: String
	lateinit var index: String

	override fun hasUpdateAvailable(pluginInfo: ServerPlugin): UpdateCheckState {
		if (pluginInfo.version == null)
			return UpdateCheckState.NOT_FOUND

		if (checkIfAlreadyDownloaded(pluginInfo, pluginInfo.version))
			return UpdateCheckState.ALREADY_UPDATED

		val organization = organization ?: "PerfectDreams"
		val artifacts = getGitHubTaggedReleaseInfo(pluginInfo.name, organization, pluginInfo.version) ?: return UpdateCheckState.NOT_FOUND

		val firstArtifact = artifacts.firstOrNull() ?: return UpdateCheckState.NO_ARTIFACT_FOUND

		downloadUrl = firstArtifact.browserDownloadUrl
		index = pluginInfo.version

		return UpdateCheckState.UPDATE_AVAILABLE
	}

	override fun handleUpdate(pluginInfo: ServerPlugin) {
		val output = getJarFile(pluginInfo, "b$index")
		downloadUpdate(pluginInfo, downloadUrl, output)
	}

	fun getGitHubTaggedReleaseInfo(projectName: String, organization: String, buildIndex: String): List<GitHubAsset>? {
		val url = URL("https://api.github.com/repos/$organization/$projectName/releases/tags/$buildIndex")

		val connection = url.openConnection() as HttpURLConnection
		val responseCode = connection.responseCode

		if (responseCode == 404) {
			return null
		}

		val content = connection.getContentAsString()
		val payload = DreamSpicyBoot.jsonParser.parse(content)
		return DreamSpicyBoot.gson.fromJson(payload["assets"].array)
	}
}