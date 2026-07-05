package nl.t64.cot.components.conversation

import com.fasterxml.jackson.annotation.JsonProperty


class AlternateStart(
    @JsonProperty("condition")
    val conditions: List<String>,
    val startId: String
)
