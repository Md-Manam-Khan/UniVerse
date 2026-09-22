package com.universe.app;
import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class FirebaseManager 
{
    private static final String DB_URL = "https://baustkhulnaapp-default-rtdb.firebaseio.com/";
    private DatabaseReference rootRef;
    public FirebaseManager() 
    {
        rootRef = FirebaseDatabase.getInstance(DB_URL).getReference();
    }
    public interface NoticeListener 
    {
        void onNoticeChanged(String noticeText);
    }
    public void postNotice(String message, String date, long timestamp) 
    {
        DatabaseReference noticeRef = rootRef.child("notices").push();
        Map<String, Object> notice = new HashMap<>();
        notice.put("message", message);
        notice.put("date", date);
        notice.put("timestamp", timestamp);
        noticeRef.setValue(notice);
    }
    public void listenForLatestNotice(final NoticeListener listener) 
    {
        rootRef.child("notices").addValueEventListener(new ValueEventListener() 
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) 
            {
                List<long[]> order = new ArrayList<>();
                List<String> messages = new ArrayList<>();
                List<String> dates = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) 
                {
                    Long timestamp = child.child("timestamp").getValue(Long.class);
                    String message = child.child("message").getValue(String.class);
                    String date = child.child("date").getValue(String.class);
                    if (timestamp != null && message != null) 
                    {
                        messages.add(message);
                        dates.add(date);
                        order.add(new long[]{timestamp, messages.size() - 1});
                    }
                }
                order.sort((a, b) -> Long.compare(b[0], a[0]));
                if (order.isEmpty()) 
                {
                    listener.onNoticeChanged("No notices yet.");
                    return;
                }
                StringBuilder builder = new StringBuilder();
                int limit = Math.min(3, order.size());
                for (int i = 0; i < limit; i++) 
                {
                    int index = (int) order.get(i)[1];
                    if (i > 0) 
                    {
                        builder.append("\n\n");
                    }
                    builder.append(dates.get(index)).append(": ").append(messages.get(index));
                }
                listener.onNoticeChanged(builder.toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) 
            {
                listener.onNoticeChanged("No notices yet.");
            }
        });
    }
    public void saveProfile(String name, String password, String story, String department, String userType, String designation, String semester, long id) 
    {
        DatabaseReference profileRef = rootRef.child("profiles").child(String.valueOf(id));
        Map<String, Object> profile = new HashMap<>();
        profile.put("name", name);
        profile.put("password", password);
        profile.put("story", story);
        profile.put("department", department);
        profile.put("userType", userType);
        profile.put("designation", designation);
        profile.put("semester", semester);
        profile.put("id", id);
        profileRef.setValue(profile);
    }
    public void updateProfileInfo(long id, String name, String story) 
    {
        DatabaseReference profileRef = rootRef.child("profiles").child(String.valueOf(id));
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("story", story);
        profileRef.updateChildren(updates);
    }
    public void updateProfilePassword(long id, String newPassword) 
    {
        rootRef.child("profiles").child(String.valueOf(id)).child("password").setValue(newPassword);
    }
    public interface ProfilesListener 
    {
        void onResult(List<Map<String, String>> profiles);
        void onError();
    }
    public void fetchAllProfiles(final ProfilesListener listener) 
    {
        rootRef.child("profiles").addListenerForSingleValueEvent(new ValueEventListener() 
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) 
            {
                List<Map<String, String>> profiles = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) 
                {
                    Map<String, String> profile = new HashMap<>();
                    profile.put("name", stringOrEmpty(child.child("name").getValue()));
                    profile.put("password", stringOrEmpty(child.child("password").getValue()));
                    profile.put("story", stringOrEmpty(child.child("story").getValue()));
                    profile.put("department", stringOrEmpty(child.child("department").getValue()));
                    profile.put("userType", stringOrEmpty(child.child("userType").getValue()));
                    profile.put("designation", stringOrEmpty(child.child("designation").getValue()));
                    profile.put("semester", stringOrEmpty(child.child("semester").getValue()));
                    profile.put("id", child.getKey());
                    profiles.add(profile);
                }
                listener.onResult(profiles);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) 
            {
                listener.onError();
            }
        });
    }
    private String stringOrEmpty(Object value) 
    {
        return value == null ? "" : String.valueOf(value);
    }
    public interface AdminCredentialsListener 
    {
        void onResult(String name, String password);
        void onError();
    }
    public void getAdminCredentials(final AdminCredentialsListener listener) 
    {
        rootRef.child("admin").addListenerForSingleValueEvent(new ValueEventListener() 
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) 
            {
                String name = snapshot.child("name").getValue(String.class);
                String password = snapshot.child("password").getValue(String.class);
                listener.onResult(name, password);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) 
            {
                listener.onError();
            }
        });
    }
    public void updateAdminCredentials(String name, String password) 
    {
        Map<String, Object> admin = new HashMap<>();
        admin.put("name", name);
        admin.put("password", password);
        rootRef.child("admin").setValue(admin);
    }
    public void wipeCloudData() 
    {
        rootRef.child("notices").removeValue();
    }
    public interface IncomingMessageListener 
    {
        void onResult(String senderName, String messageText);
    }
    public void listenForLatestIncomingMessage(String myProfileKey, String myName, final IncomingMessageListener listener) 
    {
        rootRef.child("messages").addValueEventListener(new ValueEventListener() 
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) 
            {
                String latestSender = null;
                String latestMessage = null;
                long latestTimestamp = -1;
                for (DataSnapshot conversation : snapshot.getChildren()) 
                {
                    String conversationKey = conversation.getKey();
                    if (conversationKey == null || !conversationKey.contains(myProfileKey)) 
                    {
                        continue;
                    }
                    for (DataSnapshot messageSnap : conversation.getChildren()) 
                    {
                        String sender = messageSnap.child("sender").getValue(String.class);
                        Long timestamp = messageSnap.child("timestamp").getValue(Long.class);
                        String message = messageSnap.child("message").getValue(String.class);
                        if (sender != null && !sender.equals(myName) && timestamp != null && timestamp > latestTimestamp) 
                        {
                            latestTimestamp = timestamp;
                            latestSender = sender;
                            latestMessage = message;
                        }
                    }
                }
                listener.onResult(latestSender, latestMessage);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) 
            {
                listener.onResult(null, null);
            }
        });
    }
    public interface MessageListener 
    {
        void onMessagesChanged(List<Map<String, String>> messages);
    }
    public void sendMessage(String conversationKey, String sender, String message) 
    {
        DatabaseReference msgRef = rootRef.child("messages").child(conversationKey).push();
        Map<String, Object> msg = new HashMap<>();
        msg.put("sender", sender);
        msg.put("message", message);
        msg.put("timestamp", System.currentTimeMillis());
        msgRef.setValue(msg);
    }
    public void listenForMessages(String conversationKey, final MessageListener listener) 
    {
        rootRef.child("messages").child(conversationKey).addValueEventListener(new ValueEventListener() 
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) 
            {
                List<Map<String, String>> messages = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) 
                {
                    Map<String, String> msg = new HashMap<>();
                    msg.put("sender", String.valueOf(child.child("sender").getValue()));
                    msg.put("message", String.valueOf(child.child("message").getValue()));
                    messages.add(msg);
                }
                listener.onMessagesChanged(messages);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) 
            {
            }
        });
    }
}