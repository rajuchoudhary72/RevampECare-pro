package com.app.ecarepro.model

data class RequiredField(
    val canOpenForStudent: Int,
    val isAttachmentRequired: Boolean,
    val isAuditoryRequired: Boolean,
    val isClosureRequired: Boolean,
    val isExtensionTopicRequired: Boolean,
    val isIntroductionRequired: Boolean,
    val isKinestheticActivityRequired: Boolean,
    val isLearningOutcomesRequired: Boolean,
    val isObjectiveRequired: Boolean,
    val isOtherResourcesRequired: Boolean,
    val isResourcesRequired: Boolean,
    val isTopicRequired: Boolean,
    val isYoutubeLinksRequired: Boolean,
    val backDate: Int
)