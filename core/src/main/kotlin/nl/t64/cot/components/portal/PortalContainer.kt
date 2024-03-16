package nl.t64.cot.components.portal


class PortalContainer {

    private val portals: MutableMap<String, Boolean> = mutableMapOf(
        Portal.HONEYWOOD_GREAT_TREE.name to false,
        Portal.HONEYWOOD_HOUSE_ELDER_B2.name to false,
        Portal.LASTDENN.name to false
    )

    fun getAllIds(): Array<String> {
        return portals.keys.toTypedArray()
    }

    fun getAllActivatedPortalsExcept(currentMapName: String): Array<Portal> {
        return portals
            .filterKeys { it != currentMapName }
            .filterValues { it }
            .map { Portal.valueOf(it.key) }
            .toTypedArray()
    }

    fun isActivated(portalId: String): Boolean {
        return portals[portalId]!!
    }

    fun activate(portalId: String) {
        portals[portalId] = true
    }

}
