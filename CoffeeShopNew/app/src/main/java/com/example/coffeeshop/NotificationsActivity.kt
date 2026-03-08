package com.example.coffeeshop

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class NotificationsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private var notifications = mutableListOf<NotificationItem>()
    private var adapter: NotificationsAdapter? = null
    private var listener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        recyclerView = findViewById(R.id.notificationsRecycler)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = NotificationsAdapter(notifications) { notification ->
            openNotificationDetail(notification)
        }
        recyclerView.adapter = adapter

        findViewById<ImageView>(R.id.backButton).setOnClickListener { finish() }

        fetchNotifications()
    }

    private fun fetchNotifications() {
        val userId = FirebaseHelper.auth.currentUser?.uid ?: return

        listener = FirebaseHelper.firestore
            .collection("Users")
            .document(userId)
            .collection("Notifications")
            .orderBy("time", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener

                notifications.clear()
                for (doc in snapshot.documents) {
                    val item = NotificationItem(
                        id = doc.id,
                        title = doc.getString("title") ?: "",
                        message = doc.getString("message") ?: "",
                        time = doc.getString("time") ?: "",
                        isRead = doc.getBoolean("isRead") ?: false
                    )
                    notifications.add(item)
                }
                adapter?.notifyDataSetChanged()
            }
    }

    private fun openNotificationDetail(notification: NotificationItem) {
        // Mark as read in Firestore
        val userId = FirebaseHelper.auth.currentUser?.uid ?: return
        FirebaseHelper.firestore
            .collection("Users")
            .document(userId)
            .collection("Notifications")
            .document(notification.id)
            .update("isRead", true)

        // Open detail activity
        val intent = Intent(this, NotificationDetailActivity::class.java)
        intent.putExtra("title", notification.title)
        intent.putExtra("message", notification.message)
        intent.putExtra("time", notification.time)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        listener?.remove()
    }
}
