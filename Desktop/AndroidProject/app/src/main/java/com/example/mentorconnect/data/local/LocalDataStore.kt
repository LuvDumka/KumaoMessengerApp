package com.example.mentorconnect.data.local

import com.example.mentorconnect.data.model.*
import java.util.concurrent.ConcurrentHashMap

/**
 * In-memory data store for local testing without Firebase.
 * All data persists only while app is running.
 */
object LocalDataStore {
    
    // Storage maps
    private val users = ConcurrentHashMap<String, UserProfile>()
    private val mentors = ConcurrentHashMap<String, Mentor>()
    private val mentorProfiles = ConcurrentHashMap<String, MentorProfile>()
    private val timeSlots = ConcurrentHashMap<String, TimeSlot>()
    private val videoSessions = ConcurrentHashMap<String, VideoCallSession>()
    private val messages = ConcurrentHashMap<String, MutableList<ChatMessage>>()
    private val chatThreads = ConcurrentHashMap<String, MutableList<ChatThread>>()
    
    // Current logged-in user
    var currentUserId: String? = null
    var currentUserEmail: String? = null
    
    init {
        seedInitialData()
    }
    
    // User operations
    fun saveUser(user: UserProfile) {
        users[user.uid] = user
    }
    
    fun getUser(uid: String): UserProfile? = users[uid]
    
    fun getUserByEmail(email: String): UserProfile? =
        users.values.find { it.email == email }
    
    fun getAllUsers(): List<UserProfile> = users.values.toList()
    
    // Mentor operations
    fun saveMentor(mentor: Mentor) {
        mentors[mentor.id] = mentor
    }
    
    fun getMentor(id: String): Mentor? = mentors[id]
    
    fun getAllMentors(): List<Mentor> = mentors.values.toList()
    
    fun saveMentorProfile(profile: MentorProfile) {
        mentorProfiles[profile.mentorId] = profile
        // Also update user profile completion status
        users[profile.mentorId]?.let {
            users[profile.mentorId] = it.copy(profileCompleted = true)
        }
    }
    
    fun getMentorProfile(mentorId: String): MentorProfile? = mentorProfiles[mentorId]
    
    // Time slot operations
    fun saveTimeSlot(slot: TimeSlot) {
        timeSlots[slot.id] = slot
    }
    
    fun getTimeSlot(id: String): TimeSlot? = timeSlots[id]
    
    fun getSlotsForMentor(mentorId: String, date: String): List<TimeSlot> =
        timeSlots.values.filter { it.mentorId == mentorId && it.date == date }
            .sortedBy { it.startTime }
    
    fun getMyBookings(userId: String): List<TimeSlot> =
        timeSlots.values.filter { 
            it.bookedByUserId == userId && 
            (it.status == SlotStatus.BOOKED || it.status == SlotStatus.COMPLETED)
        }
    
    fun getMentorSessions(mentorId: String): List<TimeSlot> =
        timeSlots.values.filter { 
            it.mentorId == mentorId && 
            (it.status == SlotStatus.BOOKED || it.status == SlotStatus.COMPLETED)
        }
    
    fun bookSlot(slotId: String, userId: String, userName: String, message: String): Boolean {
        val slot = timeSlots[slotId] ?: return false
        if (slot.status != SlotStatus.AVAILABLE) return false
        
        timeSlots[slotId] = slot.copy(
            status = SlotStatus.BOOKED,
            bookedByUserId = userId,
            bookedByUserName = userName,
            bookedAt = System.currentTimeMillis(),
            studentMessage = message
        )
        return true
    }
    
    fun cancelBooking(slotId: String): Boolean {
        val slot = timeSlots[slotId] ?: return false
        timeSlots[slotId] = slot.copy(
            status = SlotStatus.CANCELLED,
            bookedByUserId = "",
            bookedByUserName = "",
            bookedAt = 0,
            studentMessage = ""
        )
        return true
    }
    
    // Video session operations
    fun saveVideoSession(session: VideoCallSession) {
        videoSessions[session.id] = session
    }
    
    fun getVideoSession(id: String): VideoCallSession? = videoSessions[id]
    
    // Chat operations
    fun saveMessage(conversationId: String, message: ChatMessage) {
        messages.getOrPut(conversationId) { mutableListOf() }.add(message)
        updateChatThreads(conversationId, message)
    }
    
    fun getMessages(conversationId: String): List<ChatMessage> =
        messages[conversationId]?.toList() ?: emptyList()
    
    fun getChatThreads(userId: String): List<ChatThread> =
        chatThreads[userId]?.toList() ?: emptyList()
    
    private fun updateChatThreads(conversationId: String, message: ChatMessage) {
        // Update sender's thread
        val senderThreads = chatThreads.getOrPut(message.senderId) { mutableListOf() }
        val senderThreadIndex = senderThreads.indexOfFirst { it.conversationId == conversationId }
        val senderThread = ChatThread(
            conversationId = conversationId,
            otherUserId = message.receiverId,
            otherUserName = users[message.receiverId]?.name ?: "Unknown",
            lastMessage = message.message,
            lastMessageTimestamp = message.timestamp,
            unreadCount = 0
        )
        if (senderThreadIndex >= 0) {
            senderThreads[senderThreadIndex] = senderThread
        } else {
            senderThreads.add(senderThread)
        }
        
        // Update receiver's thread
        val receiverThreads = chatThreads.getOrPut(message.receiverId) { mutableListOf() }
        val receiverThreadIndex = receiverThreads.indexOfFirst { it.conversationId == conversationId }
        val receiverThread = ChatThread(
            conversationId = conversationId,
            otherUserId = message.senderId,
            otherUserName = message.senderName,
            lastMessage = message.message,
            lastMessageTimestamp = message.timestamp,
            unreadCount = receiverThreads.getOrNull(receiverThreadIndex)?.unreadCount?.plus(1) ?: 1
        )
        if (receiverThreadIndex >= 0) {
            receiverThreads[receiverThreadIndex] = receiverThread
        } else {
            receiverThreads.add(receiverThread)
        }
    }
    
    // Clear all data
    fun clearAll() {
        users.clear()
        mentors.clear()
        mentorProfiles.clear()
        timeSlots.clear()
        videoSessions.clear()
        messages.clear()
        chatThreads.clear()
        currentUserId = null
        currentUserEmail = null
        seedInitialData()
    }
    
    // Seed initial test data
    private fun seedInitialData() {
        // Create test users
        val janeUser = UserProfile(
            uid = "mentor_jane_ds",
            name = "Dr. Jane Patel",
            email = "jane@example.com",
            education = "M.S. Data Science",
            interest = "Career coaching",
            mentorExpertise = "Data Science",
            mentorExperience = "7 years",
            mentorRate = "1800",
            role = "MENTOR",
            profileCompleted = true
        )
        
        val rahulUser = UserProfile(
            uid = "mentor_rahul_ui",
            name = "Rahul Mehta",
            email = "rahul@example.com",
            education = "B.Des Interaction Design",
            interest = "Design leadership",
            mentorExpertise = "UI/UX Design",
            mentorExperience = "9 years",
            mentorRate = "1500",
            role = "MENTOR",
            profileCompleted = true
        )
        
        val steveUser = UserProfile(
            uid = "student_steve",
            name = "Steve Lee",
            email = "steve@example.com",
            education = "B.Tech CSE",
            interest = "Product management",
            role = "STUDENT",
            profileCompleted = true
        )
        
        val ayeshaUser = UserProfile(
            uid = "student_ayesha",
            name = "Ayesha Khan",
            email = "ayesha@example.com",
            education = "MCA",
            interest = "UI/UX",
            role = "STUDENT",
            profileCompleted = true
        )
        
        saveUser(janeUser)
        saveUser(rahulUser)
        saveUser(steveUser)
        saveUser(ayeshaUser)
        
        // Create mentors  
        val janeMentor = Mentor(
            id = "mentor_jane_ds",
            name = "Dr. Jane Patel",
            expertise = "Data Science",
            bio = "Former Google data scientist mentoring grads.",
            experience = "7 years",
            rating = 4.8,
            hourlyRate = 1800,
            skills = listOf("Python", "TensorFlow", "MLOps")
        )
        
        val rahulMentor = Mentor(
            id = "mentor_rahul_ui",
            name = "Rahul Mehta",
            expertise = "UI/UX Design",
            bio = "Lead product designer helping engineers level up UI craft.",
            experience = "9 years",
            rating = 4.6,
            hourlyRate = 1500,
            skills = listOf("Figma", "Design Systems", "Accessibility")
        )
        
        saveMentor(janeMentor)
        saveMentor(rahulMentor)
        
        // Create time slots for Jane (Nov 27-28, 2025)
        saveTimeSlot(TimeSlot(
            id = "slot_jane_2025-11-27-0900",
            mentorId = "mentor_jane_ds",
            date = "2025-11-27",
            startTime = "09:00 AM",
            endTime = "09:30 AM",
            status = SlotStatus.AVAILABLE,
            price = 1800.0
        ))
        
        saveTimeSlot(TimeSlot(
            id = "slot_jane_2025-11-27-1000",
            mentorId = "mentor_jane_ds",
            date = "2025-11-27",
            startTime = "10:00 AM",
            endTime = "10:30 AM",
            status = SlotStatus.BOOKED,
            price = 1800.0,
            bookedByUserId = "student_steve",
            bookedByUserName = "Steve Lee",
            bookedAt = System.currentTimeMillis() - 86400000,
            studentMessage = "Resume review"
        ))
        
        saveTimeSlot(TimeSlot(
            id = "slot_jane_2025-11-28-1830",
            mentorId = "mentor_jane_ds",
            date = "2025-11-28",
            startTime = "06:30 PM",
            endTime = "07:00 PM",
            status = SlotStatus.AVAILABLE,
            price = 1800.0
        ))
        
        // Create time slots for Rahul
        saveTimeSlot(TimeSlot(
            id = "slot_rahul_2025-11-27-1500",
            mentorId = "mentor_rahul_ui",
            date = "2025-11-27",
            startTime = "03:00 PM",
            endTime = "03:45 PM",
            status = SlotStatus.AVAILABLE,
            price = 1500.0
        ))
        
        saveTimeSlot(TimeSlot(
            id = "slot_rahul_2025-11-28-1100",
            mentorId = "mentor_rahul_ui",
            date = "2025-11-28",
            startTime = "11:00 AM",
            endTime = "11:45 AM",
            status = SlotStatus.BOOKED,
            price = 1500.0,
            bookedByUserId = "student_ayesha",
            bookedByUserName = "Ayesha Khan",
            bookedAt = System.currentTimeMillis() - 43200000,
            studentMessage = "Portfolio critique"
        ))
        
        // Seed one conversation
        val msg1 = ChatMessage(
            id = "msg1",
            conversationId = "mentor_jane_ds_student_steve",
            senderId = "student_steve",
            senderName = "Steve Lee",
            receiverId = "mentor_jane_ds",
            message = "Hi Dr. Jane, excited for the session!",
            timestamp = System.currentTimeMillis() - 7200000
        )
        
        val msg2 = ChatMessage(
            id = "msg2",
            conversationId = "mentor_jane_ds_student_steve",
            senderId = "mentor_jane_ds",
            senderName = "Dr. Jane Patel",
            receiverId = "student_steve",
            message = "Looking forward to it. Bring your latest resume.",
            timestamp = System.currentTimeMillis() - 3600000
        )
        
        saveMessage("mentor_jane_ds_student_steve", msg1)
        saveMessage("mentor_jane_ds_student_steve", msg2)
    }
}
