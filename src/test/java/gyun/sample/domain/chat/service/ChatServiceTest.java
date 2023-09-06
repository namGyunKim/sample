package gyun.sample.domain.chat.service;

import org.aspectj.lang.annotation.After;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class ChatServiceTest {

    @BeforeEach
    void setUp() {
        System.out.println("setUp");
    }

    @AfterEach
    void tearDown() {
        System.out.println("tearDown");
    }
    @Test
    void send() {

    }

    @Test
    void getChatList() {
    }

    @Test
    void delete() {
    }
}