package de.miraculixx.mcord_event.modules.events.halloween.data

enum class CreatureType(val rarity: Rarity, val spawnMessage: String, val imageURL: String) {
    BAT(Rarity.COMMON, "Eine wilde Fledermaus kommt angeflogen", "https://i.pinimg.com/originals/d9/7a/6d/d97a6d99e7bde5141805653993793a9f.gif"),
    SKELETON(Rarity.COMMON, "Du hast ein einsames Skelett gefunden", "https://c.tenor.com/oNKt0lStkY8AAAAd/spooky-scary.gif"),
    SPIDER(Rarity.COMMON, "Eine wilde Spinne krabbelt auf dich zu", "https://i.imgur.com/pDVLoqA.gif"),
    GHOST(Rarity.COMMON, "Ein einsamer Geist ist erschienen", "https://i.imgur.com/BUmBdUw.gif"),

    ZOMBIE(Rarity.RARE, "Ein hungriger Zombie kriecht um die Ecke", "https://i.imgur.com/36EAwp4.gifv"),
    WITCH(Rarity.RARE, "Du hast eine lachende Hexe angelockt", "https://media.tenor.com/E6oo2-KiwmIAAAAC/candle-candle-light.gif"),
    WOLF(Rarity.RARE, "Ein lechzender grauer Wolf setzt zum Sprung an", "https://i.imgur.com/7iKTqyU.png"),

    PUMPKIN(Rarity.EPIC, "Du findest einen leuchtenden, gruseligen Kürbis", "https://w0.peakpx.com/wallpaper/142/268/HD-wallpaper-halloween-fantasy-fantasy-halloween-pretty-art-digital.jpg"),
    MUMMY(Rarity.EPIC, "Eine uralte Mumie erwacht vor dir zum Leben", "https://i.giphy.com/media/lkZ4CPeKrpbOOENaRK/200w.gif"),
    VAMPIRE(Rarity.EPIC, "Du hast einen durstigen Vampir angelockt", "https://media.tenor.com/R0MXDlw4fRMAAAAC/vampire-awake.gif"),
    DEMON(Rarity.EPIC, "Ein heimtückischer Dämon schleicht sich an", "https://i.imgur.com/bnXcxBc.png"),

    HEADLESS_HORSEMAN(Rarity.LEGENDARY, "Ein kopfloser Reiter galoppiert mit Gelächter auf dich zu", "https://media.tenor.com/iIIWEock5A8AAAAC/horse-running.gif"),
    REAPER(Rarity.LEGENDARY, "Du hast einen mystischen Sensenmann beschworen", "https://media.tenor.com/JZvKt1uKxcEAAAAC/horror-scary.gif")
}