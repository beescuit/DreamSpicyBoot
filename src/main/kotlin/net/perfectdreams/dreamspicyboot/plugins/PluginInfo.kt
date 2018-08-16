package net.perfectdreams.dreamspicyboot.plugins

import net.perfectdreams.dreamspicyboot.UpdateSource

class PluginInfo(
		val name: String,
		val organization: String?,
		val autoUpdate: Boolean,
		val updateFrom: UpdateSource?,
		val jarName: String?,
		val sourceJarPattern: String?,
		val storedJarName: String?,
		val buildIndex: String?
)