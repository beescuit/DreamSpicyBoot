package net.perfectdreams.dreamspicyboot

enum class UpdateSource {
	// Development
	CIRCLECI,
	// Development
	JENKINS,
	// Stable
	SPIGOT,
	// Stable (tagged releases)
	GITHUB
}