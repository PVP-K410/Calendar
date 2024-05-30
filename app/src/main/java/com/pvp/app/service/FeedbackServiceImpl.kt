package com.pvp.app.service

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.pvp.app.api.FeedbackService
import com.pvp.app.model.Feedback
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FeedbackServiceImpl @Inject constructor(
    private val database: FirebaseFirestore
) : FeedbackService {

    override suspend fun create(
        bug: Boolean,
        email: String,
        description: String,
        rating: Int,
        date: Timestamp
    ) {
        val reference = database
            .collection(identifier)
            .document()

        val feedback = Feedback(
            id = reference.id,
            bug = bug,
            date = date,
            description = description,
            email = email,
            rating = rating
        )

        reference
            .set(feedback)
            .await()
    }
}