package net.perfectdreams.dreamspicyboot.plugins

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import net.perfectdreams.dreamspicyboot.UpdateSource

@JsonIgnoreProperties(ignoreUnknown=true)
class PluginInfo @JsonCreator constructor(
		@JsonProperty("name")
		val name: String,
		@JsonProperty("organization")
		val organization: String?,
		@JsonProperty("auto-update")
		val autoUpdate: Boolean,
		@JsonProperty("update-from")
		val updateFrom: UpdateSource?,
		@JsonProperty("jar-name")
		val jarName: String?,
		@JsonProperty("source-jar-pattern")
		val sourceJarPattern: String?,
		@JsonProperty("stored-jar-name")
		val storedJarName: String?,
		@JsonProperty("build-index")
		val buildIndex: String?
)