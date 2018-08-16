package net.perfectdreams.dreamspicyboot.plugins

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Um plugin qualquer
 */
@JsonIgnoreProperties(ignoreUnknown=true)
open class ServerPlugin @JsonCreator constructor(
		@JsonProperty("name")
		val name: String,
		@JsonProperty("version")
		val version: String?,
		@JsonProperty("auto-update")
		val autoUpdater: AutoUpdater?
)