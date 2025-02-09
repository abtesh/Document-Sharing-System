package com.LIB.MessagingSystem.Controller;

import com.LIB.MessagingSystem.Dto.MessageRequest;
import com.LIB.MessagingSystem.Dto.MessageSearchRequestDto;
import com.LIB.MessagingSystem.Model.Message;
import com.LIB.MessagingSystem.Model.Users;
import com.LIB.MessagingSystem.Repository.UserRepository;
import com.LIB.MessagingSystem.Service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 *
 *  @author Abenezer Teshome  - Date 17/aug/2024
 *  Controller class to create Messages, get Messages and Fetch Messages between Users
 */


@RestController
@RequestMapping("/messenger")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageServiceImpl;
    private final UserRepository userRepository;


    @PostMapping("/createUser")
    public String createUser(@RequestBody Users user) {
        userRepository.save(user);
        return "User created";
    }

    @PostMapping("/create")
    public Message createMessage(@ModelAttribute MessageRequest message) {
        System.out.println("Received message request: " + message);
        return messageServiceImpl.createMessage(
                message.getReceiverEmail(),
                message.getContent(),
                message.getAttachments());
    }

    @PostMapping("/send/{objectId}")
    public ResponseEntity<?> sendMessage(@PathVariable("objectId") String objectId) {
        try {
            Message sentMessage = messageServiceImpl.sendMessage(objectId);
            return ResponseEntity.ok(sentMessage);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<Message> getMessage(@PathVariable("id") String id) {
        Message message = messageServiceImpl.getMessage(id);
        return ResponseEntity.ok(message);
    }
    @GetMapping("/{senderId}/{receiverId}")
    public List<Message> getMessageBetweenUsers(@PathVariable("senderId") String senderId, @PathVariable("receiverId") String receiverId) {
        return messageServiceImpl.getMessagesBetweenUsers(senderId, receiverId);
    }

    @GetMapping("/inbox/{receiverId}")
    public List<Message> getInboxMessages() {
        return messageServiceImpl.getMessagesForUser();
    }

    @GetMapping("/search")
    public ResponseEntity<Message> getMessageById(@RequestBody MessageSearchRequestDto searchRequest) {
        Message message = messageServiceImpl.findMessageByIdForUser(
                searchRequest.getMessageId(),
                searchRequest.getUsername()
        );
        return ResponseEntity.ok(message);
    }
    @GetMapping("/{messageId}/attachments/{attachmentId}")
    public Message getMessageWithAttachmentCheck(@PathVariable String messageId,
                                                 @PathVariable String attachmentId,
                                                 @RequestParam String userId) {
        return messageServiceImpl.getMessageWithAttachmentCheck(messageId, attachmentId, userId);

    }
    @DeleteMapping("/delete/{messageId}")
    public String deleteMessage(@PathVariable("messageId") String messageId) {
        messageServiceImpl.deleteMessageById(messageId);
        return "Message deleted";
    }
}
