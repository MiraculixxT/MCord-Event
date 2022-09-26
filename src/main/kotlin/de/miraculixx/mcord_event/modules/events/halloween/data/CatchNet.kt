package de.miraculixx.mcord_event.modules.events.halloween.data

// "pronoun" because name cannot be overridden, bec enum implementation is shit
enum class CatchNet(val pronoun: String) {
    BASIC("Basic"),
    SILVER("Silber"),
    GOLD("Gold")
}