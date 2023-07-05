package main;

import main.dto.DtoMessage;
import main.dto.MessageMapper;
import main.model.Message;
import main.model.MessageRepository;
import main.model.User;
import main.model.UserRepository;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class ChatController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;

    @GetMapping("/init")
    public Map<String, Boolean> init() {
        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        Optional<User> userOpt = userRepository.findBySessionId(sessionId);
        return Map.of("result", userOpt.isPresent());
    }

    @PostMapping("/auth")
    public Map<String, Boolean> auth(@RequestParam String name) {
        if (!Strings.isNotEmpty(name)) {
            return Map.of("result", false);
        }
        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        User user = new User();
        user.setName(name);
        user.setSessionId(sessionId);
        userRepository.save(user);
        return Map.of("result", true);
    }

    @PostMapping("/message")
    public Map<String, Boolean> sendMessage(@RequestParam String message) {
        if (Strings.isEmpty(message)) {
            return Map.of("result", false);
        }

        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        User user = userRepository.findBySessionId(sessionId).get();
        Message msg = new Message();
        msg.setMessage(message);
        msg.setDateTime(LocalDateTime.now());
        msg.setUser(user);
        messageRepository.save(msg);
        return Map.of("result", true);
    }

    @GetMapping("/message")
    public List<DtoMessage> getMessagesList() {
        return messageRepository
                .findAll(Sort.by(Sort.Direction.ASC, "dateTime"))
                .stream()
                .map(MessageMapper::map)
                .collect(Collectors.toList());
    }

    @GetMapping("/user")
    public HashMap<Integer, String> getUsersList() {
        return null;
    }
}
