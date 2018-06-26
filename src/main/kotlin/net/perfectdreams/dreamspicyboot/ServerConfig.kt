package net.perfectdreams.dreamspicyboot

class ServerConfig(
		val serverName: String,
		val platformType: PlatformType,
		val serverVersion: ServerVersion,
		val autoUpdate: Boolean,
		val deletePluginsOnBoot: Boolean,
		val enableJRebel: Boolean,
		val jrebelPort: Int,
		val flags: String,
		val plugins: List<PluginInfo>
)