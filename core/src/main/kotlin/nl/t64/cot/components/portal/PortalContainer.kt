package nl.t64.cot.components.portal


class PortalContainer {

    private val portals: MutableMap<String, Boolean> = createPortalMap()

    fun updateOutdatedData() {
        val currentPortals: Map<String, Boolean> = createPortalMap()
        currentPortals
            .filterKeys { it !in portals }
            .forEach { portals[it.key] = it.value }
        portals.keys.retainAll(currentPortals.keys)
    }

    fun getAllIds(): Array<String> {
        return portals.keys.toTypedArray()
    }

    fun getAllActivatedPortalsIncludingBed(): Array<Portal> {
        return portals
            .filterValues { it }
            .map { Portal.valueOf(it.key) }
            .toTypedArray()
    }

    fun getAllActivatedPortalsExcept(currentMapName: String): Array<Portal> {
        return portals
            .filterKeys { it != Portal.HONEYWOOD_HOUSE_MOZES.name }
            .filterKeys { it != currentMapName }
            .filterValues { it }
            .map { Portal.valueOf(it.key) }
            .toTypedArray()
    }

    fun areNoneActivated(): Boolean {
        return portals.values.none { it }
    }

    fun isActivated(portalId: String): Boolean {
        return portals[portalId]!!
    }

    fun activate(portalId: String) {
        portals[portalId] = true
    }

    private fun createPortalMap(): MutableMap<String, Boolean> {
        return Portal.entries.associate { it.name to it.isActivatedAtStart }.toMutableMap()
    }

}
