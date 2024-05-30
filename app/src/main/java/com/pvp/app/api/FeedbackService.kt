package com.pvp.app.api

import com.google.firebase.Timestamp

interface FeedbackService : DocumentsCollection {

    override val identifier: String
        get() = "feedback"

    /**
     * Create a new feedback entry.
     *
     * @param bug Whether the feedback is a bug report.
     * @param email The email of the user submitting the feedback.
     * @param description The feedback description.
     * @param rating The rating of the feedback.
     * @param date The date the feedback was submitted.
     */
    suspend fun create(
        bug: Boolean,
        email: String,
        description: String,
        rating: Int,
        date: Timestamp
    )
}