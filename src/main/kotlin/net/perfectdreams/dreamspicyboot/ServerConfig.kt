package net.perfectdreams.dreamspicyboot

import com.fasterxml.jackson.annotation.JsonProperty
import net.perfectdreams.dreamspicyboot.plugins.PluginInfo

class ServerConfig(
		@JsonProperty("server-name")
		val serverName: String,
		@JsonProperty("platform-type")
		val platformType: PlatformType,
		@JsonProperty("minecraft-version")
		val serverVersion: ServerVersion,
		@JsonProperty("auto-update")
		val autoUpdate: Boolean,
		@JsonProperty("delete-plugins-on-boot")
		val deletePluginsOnBoot: Boolean,
		@JsonProperty("jrebel")
		val jrebel: JRebelConfig,
		@JsonProperty("flags")
		val flags: String,
		@JsonProperty("plugins")
		val plugins: List<PluginInfo>
)

class JRebelConfig(val enabled: Boolean, val port: Int)